import { FuelFormat } from "@/service/config/RequestEnums.js";

const API_URL = import.meta.env.VITE_API_URL;
const API_PORT = import.meta.env.VITE_API_PORT;

const ProductService = {
    getVehiclesData: async function () {
        try {
            const response = await fetch(`${API_URL}:${API_PORT}/api/v1/vehicles`);
            const vehicles = await response.json();

            vehicles.map((vehicle) => {
                vehicle.fuelType = FuelFormat[vehicle.fuelType];
                vehicle.productionDate = new Date(vehicle.productionDate).toISOString().split('T')[0];

                return vehicle;
            });

            return vehicles;
        } catch (error) {
            throw new Error(error);
        }
    },
    createVehicle: async function (vehicle) {
        let vehicleCopy = {...vehicle};
        vehicleCopy.fuelType = Object.keys(FuelFormat).find(key => FuelFormat[key] === vehicle.fuelType);

        // vehicle clients are added by PATCH request
        delete vehicleCopy.clients;

        try {
            const response = await fetch(`${API_URL}:${API_PORT}/api/v1/vehicles`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(vehicleCopy)
            });

            return response;
        } catch (error) {
            throw new Error(error);
        }
    },
    addVehicleClients: async function (vehicleId, clientsIds) {
        try {
            const requestBody = {
                clients: clientsIds
            };

            const response = await fetch(`${API_URL}:${API_PORT}/api/v1/vehicles/${vehicleId}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(requestBody)
            });

            return response;
        } catch (error) {
            throw new Error(error);
        }
    },

};

export default ProductService;