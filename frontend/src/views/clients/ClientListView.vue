<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import ClientService from '@/service/ClientService.js';
import { formatDate } from '@/libs/dateUtils.js';

const router = useRouter();
const toast = useToast();

const clients = ref([]);
const loading = ref(true);
const deleteDialog = ref(null);
const clientToDelete = ref(null);

const columns = [
    { field: 'firstname', header: 'Imię' },
    { field: 'surname', header: 'Nazwisko' },
    { field: 'phoneNumber', header: 'Telefon' },
    { field: 'email', header: 'Email' },
    { field: 'vehicles', header: 'Pojazdy', sortable: false },
];

const globalFilterFields = ['firstname', 'surname', 'phoneNumber', 'email', 'city'];

// Map clients for display with vehicle info
const clientsWithVehicles = computed(() => {
    return clients.value.map(client => ({
        ...client,
        vehiclesList: client.vehicles?.map(v => ({
            id: v.id,
            label: `${v.manufacturer} ${v.model}`,
        })) || [],
    }));
});

onMounted(async () => {
    await loadClients();
});

async function loadClients() {
    loading.value = true;
    try {
        clients.value = await ClientService.getAll();
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać listy klientów',
            life: 3000,
        });
    } finally {
        loading.value = false;
    }
}

function onView(client) {
    router.push(`/clients/${client.id}`);
}

function onEdit(client) {
    router.push(`/clients/${client.id}/edit`);
}

function onDelete(client) {
    clientToDelete.value = client;
    deleteDialog.value.show(client);
}

async function confirmDelete(client) {
    try {
        await ClientService.delete(client.id);
        toast.add({
            severity: 'success',
            summary: 'Sukces',
            detail: 'Klient został usunięty',
            life: 3000,
        });
        await loadClients();
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się usunąć klienta',
            life: 3000,
        });
    }
    clientToDelete.value = null;
}
</script>

<template>
    <div>
        <WMPageHeader
            title="Klienci"
            createRoute="/clients/new"
            createLabel="Dodaj klienta"
        />

        <WMDataTable
            :data="clientsWithVehicles"
            :columns="columns"
            :loading="loading"
            :globalFilterFields="globalFilterFields"
            searchPlaceholder="Szukaj klientów..."
            @view="onView"
            @edit="onEdit"
            @delete="onDelete"
        >
            <template #column-vehicles="{ data }">
                <WMExpandableChips
                    :items="data.vehiclesList"
                    entityType="vehicle"
                    :maxVisible="2"
                />
            </template>
        </WMDataTable>

        <WMConfirmDialog
            ref="deleteDialog"
            header="Usuń klienta"
            message="Czy na pewno chcesz usunąć tego klienta? Tej operacji nie można cofnąć."
            @confirm="confirmDelete"
        />

        <Toast />
    </div>
</template>
