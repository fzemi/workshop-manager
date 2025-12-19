<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import RepairService from '@/service/RepairService.js';
import FileService from '@/service/FileService.js';
import { formatDate } from '@/libs/dateUtils.js';
import { getRepairTypeLabel } from '@/libs/constants.js';
import { mapRepairToDocumentData } from '@/libs/documentDataMapper.js';
import WMFileGallery from '@/components/shared/WMFileGallery.vue';
import WMFileUploadDialog from '@/components/shared/WMFileUploadDialog.vue';
import WMConfirmDialog from '@/components/shared/WMConfirmDialog.vue';
import WMDocumentSelectorDialog from '@/components/documents/WMDocumentSelectorDialog.vue';
import WMDocumentPreviewDialog from '@/components/documents/WMDocumentPreviewDialog.vue';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const repair = ref(null);
const loading = ref(true);

// File management state
const files = ref([]);
const filesLoading = ref(false);
const uploadDialogVisible = ref(false);
const deleteDialog = ref(null);
const fileToDelete = ref(null);

// Document generation state
const documentSelectorVisible = ref(false);
const documentPreviewVisible = ref(false);
const selectedTemplates = ref([]);

const repairId = computed(() => route.params.id);

const clientsList = computed(() => {
    // Clients come from top-level repair.clients (from RepairWithClientsDTO)
    return repair.value?.clients?.map(c => ({
        id: c.id,
        label: `${c.firstname} ${c.surname}`,
    })) || [];
});

/**
 * Document data mapped from repair, first client, and vehicle
 */
const documentData = computed(() => {
    if (!repair.value) return {};
    
    const client = repair.value.clients?.[0] || null;
    const vehicle = repair.value.vehicle || null;
    
    return mapRepairToDocumentData(repair.value, client, vehicle);
});

/**
 * Check if document generation is available (requires vehicle and at least one client)
 */
const canGenerateDocuments = computed(() => {
    return repair.value?.vehicle && repair.value?.clients?.length > 0;
});

onMounted(async () => {
    await loadRepair();
    await loadFiles();
});

async function loadRepair() {
    loading.value = true;
    try {
        repair.value = await RepairService.getByIdWithClients(repairId.value);
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać danych naprawy',
            life: 3000,
        });
        router.push('/repairs');
    } finally {
        loading.value = false;
    }
}

async function loadFiles() {
    filesLoading.value = true;
    try {
        files.value = await FileService.getByRepairId(repairId.value);
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać listy załączników',
            life: 3000,
        });
    } finally {
        filesLoading.value = false;
    }
}

/**
 * Open file in new browser tab as blob
 */
async function openFile(file) {
    try {
        const blob = await FileService.download(repairId.value, file.id);
        const url = URL.createObjectURL(blob);
        window.open(url, '_blank');
        // Note: We don't revoke the URL immediately as the new tab needs it
        // Browser will handle cleanup when tab is closed
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się otworzyć pliku',
            life: 3000,
        });
    }
}

/**
 * Download file to local drive
 */
async function downloadFile(file) {
    try {
        const blob = await FileService.download(repairId.value, file.id);
        const url = URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = file.filename;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
        
        toast.add({
            severity: 'success',
            summary: 'Sukces',
            detail: 'Plik został pobrany',
            life: 3000,
        });
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać pliku',
            life: 3000,
        });
    }
}

/**
 * Show delete confirmation dialog
 */
function confirmDeleteFile(file) {
    fileToDelete.value = file;
    deleteDialog.value.show(file);
}

/**
 * Delete file after confirmation
 */
async function deleteFile(file) {
    try {
        await FileService.delete(repairId.value, file.id);
        toast.add({
            severity: 'success',
            summary: 'Sukces',
            detail: 'Plik został usunięty',
            life: 3000,
        });
        await loadFiles();
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się usunąć pliku',
            life: 3000,
        });
    }
}

/**
 * Handle successful file upload
 */
function onFilesUploaded() {
    loadFiles();
}

function goBack() {
    router.push('/repairs');
}

function goToEdit() {
    router.push(`/repairs/${repairId.value}/edit`);
}

/**
 * Open document selector dialog
 */
function openDocumentGenerator() {
    documentSelectorVisible.value = true;
}

/**
 * Handle template selection from selector dialog
 */
function onTemplatesSelected(templates) {
    selectedTemplates.value = templates;
    documentPreviewVisible.value = true;
}
</script>

