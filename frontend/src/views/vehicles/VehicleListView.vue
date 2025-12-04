<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import VehicleService from '@/service/VehicleService.js';
import { getFuelTypeLabel } from '@/libs/constants.js';

const router = useRouter();
const toast = useToast();

const vehicles = ref([]);
const loading = ref(true);
const deleteDialog = ref(null);

const columns = [
    { field: 'manufacturer', header: 'Marka' },
    { field: 'model', header: 'Model' },
    { field: 'licencePlate', header: 'Nr rejestracyjny' },
    { field: 'vin', header: 'VIN' },
    { field: 'clients', header: 'Klienci', sortable: false },
];

const globalFilterFields = ['manufacturer', 'model', 'licencePlate', 'vin'];

// Map vehicles for display with client info
const vehiclesWithClients = computed(() => {
    return vehicles.value.map(vehicle => ({
        ...vehicle,
        clientsList: vehicle.clients?.map(c => ({
            id: c.id,
            label: `${c.firstname} ${c.surname}`,
        })) || [],
    }));
});

onMounted(async () => {
    await loadVehicles();
});

async function loadVehicles() {
    loading.value = true;
    try {
        vehicles.value = await VehicleService.getAll();
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać listy pojazdów',
            life: 3000,
        });
    } finally {
        loading.value = false;
    }
}

function onView(vehicle) {
    router.push(`/vehicles/${vehicle.id}`);
}

function onEdit(vehicle) {
    router.push(`/vehicles/${vehicle.id}/edit`);
}

function onDelete(vehicle) {
    deleteDialog.value.show(vehicle);
}

async function confirmDelete(vehicle) {
    try {
        await VehicleService.delete(vehicle.id);
        toast.add({
            severity: 'success',
            summary: 'Sukces',
            detail: 'Pojazd został usunięty',
            life: 3000,
        });
        await loadVehicles();
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się usunąć pojazdu',
            life: 3000,
        });
    }
}
</script>

<template>
    <div>
        <WMPageHeader
            title="Pojazdy"
            createRoute="/vehicles/new"
            createLabel="Dodaj pojazd"
        />

        <WMDataTable
            :data="vehiclesWithClients"
            :columns="columns"
            :loading="loading"
            :globalFilterFields="globalFilterFields"
            searchPlaceholder="Szukaj pojazdów..."
            @view="onView"
            @edit="onEdit"
            @delete="onDelete"
        >
            <template #column-clients="{ data }">
                <WMExpandableChips
                    :items="data.clientsList"
                    entityType="client"
                    :maxVisible="2"
                />
            </template>
        </WMDataTable>

        <WMConfirmDialog
            ref="deleteDialog"
            header="Usuń pojazd"
            message="Czy na pewno chcesz usunąć ten pojazd? Tej operacji nie można cofnąć."
            @confirm="confirmDelete"
        />

        <Toast />
    </div>
</template>
