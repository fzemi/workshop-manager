<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import VehicleService from '@/service/VehicleService.js';
import { formatDate } from '@/libs/dateUtils.js';
import { getFuelTypeLabel } from '@/libs/constants.js';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const vehicle = ref(null);
const loading = ref(true);

const vehicleId = computed(() => route.params.id);

const clientsList = computed(() => {
    return vehicle.value?.clients?.map(c => ({
        id: c.id,
        label: `${c.firstname} ${c.surname}`,
    })) || [];
});

onMounted(async () => {
    await loadVehicle();
});

async function loadVehicle() {
    loading.value = true;
    try {
        vehicle.value = await VehicleService.getById(vehicleId.value);
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać danych pojazdu',
            life: 3000,
        });
        router.push('/vehicles');
    } finally {
        loading.value = false;
    }
}

function goBack() {
    router.push('/vehicles');
}

function goToEdit() {
    router.push(`/vehicles/${vehicleId.value}/edit`);
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
                Szczegóły pojazdu
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

        <div v-else-if="vehicle" class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Dane podstawowe -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-car"></i>
                        Dane podstawowe
                    </div>
                </template>
                <template #content>
                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Marka</label>
                            <p class="font-medium">{{ vehicle.manufacturer || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Model</label>
                            <p class="font-medium">{{ vehicle.model || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Nr rejestracyjny</label>
                            <p class="font-medium">{{ vehicle.licencePlate || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">VIN</label>
                            <p class="font-medium font-mono text-sm">{{ vehicle.vin || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Data produkcji</label>
                            <p class="font-medium">{{ formatDate(vehicle.productionDate) || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Kolor</label>
                            <p class="font-medium">{{ vehicle.color || '—' }}</p>
                        </div>
                    </div>
                </template>
            </Card>

            <!-- Dane techniczne -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-cog"></i>
                        Dane techniczne
                    </div>
                </template>
                <template #content>
                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Rodzaj paliwa</label>
                            <p class="font-medium">{{ getFuelTypeLabel(vehicle.fuelType) || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Pojemność silnika</label>
                            <p class="font-medium">
                                {{ vehicle.engineCapacity ? `${vehicle.engineCapacity} cm³` : '—' }}
                            </p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Moc</label>
                            <p class="font-medium">
                                {{ vehicle.power ? `${vehicle.power} kW` : '—' }}
                            </p>
                        </div>
                    </div>
                </template>
            </Card>

            <!-- Właściciele -->
            <Card class="md:col-span-2">
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-users"></i>
                        Właściciele
                    </div>
                </template>
                <template #content>
                    <div v-if="clientsList.length > 0" class="flex flex-wrap gap-2">
                        <WMEntityLink
                            v-for="client in clientsList"
                            :key="client.id"
                            :id="client.id"
                            :label="client.label"
                            entityType="client"
                        />
                    </div>
                    <p v-else class="text-surface-500">Brak przypisanych właścicieli</p>
                </template>
            </Card>
        </div>

        <Toast />
    </div>
</template>
