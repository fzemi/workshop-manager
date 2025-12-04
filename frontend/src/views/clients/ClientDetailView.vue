<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import ClientService from '@/service/ClientService.js';
import { formatDate } from '@/libs/dateUtils.js';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const client = ref(null);
const loading = ref(true);

const clientId = computed(() => route.params.id);

const vehiclesList = computed(() => {
    return client.value?.vehicles?.map(v => ({
        id: v.id,
        label: `${v.manufacturer} ${v.model} (${v.licencePlate})`,
    })) || [];
});

onMounted(async () => {
    await loadClient();
});

async function loadClient() {
    loading.value = true;
    try {
        client.value = await ClientService.getById(clientId.value);
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: 'Nie udało się pobrać danych klienta',
            life: 3000,
        });
        router.push('/clients');
    } finally {
        loading.value = false;
    }
}

function goBack() {
    router.push('/clients');
}

function goToEdit() {
    router.push(`/clients/${clientId.value}/edit`);
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
                Szczegóły klienta
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

        <div v-else-if="client" class="grid grid-cols-1 md:grid-cols-2 gap-6">
            <!-- Dane podstawowe -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-user"></i>
                        Dane podstawowe
                    </div>
                </template>
                <template #content>
                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Imię</label>
                            <p class="font-medium">{{ client.firstname || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Nazwisko</label>
                            <p class="font-medium">{{ client.surname || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">PESEL</label>
                            <p class="font-medium">{{ client.pesel || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">NIP</label>
                            <p class="font-medium">{{ client.nip || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Data urodzenia</label>
                            <p class="font-medium">{{ formatDate(client.birthDate) || '—' }}</p>
                        </div>
                    </div>
                </template>
            </Card>

            <!-- Dane kontaktowe -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-phone"></i>
                        Dane kontaktowe
                    </div>
                </template>
                <template #content>
                    <div class="grid grid-cols-1 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Telefon</label>
                            <p class="font-medium">{{ client.phoneNumber || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Email</label>
                            <p class="font-medium">{{ client.email || '—' }}</p>
                        </div>
                    </div>
                </template>
            </Card>

            <!-- Adres -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-map-marker"></i>
                        Adres
                    </div>
                </template>
                <template #content>
                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="text-sm text-surface-500">Kraj</label>
                            <p class="font-medium">{{ client.country || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Kod pocztowy</label>
                            <p class="font-medium">{{ client.postalCode || '—' }}</p>
                        </div>
                        <div>
                            <label class="text-sm text-surface-500">Miasto</label>
                            <p class="font-medium">{{ client.city || '—' }}</p>
                        </div>
                        <div class="col-span-2">
                            <label class="text-sm text-surface-500">Adres</label>
                            <p class="font-medium">{{ client.address || '—' }}</p>
                        </div>
                    </div>
                </template>
            </Card>

            <!-- Pojazdy -->
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-car"></i>
                        Pojazdy
                    </div>
                </template>
                <template #content>
                    <div v-if="vehiclesList.length > 0" class="flex flex-wrap gap-2">
                        <WMEntityLink
                            v-for="vehicle in vehiclesList"
                            :key="vehicle.id"
                            :id="vehicle.id"
                            :label="vehicle.label"
                            entityType="vehicle"
                        />
                    </div>
                    <p v-else class="text-surface-500">Brak przypisanych pojazdów</p>
                </template>
            </Card>
        </div>

        <Toast />
    </div>
</template>
