import { httpService } from '@/api/index.js';

/**
 * Service for Vehicle API operations
 */
const VehicleService = {
    /**
     * Get all vehicles
     * @returns {Promise<Array>} List of vehicles
     */
    async getAll() {
        const response = await httpService.get('/vehicles');
        return response.data;
    },

    /**
     * Get vehicle by ID
     * @param {number|string} id - Vehicle ID
     * @returns {Promise<Object>} Vehicle data
     */
    async getById(id) {
        const response = await httpService.get(`/vehicles/${id}`);
        return response.data;
    },

    /**
     * Get vehicle by VIN
     * @param {string} vin - Vehicle Identification Number
     * @returns {Promise<Object>} Vehicle data
     */
    async getByVin(vin) {
        const response = await httpService.get(`/vehicles/vin/${vin}`);
        return response.data;
    },

    /**
     * Get vehicle by licence plate
     * @param {string} licencePlate - Licence plate number
     * @returns {Promise<Object>} Vehicle data
     */
    async getByLicencePlate(licencePlate) {
        const response = await httpService.get(`/vehicles/licence-plate/${licencePlate}`);
        return response.data;
    },

    /**
     * Create a new vehicle
     * @param {Object} vehicle - Vehicle data
     * @returns {Promise<Object>} Created vehicle
     */
    async create(vehicle) {
        // Remove clients from create payload - they are added via patch
        const { clients, ...vehicleData } = vehicle;
        const response = await httpService.post('/vehicles', vehicleData);
        
        // If clients were provided, add them via patch
        if (clients && clients.length > 0) {
            return this.patch(response.data.id, { clients });
        }
        
        return response.data;
    },

    /**
     * Update vehicle (full update)
     * @param {number|string} id - Vehicle ID
     * @param {Object} vehicle - Vehicle data
     * @returns {Promise<Object>} Updated vehicle
     */
    async update(id, vehicle) {
        const response = await httpService.put(`/vehicles/${id}`, vehicle);
        return response.data;
    },

    /**
     * Partial update vehicle
     * @param {number|string} id - Vehicle ID
     * @param {Object} updates - Fields to update
     * @returns {Promise<Object>} Updated vehicle
     */
    async patch(id, updates) {
        const response = await httpService.patch(`/vehicles/${id}`, updates);
        return response.data;
    },

    /**
     * Delete vehicle
     * @param {number|string} id - Vehicle ID
     * @returns {Promise<void>}
     */
    async delete(id) {
        await httpService.delete(`/vehicles/${id}`);
    },

    /**
     * Get vehicles formatted for dropdown/select options
     * @returns {Promise<Array>} List of { id, label, value } objects
     */
    async getOptions() {
        const vehicles = await this.getAll();
        return vehicles.map((vehicle) => ({
            id: vehicle.id,
            label: `${vehicle.manufacturer} ${vehicle.model} (${vehicle.licencePlate})`,
            value: vehicle.id,
        }));
    },
};

export default VehicleService;
