<script setup>
import { ref, computed } from 'vue';
import { useToast } from 'primevue/usetoast';
import FileService from '@/service/FileService.js';

const props = defineProps({
    /**
     * Whether the dialog is visible
     */
    visible: {
        type: Boolean,
        default: false,
    },
    /**
     * Repair ID to upload files to
     */
    repairId: {
        type: [Number, String],
        required: true,
    },
});

const emit = defineEmits(['update:visible', 'uploaded']);

const toast = useToast();

// File upload states
const STATUS = {
    PENDING: 'pending',
    UPLOADING: 'uploading',
    SUCCESS: 'success',
    ERROR: 'error',
};

// Files queue with status tracking
const filesQueue = ref([]);
const isDragging = ref(false);

// Max file size: 100MB
const MAX_FILE_SIZE = 100 * 1024 * 1024;

/**
 * Handle file selection from input or drop
 */
function onFilesSelected(event) {
    const files = event.files || event.target?.files || [];
    
    for (const file of files) {
        // Check for duplicates
        const exists = filesQueue.value.some(f => f.file.name === file.name && f.file.size === file.size);
        if (exists) continue;
        
        // Validate file size
        if (file.size > MAX_FILE_SIZE) {
            toast.add({
                severity: 'error',
                summary: 'Błąd',
                detail: `Plik "${file.name}" przekracza limit 100MB`,
                life: 5000,
            });
            continue;
        }
        
        filesQueue.value.push({
            id: crypto.randomUUID(),
            file,
            status: STATUS.PENDING,
            progress: 0,
            error: null,
        });
    }
}

/**
 * Start uploading all pending files
 */
async function uploadAll() {
    const pendingFiles = filesQueue.value.filter(f => f.status === STATUS.PENDING || f.status === STATUS.ERROR);
    
    for (const fileItem of pendingFiles) {
        await uploadFile(fileItem);
    }
    
    // Emit uploaded event if any files were successfully uploaded
    const hasSuccess = filesQueue.value.some(f => f.status === STATUS.SUCCESS);
    if (hasSuccess) {
        emit('uploaded');
    }
}

/**
 * Upload a single file
 */
async function uploadFile(fileItem) {
    fileItem.status = STATUS.UPLOADING;
    fileItem.progress = 0;
    fileItem.error = null;
    
    try {
        await FileService.upload(props.repairId, fileItem.file, (progress) => {
            fileItem.progress = progress;
        });
        
        fileItem.status = STATUS.SUCCESS;
        fileItem.progress = 100;
        
        toast.add({
            severity: 'success',
            summary: 'Sukces',
            detail: `Plik "${fileItem.file.name}" został przesłany`,
            life: 3000,
        });
    } catch (error) {
        fileItem.status = STATUS.ERROR;
        fileItem.error = error.message || 'Błąd przesyłania pliku';
        
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: `Nie udało się przesłać pliku "${fileItem.file.name}"`,
            life: 5000,
        });
    }
}

/**
 * Retry uploading a failed file
 */
function retryUpload(fileItem) {
    uploadFile(fileItem);
}

/**
 * Remove file from queue
 */
function removeFile(fileItem) {
    const index = filesQueue.value.findIndex(f => f.id === fileItem.id);
    if (index > -1) {
        filesQueue.value.splice(index, 1);
    }
}

/**
 * Clear all files from queue
 */
function clearAll() {
    filesQueue.value = [];
}

/**
 * Close dialog
 */
function closeDialog() {
    emit('update:visible', false);
}

/**
 * Handle dialog hide - clear successful uploads
 */
function onDialogHide() {
    // Remove successfully uploaded files from queue
    filesQueue.value = filesQueue.value.filter(f => f.status !== STATUS.SUCCESS);
}

/**
 * Format file size
 */
function formatSize(bytes) {
    if (!bytes || bytes === 0) return '0 B';
    
    const units = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    const size = (bytes / Math.pow(1024, i)).toFixed(i > 0 ? 1 : 0);
    
    return `${size} ${units[i]}`;
}

/**
 * Get file icon based on type
 */
function getFileIcon(file) {
    const type = file.type || '';
    
    if (type.startsWith('image/')) return 'pi-image';
    if (type === 'application/pdf') return 'pi-file-pdf';
    if (type.includes('word')) return 'pi-file-word';
    if (type.includes('excel') || type.includes('spreadsheet')) return 'pi-file-excel';
    
    return 'pi-file';
}

/**
 * Get status severity for badge
 */
function getStatusSeverity(status) {
    switch (status) {
        case STATUS.PENDING: return 'warn';
        case STATUS.UPLOADING: return 'info';
        case STATUS.SUCCESS: return 'success';
        case STATUS.ERROR: return 'danger';
        default: return 'secondary';
    }
}

/**
 * Get status label
 */
function getStatusLabel(status) {
    switch (status) {
        case STATUS.PENDING: return 'Oczekuje';
        case STATUS.UPLOADING: return 'Przesyłanie...';
        case STATUS.SUCCESS: return 'Przesłano';
        case STATUS.ERROR: return 'Błąd';
        default: return '';
    }
}

// Computed properties
const hasFiles = computed(() => filesQueue.value.length > 0);
const hasPendingFiles = computed(() => filesQueue.value.some(f => f.status === STATUS.PENDING || f.status === STATUS.ERROR));
const isUploading = computed(() => filesQueue.value.some(f => f.status === STATUS.UPLOADING));

// Handle drag events
function onDragEnter(e) {
    e.preventDefault();
    isDragging.value = true;
}

