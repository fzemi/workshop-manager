<script setup>
import { ref, computed, watch } from 'vue';
import DocumentService from '@/service/DocumentService.js';
import WMDocumentRenderer from './WMDocumentRenderer.vue';
import { useToast } from 'primevue/usetoast';

const props = defineProps({
    visible: {
        type: Boolean,
        default: false,
    },
    /**
     * Array of template names to generate
     */
    templates: {
        type: Array,
        default: () => [],
    },
    /**
     * Initial document data from repair/client/vehicle
     */
    initialData: {
        type: Object,
        required: true,
    },
});

const emit = defineEmits(['update:visible', 'complete']);

const toast = useToast();
const loading = ref(false);
const currentIndex = ref(0);
const templateHtmlList = ref([]);
const documentDataList = ref([]);
const documentRendererRef = ref(null);

// Template display names for header
const templateDisplayNames = {
    'RepairOrder': 'Zlecenie naprawy',
    'VatDeclaration': 'Oświadczenie VAT',
};

const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value),
});

const currentTemplateName = computed(() => {
    return props.templates[currentIndex.value] || '';
});

const currentTemplateDisplayName = computed(() => {
    return templateDisplayNames[currentTemplateName.value] || currentTemplateName.value;
});

const currentTemplateHtml = computed(() => {
    return templateHtmlList.value[currentIndex.value] || '';
});

const currentDocumentData = computed({
    get: () => documentDataList.value[currentIndex.value] || {},
    set: (value) => {
        documentDataList.value[currentIndex.value] = value;
    },
});

const isFirstDocument = computed(() => currentIndex.value === 0);
const isLastDocument = computed(() => currentIndex.value === props.templates.length - 1);

const dialogHeader = computed(() => {
    const total = props.templates.length;
    if (total === 1) {
        return `Podgląd dokumentu - ${currentTemplateDisplayName.value}`;
    }
    return `Podgląd dokumentu - ${currentTemplateDisplayName.value} (${currentIndex.value + 1}/${total})`;
});

/**
 * Load all template HTML content
 */
async function loadTemplates() {
    if (props.templates.length === 0) return;
    
    loading.value = true;
    try {
        const htmlPromises = props.templates.map(name => 
            DocumentService.getTemplateContent(name)
        );
        templateHtmlList.value = await Promise.all(htmlPromises);
        
        // Initialize document data for each template with a deep copy of initial data
        // Use JSON parse/stringify to ensure deep copy of the object
        documentDataList.value = props.templates.map(() => JSON.parse(JSON.stringify(props.initialData)));
    } catch (error) {
        console.error('Failed to load templates:', error);
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać szablonów dokumentów',
            life: 5000,
        });
        dialogVisible.value = false;
    } finally {
        loading.value = false;
    }
}

/**
 * Navigate to previous document
 */
function handlePrevious() {
    if (currentIndex.value > 0) {
        currentIndex.value--;
    }
}

/**
 * Navigate to next document
 */
function handleNext() {
    if (currentIndex.value < props.templates.length - 1) {
        currentIndex.value++;
    }
}

/**
 * Print current document
 */
function handlePrint() {
    if (!documentRendererRef.value) return;
    
    // Get the rendered HTML content
    const htmlContent = documentRendererRef.value.getHtmlForPrint();
    
    // Create print window with full document structure
    const printWindow = window.open('', '_blank', 'width=800,height=600');
    if (!printWindow) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się otworzyć okna drukowania. Sprawdź czy blokowanie wyskakujących okienek jest wyłączone.',
            life: 5000,
        });
        return;
    }
    
    // Build full HTML document for printing
    const fullHtml = `
<!DOCTYPE html>
<html lang="pl">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${currentTemplateDisplayName.value}</title>
    <style>
        @media print {
            @page {
                size: A4;
                margin: 0;
            }
            body {
                margin: 0;
                padding: 0;
            }
        }
        
        /* Remove editable field styling for print */
        .document-editable-field {
            border: none !important;
            background: transparent !important;
            padding: 0 !important;
        }
    </style>
</head>
<body>
    ${htmlContent}
</body>
</html>
    `;
    
    printWindow.document.write(fullHtml);
    printWindow.document.close();
    
    // Wait for images to load, then print
    printWindow.onload = () => {
        setTimeout(() => {
            printWindow.print();
        }, 250);
    };
}

/**
 * Close dialog and emit complete
 */
function handleComplete() {
    emit('complete');
    dialogVisible.value = false;
}

/**
 * Handle cancel
 */
function handleCancel() {
    dialogVisible.value = false;
}

// Watch for visibility changes to load templates
watch(() => props.visible, (newValue) => {
    if (newValue && props.templates.length > 0) {
        currentIndex.value = 0;
        loadTemplates();
    }
});

// Watch for templates changes
watch(() => props.templates, (newTemplates) => {
    if (props.visible && newTemplates.length > 0) {
        currentIndex.value = 0;
        loadTemplates();
    }
});
</script>

<template>
    <Dialog
        v-model:visible="dialogVisible"
        modal
        :blockScroll="true"
        :header="dialogHeader"
        :style="{ width: '900px', maxHeight: '90vh' }"
        :breakpoints="{ '960px': '95vw' }"
        :contentStyle="{ overflow: 'hidden', display: 'flex', flexDirection: 'column' }"
    >
        <!-- Loading state -->
        <div v-if="loading" class="flex flex-col items-center justify-center py-12 gap-4">
            <ProgressSpinner style="width: 50px; height: 50px" />
            <span class="text-surface-500">Ładowanie dokumentu...</span>
        </div>
        
        <!-- Document preview -->
        <div v-else class="flex flex-col gap-4 overflow-hidden flex-1">
            <!-- Document container with scroll -->
            <div class="document-container flex-1 overflow-auto border border-surface-200 dark:border-surface-700 rounded-lg bg-white">
                <WMDocumentRenderer
                    v-if="currentTemplateHtml"
                    ref="documentRendererRef"
                    :template-html="currentTemplateHtml"
                    v-model="currentDocumentData"
                />
            </div>
            
            <!-- Instructions -->
            <div class="text-sm text-surface-500 dark:text-surface-400 text-center">
                <i class="pi pi-info-circle mr-1"></i>
                Kliknij w pola, aby edytować. Kliknij w opcje jestem/nie jestem, aby przełączyć.
            </div>
        </div>
        
        <template #footer>
            <div class="flex justify-between items-center w-full">
                <!-- Left side: navigation -->
                <div class="flex gap-2">
                    <Button
                        v-if="templates.length > 1"
                        label="Wstecz"
                        icon="pi pi-arrow-left"
                        severity="secondary"
                        :disabled="isFirstDocument"
                        @click="handlePrevious"
                    />
                </div>
                
                <!-- Right side: actions -->
                <div class="flex gap-2">
                    <Button
                        label="Anuluj"
                        severity="secondary"
                        @click="handleCancel"
                    />
                    <Button
                        label="Drukuj"
                        icon="pi pi-print"
                        @click="handlePrint"
                    />
                    <Button
                        v-if="templates.length > 1 && !isLastDocument"
                        label="Dalej"
                        icon="pi pi-arrow-right"
                        iconPos="right"
                        @click="handleNext"
                    />
                    <Button
                        v-if="isLastDocument"
                        label="Zakończ"
                        icon="pi pi-check"
                        severity="success"
                        @click="handleComplete"
                    />
                </div>
            </div>
        </template>
    </Dialog>
</template>

<style scoped>
/* Ensure proper dialog sizing */
:deep(.p-dialog-content) {
    min-height: 400px;
}

.document-container {
    max-height: 80vh;
}
</style>
