import { RepairTypeFormat } from "@/service/config/RequestEnums.js";

const API_URL = import.meta.env.VITE_API_URL;
const API_PORT = import.meta.env.VITE_API_PORT;

function mapRepairGET(repair) {
    repair.type = RepairTypeFormat[repair.type];
    repair.startDate = new Date(repair.startDate).toISOString().split('T')[0];
    repair.expectedEndDate = new Date(repair.expectedEndDate).toISOString().split('T')[0];
}

const RepairService = {
    getRepairsData: async function () {
        try {
            const response = await fetch(`${API_URL}:${API_PORT}/api/v1/repairs`);
            const repairs = await response.json();

            repairs.map(mapRepairGET);

            return repairs;
        } catch (error) {
            throw new Error(error);
        }
    },
    getRepairsWithClients: async function () {
        try {
            const response = await fetch(`${API_URL}:${API_PORT}/api/v1/repairs/withClients`);
            const repairsWithClients = await response.json();

            repairsWithClients.map(mapRepairGET);

            return repairsWithClients;
        } catch (error) {
            throw new Error(error);
        }
    }
};

export default RepairService;