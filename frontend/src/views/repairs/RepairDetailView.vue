<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import RepairService from '@/service/RepairService.js';
import { formatDate } from '@/libs/dateUtils.js';
import { getRepairTypeLabel } from '@/libs/constants.js';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const repair = ref(null);
const loading = ref(true);

const repairId = computed(() => route.params.id);

const clientsList = computed(() => {
    // Clients come from top-level repair.clients (from RepairWithClientsDTO)
    return repair.value?.clients?.map(c => ({
        id: c.id,
        label: `${c.firstname} ${c.surname}`,
    })) || [];
});

onMounted(async () => {
    await loadRepair();
});

async function loadRepair() {
    loading.value = true;
    try {
        repair.value = await RepairService.getByIdWithClients(repairId.value);
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać danych naprawy',
            life: 3000,
        });
        router.push('/repairs');
    } finally {
        loading.value = false;
    }
}

function goBack() {
    router.push('/repairs');
}

function goToEdit() {
    router.push(`/repairs/${repairId.value}/edit`);
}
</script>

<template>
    <div>
        <div class="flex items-center gap-4 mb-6">
            <Button
                icon="pi pi-arrow-left"
                severity="secondary"
                text
                rounded
                @click="goBack"
            />
            <h1 class="text-2xl font-semibold text-surface-800 dark:text-surface-100">
                Szczegóły naprawy
            </h1>
            <div class="ml-auto">
                <Button
                    label="Edytuj"
                    icon="pi pi-pencil"
                    @click="goToEdit"
                />
            </div>
        </div>

        <div v-if="loading" class="flex justify-center py-8">
            <ProgressSpinner />
        </div>

        <div v-else-if="repair" class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Dane naprawy -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-wrench"></i>
                        Dane naprawy
                    </div>
                </template>
                <template #content>
                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Numer naprawy</label>
                            <p class="font-medium font-mono">{{ repair.number || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Typ naprawy</label>
                            <p class="font-medium">
                                <Tag 
                                    :value="getRepairTypeLabel(repair.type)" 
                                    :severity="repair.type === 'BODY' ? 'info' : 'success'" 
                                />
                            </p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Data rozpoczęcia</label>
                            <p class="font-medium">{{ formatDate(repair.startDate) || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Przewidywane zakończenie</label>
                            <p class="font-medium">{{ formatDate(repair.expectedEndDate) || '—' }}</p>
                        </div>
                    </div>
                </template>
            </Card>

            <!-- Pojazd -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-car"></i>
                        Pojazd
                    </div>
                </template>
                <template #content>
                    <div v-if="repair.vehicle" class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Marka i model</label>
                            <p class="font-medium">
                                <WMEntityLink
                                    :id="repair.vehicle.id"
                                    :label="`${repair.vehicle.manufacturer} ${repair.vehicle.model}`"
                                    entityType="vehicle"
                                />
                            </p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Nr rejestracyjny</label>
                            <p class="font-medium">{{ repair.vehicle.licencePlate || '—' }}</p>
                        </div>
                        <div class="col-span-2">
                            <label class="text-sm text-surface-500">VIN</label>
                            <p class="font-medium font-mono text-sm">{{ repair.vehicle.vin || '—' }}</p>
                        </div>
                    </div>
                    <p v-else class="text-surface-500">Brak przypisanego pojazdu</p>
                </template>
            </Card>

            <!-- Klienci -->
            <Card class="md:col-span-2">
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-users"></i>
                        Klienci (właściciele pojazdu)
                    </div>
                </template>
                <template #content>
                    <div v-if="clientsList.length > 0" class="flex flex-wrap gap-3">
                        <div 
                            v-for="client in clientsList" 
                            :key="client.id"
                            class="flex items-center gap-2 bg-surface-100 dark:bg-surface-700 rounded-lg px-3 py-2"
                        >
                            <i class="pi pi-user text-surface-500"></i>
                            <WMEntityLink
                                :id="client.id"
                                :label="client.label"
                                entityType="client"
                            />
                        </div>
                    </div>
                    <p v-else class="text-surface-500">Brak przypisanych klientów do pojazdu</p>
                </template>
            </Card>

            <!-- Pliki (placeholder na przyszłość) -->
            <Card class="md:col-span-2">
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-file"></i>
                        Załączniki
                    </div>
                </template>
                <template #content>
                    <p class="text-surface-500 italic">
                        Funkcjonalność załączników zostanie dodana w przyszłości.
                    </p>
                </template>
            </Card>
        </div>

        <Toast />
    </div>
</template>