function onDragLeave(e) {
    e.preventDefault();
    isDragging.value = false;
}

function onDragOver(e) {
    e.preventDefault();
}

function onDrop(e) {
    e.preventDefault();
    isDragging.value = false;
    
    const files = e.dataTransfer?.files;
    if (files?.length) {
        onFilesSelected({ files: Array.from(files) });
    }
}
</script>

<template>
    <Dialog
        :visible="visible"
        @update:visible="emit('update:visible', $event)"
        @hide="onDialogHide"
        modal
        header="Dodaj pliki"
        :style="{ width: '50rem' }"
        :breakpoints="{ '1199px': '75vw', '575px': '90vw' }"
    >
        <div class="flex flex-col gap-6">
            <!-- Drop Zone -->
            <div
                class="border-2 border-dashed rounded-xl p-8 text-center transition-all duration-200 cursor-pointer"
                :class="[
                    isDragging
                        ? 'border-primary-500 bg-primary-50 dark:bg-primary-900/20'
                        : 'border-surface-300 dark:border-surface-600 hover:border-primary-400 dark:hover:border-primary-500 bg-surface-50 dark:bg-surface-800'
                ]"
                @dragenter="onDragEnter"
                @dragleave="onDragLeave"
                @dragover="onDragOver"
                @drop="onDrop"
                @click="$refs.fileInput.click()"
            >
                <input
                    ref="fileInput"
                    type="file"
                    multiple
                    class="hidden"
                    @change="onFilesSelected"
                />
                
                <i class="pi pi-cloud-upload text-5xl text-surface-400 dark:text-surface-500 mb-4"></i>
                <p class="text-lg font-medium text-surface-700 dark:text-surface-300 mb-2">
                    Przeciągnij i upuść pliki tutaj
                </p>
                <p class="text-sm text-surface-500 dark:text-surface-400 mb-4">
                    lub kliknij aby wybrać pliki
                </p>
                <Button
                    label="Wybierz pliki"
                    icon="pi pi-folder-open"
                    severity="secondary"
                    outlined
                    @click.stop="$refs.fileInput.click()"
                />
                <p class="text-xs text-surface-400 dark:text-surface-500 mt-4">
                    Maksymalny rozmiar pliku: 100MB
                </p>
            </div>

            <!-- Files Queue -->
            <div v-if="hasFiles" class="flex flex-col gap-2">
                <div class="flex items-center justify-between mb-2">
                    <span class="font-medium text-surface-700 dark:text-surface-300">
                        Pliki do przesłania ({{ filesQueue.length }})
                    </span>
                    <Button
                        label="Wyczyść"
                        icon="pi pi-times"
                        severity="secondary"
                        text
                        size="small"
                        @click="clearAll"
                        :disabled="isUploading"
                    />
                </div>

                <!-- File Items -->
                <div class="max-h-64 overflow-y-auto space-y-2">
                    <div
                        v-for="fileItem in filesQueue"
                        :key="fileItem.id"
                        class="flex items-center gap-3 p-3 rounded-lg border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900"
                    >
                        <!-- File Icon -->
                        <i :class="['pi', getFileIcon(fileItem.file), 'text-2xl text-surface-400']"></i>

                        <!-- File Info -->
                        <div class="flex-1 min-w-0">
                            <div class="flex items-center gap-2 mb-1">
                                <span class="font-medium text-surface-700 dark:text-surface-300 truncate">
                                    {{ fileItem.file.name }}
                                </span>
                                <Badge
                                    :value="getStatusLabel(fileItem.status)"
                                    :severity="getStatusSeverity(fileItem.status)"
                                    class="shrink-0"
                                />
                            </div>
                            <div class="flex items-center gap-2 text-xs text-surface-500">
                                <span>{{ formatSize(fileItem.file.size) }}</span>
                                <span v-if="fileItem.error" class="text-red-500">
                                    • {{ fileItem.error }}
                                </span>
                            </div>
                            <!-- Progress Bar -->
                            <ProgressBar
                                v-if="fileItem.status === STATUS.UPLOADING"
                                :value="fileItem.progress"
                                :showValue="false"
                                class="h-1.5 mt-2"
                            />
                        </div>

                        <!-- Actions -->
                        <div class="flex items-center gap-1 shrink-0">
                            <!-- Retry Button -->
                            <Button
                                v-if="fileItem.status === STATUS.ERROR"
                                icon="pi pi-refresh"
                                severity="warn"
                                text
                                rounded
                                size="small"
                                v-tooltip.top="'Ponów'"
                                @click="retryUpload(fileItem)"
                            />
                            <!-- Remove Button -->
                            <Button
                                v-if="fileItem.status !== STATUS.UPLOADING"
                                icon="pi pi-times"
                                severity="secondary"
                                text
                                rounded
                                size="small"
                                v-tooltip.top="'Usuń z listy'"
                                @click="removeFile(fileItem)"
                            />
                            <!-- Success Icon -->
                            <i
                                v-if="fileItem.status === STATUS.SUCCESS"
                                class="pi pi-check-circle text-green-500 text-xl"
                            ></i>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <template #footer>
            <div class="flex justify-end gap-2">
                <Button
                    label="Zamknij"
                    severity="secondary"
                    @click="closeDialog"
                />
                <Button
                    label="Prześlij wszystkie"
                    icon="pi pi-upload"
                    @click="uploadAll"
                    :disabled="!hasPendingFiles"
                    :loading="isUploading"
                />
            </div>
        </template>
    </Dialog>
</template>
