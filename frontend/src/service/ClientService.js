import { httpService } from '@/api/index.js';

/**
 * Service for Client API operations
 */
const ClientService = {
    /**
     * Get all clients
     * @returns {Promise<Array>} List of clients
     */
    async getAll() {
        const response = await httpService.get('/clients');
        return response.data;
    },

    /**
     * Get client by ID
     * @param {number|string} id - Client ID
     * @returns {Promise<Object>} Client data
     */
    async getById(id) {
        const response = await httpService.get(`/clients/${id}`);
        return response.data;
    },

    /**
     * Search clients by surname
     * @param {string} surname - Surname to search
     * @returns {Promise<Array>} List of matching clients
     */
    async findBySurname(surname) {
        const response = await httpService.get(`/clients/find/${surname}`);
        return response.data;
    },

    /**
     * Create a new client
     * @param {Object} client - Client data
     * @returns {Promise<Object>} Created client
     */
    async create(client) {
        const response = await httpService.post('/clients', client);
        return response.data;
    },

    /**
     * Update client (full update)
     * @param {number|string} id - Client ID
     * @param {Object} client - Client data
     * @returns {Promise<Object>} Updated client
     */
    async update(id, client) {
        const response = await httpService.put(`/clients/${id}`, client);
        return response.data;
    },

    /**
     * Partial update client
     * @param {number|string} id - Client ID
     * @param {Object} updates - Fields to update
     * @returns {Promise<Object>} Updated client
     */
    async patch(id, updates) {
        const response = await httpService.patch(`/clients/${id}`, updates);
        return response.data;
    },

    /**
     * Delete client
     * @param {number|string} id - Client ID
     * @returns {Promise<void>}
     */
    async delete(id) {
        await httpService.delete(`/clients/${id}`);
    },

    /**
     * Get clients formatted for dropdown/select options
     * @returns {Promise<Array>} List of { id, label } objects
     */
    async getOptions() {
        const clients = await this.getAll();
        return clients.map((client) => ({
            id: client.id,
            label: `${client.firstname} ${client.surname}`,
            value: client.id,
        }));
    },
};

export default ClientService;
