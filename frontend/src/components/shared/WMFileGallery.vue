<script setup>
import { ref, computed, onMounted, watch } from 'vue';
import FileService from '@/service/FileService.js';

const props = defineProps({
    /**
     * Array of file DTOs to display
     */
    files: {
        type: Array,
        default: () => [],
    },
    /**
     * Whether the gallery is in loading state
     */
    loading: {
        type: Boolean,
        default: false,
    },
    /**
     * Repair ID for fetching thumbnails
     */
    repairId: {
        type: [Number, String],
        required: true,
    },
});

const emit = defineEmits(['open', 'download', 'delete']);

// Cache for thumbnail blob URLs
const thumbnailUrls = ref({});

/**
 * Load thumbnail for an image file
 */
async function loadThumbnail(file) {
    if (!isImage(file.contentType) || thumbnailUrls.value[file.id]) {
        return;
    }
    
    try {
        const blob = await FileService.download(props.repairId, file.id);
        thumbnailUrls.value[file.id] = URL.createObjectURL(blob);
    } catch (error) {
        console.error('Failed to load thumbnail for file:', file.id, error);
    }
}

/**
 * Load all thumbnails for image files
 */
async function loadAllThumbnails() {
    const imageFiles = props.files.filter(f => isImage(f.contentType));
    await Promise.all(imageFiles.map(loadThumbnail));
}

// Watch for files changes and load thumbnails
watch(() => props.files, (newFiles) => {
    if (newFiles?.length > 0) {
        loadAllThumbnails();
    }
}, { immediate: true });

// Cleanup blob URLs on unmount
onMounted(() => {
    return () => {
        Object.values(thumbnailUrls.value).forEach(url => {
            URL.revokeObjectURL(url);
        });
    };
});

/**
 * Get thumbnail URL for a file
 */
function getThumbnailUrl(file) {
    return thumbnailUrls.value[file.id] || null;
}

/**
 * Get icon class based on content type
 */
function getFileIcon(contentType) {
    if (!contentType) return 'pi-file';
    
    if (contentType.startsWith('image/')) return 'pi-image';
    if (contentType === 'application/pdf') return 'pi-file-pdf';
    if (contentType.includes('word') || contentType.includes('document')) return 'pi-file-word';
    if (contentType.includes('excel') || contentType.includes('spreadsheet')) return 'pi-file-excel';
    if (contentType.includes('powerpoint') || contentType.includes('presentation')) return 'pi-file';
    if (contentType.startsWith('video/')) return 'pi-video';
    if (contentType.startsWith('audio/')) return 'pi-volume-up';
    if (contentType.includes('zip') || contentType.includes('rar') || contentType.includes('archive')) return 'pi-file-zip';
    
    return 'pi-file';
}

/**
 * Check if file is an image
 */
function isImage(contentType) {
    return contentType?.startsWith('image/');
}

/**
 * Format file size to human readable format
 */
function formatSize(bytes) {
    if (!bytes || bytes === 0) return '0 B';
    
    const units = ['B', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(1024));
    const size = (bytes / Math.pow(1024, i)).toFixed(i > 0 ? 1 : 0);
    
    return `${size} ${units[i]}`;
}

/**
 * Truncate filename if too long
 */
function truncateFilename(filename, maxLength = 20) {
    if (!filename || filename.length <= maxLength) return filename;
    
    const ext = filename.lastIndexOf('.') > 0 ? filename.slice(filename.lastIndexOf('.')) : '';
    const name = filename.slice(0, filename.lastIndexOf('.') > 0 ? filename.lastIndexOf('.') : filename.length);
    const truncatedName = name.slice(0, maxLength - ext.length - 3) + '...';
    
    return truncatedName + ext;
}

const hasFiles = computed(() => props.files && props.files.length > 0);
const skeletonCount = 6;

// Track which file card is active (for mobile tap-to-show overlay)
const activeFileId = ref(null);

/**
 * Toggle overlay visibility on mobile (click/tap)
 */
