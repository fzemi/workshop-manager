const API_URL = import.meta.env.VITE_API_URL;
const API_PORT = import.meta.env.VITE_API_PORT;

const ClientService = {
    getClientsData: async function () {
        try {
            const response = await fetch(`${API_URL}:${API_PORT}/api/v1/clients`);
            const clients = await response.json();

            clients.map((client) => {
                client.birthDate = new Date(client.birthDate).toISOString().split('T')[0];
                return client;
            });

            return clients;
        } catch (error) {
            throw new Error(error);
        }
    },
    getClientsIdAndName: async function () {
        try {
            const response = await fetch(`${API_URL}:${API_PORT}/api/v1/clients`);
            const clients = await response.json();

            return clients.map((client) => {
                return {
                    id: client.id,
                    fullName: `${client.firstname} ${client.surname}`
                };
            });
        } catch (error) {
            throw new Error(error);
        }
    },
};

export default ClientService;