<template>
    <div>
        <div class="flex items-center gap-4 mb-6">
            <Button
                icon="pi pi-arrow-left"
                severity="secondary"
                text
                rounded
                @click="goBack"
            />
            <h1 class="text-2xl font-semibold text-surface-800 dark:text-surface-100">
                Szczegóły naprawy
            </h1>
            <div class="ml-auto flex gap-2">
                <Button
                    label="Generuj dokumenty"
                    icon="pi pi-file-export"
                    severity="secondary"
                    :disabled="!canGenerateDocuments"
                    @click="openDocumentGenerator"
                />
                <Button
                    label="Edytuj"
                    icon="pi pi-pencil"
                    @click="goToEdit"
                />
            </div>
        </div>

        <div v-if="loading" class="flex justify-center py-8">
            <ProgressSpinner />
        </div>

        <div v-else-if="repair" class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Dane naprawy -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-wrench"></i>
                        Dane naprawy
                    </div>
                </template>
                <template #content>
                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Numer naprawy</label>
                            <p class="font-medium font-mono">{{ repair.number || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Typ naprawy</label>
                            <p class="font-medium">
                                <Tag 
                                    :value="getRepairTypeLabel(repair.type)" 
                                    :severity="repair.type === 'BODY' ? 'info' : 'success'" 
                                />
                            </p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Data rozpoczęcia</label>
                            <p class="font-medium">{{ formatDate(repair.startDate) || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Przewidywane zakończenie</label>
                            <p class="font-medium">{{ formatDate(repair.expectedEndDate) || '—' }}</p>
                        </div>
                    </div>
                </template>
            </Card>

            <!-- Pojazd -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-car"></i>
                        Pojazd
                    </div>
                </template>
                <template #content>
                    <div v-if="repair.vehicle" class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Marka i model</label>
                            <p class="font-medium">
                                <WMEntityLink
                                    :id="repair.vehicle.id"
                                    :label="`${repair.vehicle.manufacturer} ${repair.vehicle.model}`"
                                    entityType="vehicle"
                                />
                            </p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Nr rejestracyjny</label>
                            <p class="font-medium">{{ repair.vehicle.licencePlate || '—' }}</p>
                        </div>
                        <div class="col-span-2">
                            <label class="text-sm text-surface-500">VIN</label>
                            <p class="font-medium font-mono text-sm">{{ repair.vehicle.vin || '—' }}</p>
                        </div>
                    </div>
                    <p v-else class="text-surface-500">Brak przypisanego pojazdu</p>
                </template>
            </Card>

            <!-- Klienci -->
            <Card class="md:col-span-2">
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-users"></i>
                        Klienci
                    </div>
                </template>
                <template #content>
                    <div v-if="clientsList.length > 0" class="flex flex-wrap gap-3">
                        <div 
                            v-for="client in clientsList" 
                            :key="client.id"
                            class="flex items-center gap-2 bg-surface-100 dark:bg-surface-700 rounded-lg px-3 py-2"
                        >
                            <i class="pi pi-user text-surface-500"></i>
                            <WMEntityLink
                                :id="client.id"
                                :label="client.label"
                                entityType="client"
                            />
                        </div>
                    </div>
                    <p v-else class="text-surface-500">Brak przypisanych klientów do pojazdu</p>
                </template>
            </Card>

            <!-- Załączniki -->
            <Card class="md:col-span-2">
                <template #title>
                    <div class="flex items-center justify-between">
                        <div class="flex items-center gap-2">
                            <i class="pi pi-file"></i>
                            Załączniki
                        </div>
                        <Button
                            label="Dodaj pliki"
                            icon="pi pi-plus"
                            size="small"
                            @click="uploadDialogVisible = true"
                        />
                    </div>
                </template>
                <template #content>
                    <WMFileGallery
                        :files="files"
                        :loading="filesLoading"
                        :repairId="repairId"
                        @open="openFile"
                        @download="downloadFile"
                        @delete="confirmDeleteFile"
                    />
                </template>
            </Card>
        </div>

        <!-- File Upload Dialog -->
        <WMFileUploadDialog
            v-model:visible="uploadDialogVisible"
            :repairId="repairId"
            @uploaded="onFilesUploaded"
        />

        <!-- Delete Confirmation Dialog -->
        <WMConfirmDialog
            ref="deleteDialog"
            header="Usuń plik"
            message="Czy na pewno chcesz usunąć ten plik? Tej operacji nie można cofnąć."
            @confirm="deleteFile"
        />

        <!-- Document Selector Dialog -->
        <WMDocumentSelectorDialog
            v-model:visible="documentSelectorVisible"
            @select="onTemplatesSelected"
        />

        <!-- Document Preview Dialog -->
        <WMDocumentPreviewDialog
            v-model:visible="documentPreviewVisible"
            :templates="selectedTemplates"
            :initial-data="documentData"
        />

        <Toast />
    </div>
</template>