function toggleOverlay(file) {
    if (activeFileId.value === file.id) {
        activeFileId.value = null;
    } else {
        activeFileId.value = file.id;
    }
}

/**
 * Check if overlay should be visible for a file
 */
function isOverlayVisible(file) {
    return activeFileId.value === file.id;
}
</script>

<template>
    <!-- Loading State -->
    <div v-if="loading" class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
        <div v-for="i in skeletonCount" :key="i" class="flex flex-col items-center">
            <Skeleton width="100%" height="120px" class="mb-2 rounded-lg" />
            <Skeleton width="80%" height="1rem" />
        </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="!hasFiles" class="flex flex-col items-center justify-center py-12 text-surface-500">
        <i class="pi pi-inbox text-5xl mb-4"></i>
        <p class="text-lg font-medium">Brak załączników</p>
        <p class="text-sm">Kliknij "Dodaj pliki" aby przesłać załączniki</p>
    </div>

    <!-- File Grid -->
    <div v-else class="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 xl:grid-cols-6 gap-4">
        <div
            v-for="file in files"
            :key="file.id"
            class="group relative flex flex-col items-center p-3 rounded-lg border border-surface-200 dark:border-surface-700 bg-surface-0 dark:bg-surface-900 hover:border-primary-300 dark:hover:border-primary-600 hover:shadow-md transition-all duration-200 cursor-pointer"
            @click="toggleOverlay(file)"
        >
            <!-- Thumbnail / Icon Container -->
            <div class="relative w-full aspect-square flex items-center justify-center bg-surface-100 dark:bg-surface-800 rounded-lg overflow-hidden mb-2">
                <!-- Image Thumbnail -->
                <img
                    v-if="isImage(file.contentType) && getThumbnailUrl(file)"
                    :src="getThumbnailUrl(file)"
                    :alt="file.filename"
                    class="w-full h-full object-cover"
                />
                <!-- Loading placeholder for images -->
                <div
                    v-else-if="isImage(file.contentType) && !getThumbnailUrl(file)"
                    class="w-full h-full flex items-center justify-center"
                >
                    <i class="pi pi-spin pi-spinner text-2xl text-surface-400"></i>
                </div>
                <!-- File Icon -->
                <i
                    v-else
                    :class="['pi', getFileIcon(file.contentType), 'text-4xl text-surface-400 dark:text-surface-500']"
                ></i>

                <!-- Hover/Click Overlay -->
                <div 
                    class="absolute inset-0 bg-black/60 transition-opacity duration-200 flex items-center justify-center gap-2"
                    :class="isOverlayVisible(file) ? 'opacity-100' : 'opacity-0 group-hover:opacity-100'"
                >
                    <Button
                        icon="pi pi-external-link"
                        severity="secondary"
                        rounded
                        size="small"
                        v-tooltip.top="'Otwórz'"
                        @click.stop="emit('open', file)"
                        class="!bg-white/90 hover:!bg-white !text-surface-700"
                    />
                    <Button
                        icon="pi pi-download"
                        severity="secondary"
                        rounded
                        size="small"
                        v-tooltip.top="'Pobierz'"
                        @click.stop="emit('download', file)"
                        class="!bg-white/90 hover:!bg-white !text-surface-700"
                    />
                    <Button
                        icon="pi pi-trash"
                        severity="danger"
                        rounded
                        size="small"
                        v-tooltip.top="'Usuń'"
                        @click.stop="emit('delete', file)"
                        class="!bg-red-500 hover:!bg-red-600 !text-white !border-red-500"
                    />
                </div>
            </div>

            <!-- Filename -->
            <span
                class="text-sm text-center text-surface-700 dark:text-surface-300 w-full truncate"
                :title="file.filename"
            >
                {{ truncateFilename(file.filename) }}
            </span>

            <!-- File Size -->
            <span class="text-xs text-surface-400 dark:text-surface-500">
                {{ formatSize(file.size) }}
            </span>
        </div>
    </div>
</template>
