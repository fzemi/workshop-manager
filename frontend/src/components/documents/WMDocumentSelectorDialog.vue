<script setup>
import { ref, computed, onMounted } from 'vue';
import DocumentService from '@/service/DocumentService.js';
import { useToast } from 'primevue/usetoast';

const props = defineProps({
    visible: {
        type: Boolean,
        default: false,
    },
});

const emit = defineEmits(['update:visible', 'select']);

const toast = useToast();
const loading = ref(false);
const templates = ref([]);
const selectedTemplates = ref([]);

const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value),
});

const canGenerate = computed(() => selectedTemplates.value.length > 0);

/**
 * Load available templates from backend
 */
async function loadTemplates() {
    loading.value = true;
    try {
        templates.value = await DocumentService.getAvailableTemplates();
        // Pre-select all templates by default
        selectedTemplates.value = templates.value.map(t => t.name);
    } catch (error) {
        console.error('Failed to load templates:', error);
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać listy szablonów',
            life: 5000,
        });
    } finally {
        loading.value = false;
    }
}

/**
 * Toggle template selection
 */
function toggleTemplate(templateName) {
    const index = selectedTemplates.value.indexOf(templateName);
    if (index === -1) {
        selectedTemplates.value.push(templateName);
    } else {
        selectedTemplates.value.splice(index, 1);
    }
}

/**
 * Check if template is selected
 */
function isSelected(templateName) {
    return selectedTemplates.value.includes(templateName);
}

/**
 * Handle generate button click
 */
function handleGenerate() {
    if (selectedTemplates.value.length === 0) return;
    
    emit('select', [...selectedTemplates.value]);
    dialogVisible.value = false;
}

/**
 * Handle cancel button click
 */
function handleCancel() {
    dialogVisible.value = false;
}

onMounted(() => {
    if (props.visible) {
        loadTemplates();
    }
});

// Reload templates when dialog becomes visible
import { watch } from 'vue';
watch(() => props.visible, (newValue) => {
    if (newValue) {
        loadTemplates();
    }
});
</script>

<template>
    <Dialog
        v-model:visible="dialogVisible"
        modal
        :blockScroll="true"
        header="Generuj dokumenty"
        :style="{ width: '500px' }"
        :breakpoints="{ '575px': '90vw' }"
    >
        <div class="flex flex-col gap-4">
            <p class="text-surface-600 dark:text-surface-400 m-0">
                Wybierz dokumenty do wygenerowania:
            </p>
            
            <!-- Loading state -->
            <div v-if="loading" class="flex flex-col gap-3">
                <Skeleton height="4rem" />
                <Skeleton height="4rem" />
            </div>
            
            <!-- Templates list -->
            <div v-else class="flex flex-col gap-2">
                <div
                    v-for="template in templates"
                    :key="template.name"
                    class="flex items-start gap-3 p-3 rounded-lg border border-surface-200 dark:border-surface-700 cursor-pointer hover:bg-surface-50 dark:hover:bg-surface-800 transition-colors"
                    :class="{ 'bg-primary-50 dark:bg-primary-900/20 border-primary-300 dark:border-primary-700': isSelected(template.name) }"
                    @click="toggleTemplate(template.name)"
                >
                    <Checkbox
                        :modelValue="isSelected(template.name)"
                        :binary="true"
                        @click.stop
                        @update:modelValue="toggleTemplate(template.name)"
                    />
                    <div class="flex flex-col gap-1">
                        <span class="font-medium">{{ template.displayName }}</span>
                        <span class="text-sm text-surface-500 dark:text-surface-400">
                            {{ template.description }}
                        </span>
                    </div>
                </div>
                
                <!-- Empty state -->
                <div v-if="templates.length === 0 && !loading" class="text-center py-4 text-surface-500">
                    Brak dostępnych szablonów
                </div>
            </div>
        </div>
        
        <template #footer>
            <div class="flex justify-end gap-2">
                <Button
                    label="Anuluj"
                    severity="secondary"
                    @click="handleCancel"
                />
                <Button
                    label="Generuj"
                    icon="pi pi-arrow-right"
                    iconPos="right"
                    :disabled="!canGenerate"
                    @click="handleGenerate"
                />
            </div>
        </template>
    </Dialog>
</template>
