import { httpService } from '@/api/index.js';

/**
 * Service for File API operations
 * Handles file upload, download, and deletion for repairs
 */
const FileService = {
    /**
     * Get all files for a repair
     * @param {number|string} repairId - Repair ID
     * @returns {Promise<Array>} List of file DTOs
     */
    async getByRepairId(repairId) {
        const response = await httpService.get(`/repairs/${repairId}/files`);
        return response.data;
    },

    /**
     * Upload a file to a repair
     * @param {number|string} repairId - Repair ID
     * @param {File} file - File to upload
     * @param {Function} onProgress - Optional progress callback (0-100)
     * @returns {Promise<Object>} Response with status
     */
    async upload(repairId, file, onProgress = null) {
        const formData = new FormData();
        formData.append('file', file);

        return httpService.uploadFile(`/repairs/${repairId}/files`, formData, onProgress);
    },

    /**
     * Download a file as blob
     * @param {number|string} repairId - Repair ID
     * @param {number|string} fileId - File ID
     * @returns {Promise<Blob>} File blob
     */
    async download(repairId, fileId) {
        return httpService.downloadBlob(`/repairs/${repairId}/files/${fileId}`);
    },

    /**
     * Delete a file
     * @param {number|string} repairId - Repair ID
     * @param {number|string} fileId - File ID
     * @returns {Promise<void>}
     */
    async delete(repairId, fileId) {
        await httpService.delete(`/repairs/${repairId}/files/${fileId}`);
    },
};

export default FileService;
