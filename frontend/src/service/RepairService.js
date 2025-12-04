import { httpService } from '@/api/index.js';

/**
 * Service for Repair API operations
 */
const RepairService = {
    /**
     * Get all repairs
     * @returns {Promise<Array>} List of repairs
     */
    async getAll() {
        const response = await httpService.get('/repairs');
        return response.data;
    },

    /**
     * Get all repairs with associated clients (via vehicle)
     * @returns {Promise<Array>} List of repairs with clients
     */
    async getAllWithClients() {
        const response = await httpService.get('/repairs/withClients');
        return response.data;
    },

    /**
     * Get repair by ID
     * @param {number|string} id - Repair ID
     * @returns {Promise<Object>} Repair data
     */
    async getById(id) {
        const response = await httpService.get(`/repairs/${id}`);
        return response.data;
    },

    /**
     * Get repair by ID with associated clients
     * @param {number|string} id - Repair ID
     * @returns {Promise<Object>} Repair data with clients
     */
    async getByIdWithClients(id) {
        const response = await httpService.get(`/repairs/${id}/withClients`);
        return response.data;
    },

    /**
     * Get repair by number
     * @param {string} number - Repair number
     * @returns {Promise<Object>} Repair data
     */
    async getByNumber(number) {
        const response = await httpService.get(`/repairs/number/${number}`);
        return response.data;
    },

    /**
     * Create a new repair
     * @param {Object} repair - Repair data
     * @returns {Promise<Object>} Created repair
     */
    async create(repair) {
        const response = await httpService.post('/repairs', repair);
        return response.data;
    },

    /**
     * Update repair (full update)
     * @param {number|string} id - Repair ID
     * @param {Object} repair - Repair data
     * @returns {Promise<Object>} Updated repair
     */
    async update(id, repair) {
        const response = await httpService.put(`/repairs/${id}`, repair);
        return response.data;
    },

    /**
     * Partial update repair
     * @param {number|string} id - Repair ID
     * @param {Object} updates - Fields to update
     * @returns {Promise<Object>} Updated repair
     */
    async patch(id, updates) {
        const response = await httpService.patch(`/repairs/${id}`, updates);
        return response.data;
    },

    /**
     * Delete repair
     * @param {number|string} id - Repair ID
     * @returns {Promise<void>}
     */
    async delete(id) {
        await httpService.delete(`/repairs/${id}`);
    },
};

export default RepairService;