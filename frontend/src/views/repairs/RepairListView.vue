<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import RepairService from '@/service/RepairService.js';
import { formatDate } from '@/libs/dateUtils.js';
import { getRepairTypeLabel } from '@/libs/constants.js';

const router = useRouter();
const toast = useToast();

const repairs = ref([]);
const loading = ref(true);
const deleteDialog = ref(null);

const columns = [
    { field: 'number', header: 'Numer' },
    { field: 'clients', header: 'Klienci', sortable: false },
    { field: 'startDate', header: 'Data rozpoczęcia' },
    { field: 'expectedEndDate', header: 'Przewidywane zakończenie' },
    { field: 'type', header: 'Typ naprawy' },
    { field: 'vehicle', header: 'Pojazd', sortable: false },
];

const globalFilterFields = ['number', 'type'];

// Map repairs for display
const repairsForDisplay = computed(() => {
    return repairs.value.map(repair => ({
        ...repair,
        startDateFormatted: formatDate(repair.startDate),
        expectedEndDateFormatted: formatDate(repair.expectedEndDate),
        typeLabel: getRepairTypeLabel(repair.type),
        vehicleLabel: repair.vehicle 
            ? `${repair.vehicle.manufacturer} ${repair.vehicle.model}` 
            : '—',
        vehicleId: repair.vehicle?.id,
        // Clients come from top-level repair.clients (from RepairWithClientsDTO)
        clientsList: repair.clients?.map(c => ({
            id: c.id,
            label: `${c.firstname} ${c.surname}`,
        })) || [],
    }));
});

onMounted(async () => {
    await loadRepairs();
});

async function loadRepairs() {
    loading.value = true;
    try {
        repairs.value = await RepairService.getAllWithClients();
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać listy napraw',
            life: 3000,
        });
    } finally {
        loading.value = false;
    }
}

function onView(repair) {
    router.push(`/repairs/${repair.id}`);
}

function onEdit(repair) {
    router.push(`/repairs/${repair.id}/edit`);
}

function onDelete(repair) {
    deleteDialog.value.show(repair);
}

async function confirmDelete(repair) {
    try {
        await RepairService.delete(repair.id);
        toast.add({
            severity: 'success',
            summary: 'Sukces',
            detail: 'Naprawa została usunięta',
            life: 3000,
        });
        await loadRepairs();
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się usunąć naprawy',
            life: 3000,
        });
    }
}
</script>

<template>
    <div>
        <WMPageHeader
            title="Naprawy"
            createRoute="/repairs/new"
            createLabel="Dodaj naprawę"
        />

        <WMDataTable
            :data="repairsForDisplay"
            :columns="columns"
            :loading="loading"
            :globalFilterFields="globalFilterFields"
            searchPlaceholder="Szukaj napraw..."
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

            <template #column-startDate="{ data }">
                {{ data.startDateFormatted }}
            </template>

            <template #column-expectedEndDate="{ data }">
                {{ data.expectedEndDateFormatted }}
            </template>

            <template #column-type="{ data }">
                <Tag :value="data.typeLabel" :severity="data.type === 'BODY' ? 'info' : 'success'" />
            </template>

            <template #column-vehicle="{ data }">
                <WMEntityLink
                    v-if="data.vehicleId"
                    :id="data.vehicleId"
                    :label="data.vehicleLabel"
                    entityType="vehicle"
                />
                <span v-else class="text-surface-400">—</span>
            </template>
        </WMDataTable>

        <WMConfirmDialog
            ref="deleteDialog"
            header="Usuń naprawę"
            message="Czy na pewno chcesz usunąć tę naprawę? Tej operacji nie można cofnąć."
            @confirm="confirmDelete"
        />

        <Toast />
    </div>
</template>
