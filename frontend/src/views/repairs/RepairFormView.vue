<script setup>
import { ref, onMounted, computed, watch } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import { useConfirm } from 'primevue/useconfirm';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import RepairService from '@/service/RepairService.js';
import VehicleService from '@/service/VehicleService.js';
import ClientService from '@/service/ClientService.js';
import { repairSchema, repairDefaultValues } from '@/libs/schemas/repairSchemas.js';
import { REPAIR_TYPE_OPTIONS } from '@/libs/constants.js';
import { toISODateString } from '@/libs/dateUtils.js';

const route = useRoute();
const router = useRouter();
const toast = useToast();
const confirm = useConfirm();

const isEditMode = computed(() => !!route.params.id);
const repairId = computed(() => route.params.id);
const pageTitle = computed(() => isEditMode.value ? 'Edytuj naprawę' : 'Dodaj naprawę');

const loading = ref(false);
const saving = ref(false);
const vehicleOptions = ref([]);
const clientOptions = ref([]);
const selectedVehicleId = ref(null);
const selectedClientIds = ref([]);
const isInitialLoad = ref(true); // Track if we're in initial load phase

// Quick create dialog refs
const vehicleQuickCreateDialog = ref(null);
const clientQuickCreateDialog = ref(null);

// Form setup
const resolver = zodResolver(repairSchema);
const initialValues = ref({ ...repairDefaultValues });
const formKey = ref(0); // Key to force form re-render when initialValues change

// Watch for vehicle selection changes to suggest clients (only after initial load)
watch(selectedVehicleId, async (newVehicleId, oldVehicleId) => {
    // Skip suggestion during initial load or if no vehicle selected
    if (isInitialLoad.value || !newVehicleId || newVehicleId === oldVehicleId) {
        return;
    }
    
    // Don't show suggestion if clients are already assigned
    if (selectedClientIds.value && selectedClientIds.value.length > 0) {
        return;
    }
    
    await suggestClientsFromVehicle(newVehicleId);
});

async function suggestClientsFromVehicle(vehicleId) {
    try {
        const vehicle = await VehicleService.getById(vehicleId);
        if (vehicle.clients && vehicle.clients.length > 0) {
            const vehicleClientIds = vehicle.clients.map(c => c.id);
            
            // Only show suggestion dialog if no clients are currently selected
            if (selectedClientIds.value.length === 0) {
                confirm.require({
                    message: `Pojazd ma przypisanych ${vehicle.clients.length} klient(ów). Czy chcesz automatycznie przypisać ich do naprawy?`,
                    header: 'Sugeruj klientów',
                    icon: 'pi pi-users',
                    acceptLabel: 'Tak, przypisz',
                    rejectLabel: 'Nie, wybiorę ręcznie',
                    accept: () => {
                        selectedClientIds.value = vehicleClientIds;
                    },
                });
            }
        }
    } catch (error) {
        console.error('Failed to fetch vehicle clients:', error);
    }
}

onMounted(async () => {
    await Promise.all([loadVehicleOptions(), loadClientOptions()]);
    
    if (isEditMode.value) {
        await loadRepair();
    }
    
    // Mark initial load as complete
    isInitialLoad.value = false;
});

async function loadVehicleOptions() {
    try {
        vehicleOptions.value = await VehicleService.getOptions();
    } catch (error) {
        console.error('Failed to load vehicle options:', error);
    }
}

async function loadClientOptions() {
    try {
        clientOptions.value = await ClientService.getOptions();
    } catch (error) {
        console.error('Failed to load client options:', error);
    }
}

async function loadRepair() {
    loading.value = true;
    try {
        const repair = await RepairService.getByIdWithClients(repairId.value);
        const clientIds = repair.clients?.map(c => c.id) || [];
        
        initialValues.value = {
            number: repair.number || '',
            startDate: repair.startDate ? new Date(repair.startDate) : new Date(),
            expectedEndDate: repair.expectedEndDate ? new Date(repair.expectedEndDate) : null,
            type: repair.type || null,
            vehicleId: repair.vehicle?.id || null,
            clientIds: clientIds,
        };
        
        selectedVehicleId.value = repair.vehicle?.id || null;
        selectedClientIds.value = clientIds;
        
        // Force form to re-render with new initial values
        formKey.value++;
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

async function onFormSubmit({ valid, values }) {
    if (!valid) {
        toast.add({
            severity: 'warn',
            summary: 'Uwaga',
            detail: 'Popraw błędy w formularzu',
            life: 3000,
        });
        return;
    }

    saving.value = true;
    try {
        // Prepare data for API
        const repairData = {
            number: values.number,
            startDate: toISODateString(values.startDate),
            expectedEndDate: toISODateString(values.expectedEndDate),
            type: values.type,
            vehicle: { id: values.vehicleId },
            clients: selectedClientIds.value.map(id => ({ id })),
        };

        if (isEditMode.value) {
            await RepairService.update(repairId.value, repairData);
            toast.add({
                severity: 'success',
                summary: 'Sukces',
                detail: 'Naprawa została zaktualizowana',
                life: 3000,
            });
        } else {
            await RepairService.create(repairData);
            toast.add({
                severity: 'success',
                summary: 'Sukces',
                detail: 'Naprawa została utworzona',
                life: 3000,
            });
        }
        router.push('/repairs');
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: error.message || 'Nie udało się zapisać naprawy',
            life: 3000,
        });
    } finally {
        saving.value = false;
    }
}

function goBack() {
    router.push('/repairs');
}

function openVehicleQuickCreate() {
    vehicleQuickCreateDialog.value.show();
}

function openClientQuickCreate() {
    clientQuickCreateDialog.value.show();
}

async function onVehicleCreated(vehicle) {
    // Reload vehicle options and select the new vehicle
    await loadVehicleOptions();
    toast.add({
        severity: 'info',
        summary: 'Informacja',
        detail: 'Nowy pojazd został dodany do listy. Możesz go teraz wybrać.',
        life: 4000,
    });
}

async function onClientCreated(client) {
    // Reload client options
    await loadClientOptions();
    toast.add({
        severity: 'info',
        summary: 'Informacja',
        detail: 'Nowy klient został dodany do listy. Możesz go teraz wybrać.',
        life: 4000,
    });
}

function onVehicleChange(vehicleId) {
    selectedVehicleId.value = vehicleId;
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
                {{ pageTitle }}
            </h1>
        </div>

        <div v-if="loading" class="flex justify-center py-8">
            <ProgressSpinner />
        </div>

        <Card v-else>
            <template #content>
                <Form
                    :key="formKey"
                    :resolver="resolver"
                    :initialValues="initialValues"
                    @submit="onFormSubmit"
                    class="grid grid-cols-1 md:grid-cols-2 gap-6"
                >
                    <!-- Dane naprawy -->
                    <div class="md:col-span-2">
                        <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
                            <i class="pi pi-wrench"></i>
                            Dane naprawy
                        </h3>
                    </div>

                    <FormField v-slot="$field" name="number">
                        <label class="font-medium">Numer naprawy *</label>
                        <InputText
                            v-bind="$field"
                            class="w-full font-mono"
                            placeholder="np. 21G-01-2024"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="type">
                        <label class="font-medium">Typ naprawy *</label>
                        <Select
                            v-bind="$field"
                            :options="REPAIR_TYPE_OPTIONS"
                            optionLabel="label"
                            optionValue="value"
                            placeholder="Wybierz typ naprawy"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="startDate">
                        <label class="font-medium">Data rozpoczęcia *</label>
                        <DatePicker
                            :modelValue="$field.value"
                            @update:modelValue="(val) => { $field.value = val; $field.onInput?.({ target: { value: val } }); }"
                            class="w-full"
                            dateFormat="dd.mm.yy"
                            showIcon
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="expectedEndDate">
                        <label class="font-medium">Przewidywane zakończenie *</label>
                        <DatePicker
                            :modelValue="$field.value"
                            @update:modelValue="(val) => { $field.value = val; $field.onInput?.({ target: { value: val } }); }"
                            class="w-full"
                            dateFormat="dd.mm.yy"
                            showIcon
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <!-- Pojazd -->
                    <div class="md:col-span-2 mt-4">
                        <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
                            <i class="pi pi-car"></i>
                            Pojazd
                        </h3>
                    </div>

                    <FormField v-slot="$field" name="vehicleId" class="md:col-span-2">
                        <div class="flex items-end gap-2">
                            <div class="flex-1">
                                <label class="font-medium">Wybierz pojazd *</label>
                                <Select
                                    v-bind="$field"
                                    :options="vehicleOptions"
                                    optionLabel="label"
                                    optionValue="value"
                                    placeholder="Wybierz pojazd"
                                    class="w-full"
                                    filter
                                    :invalid="!!$field?.invalid"
                                    @change="(e) => onVehicleChange(e.value)"
                                />
                            </div>
                            <Button
                                icon="pi pi-plus"
                                severity="secondary"
                                outlined
                                v-tooltip.top="'Dodaj nowy pojazd'"
                                @click="openVehicleQuickCreate"
                            />
                        </div>
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <!-- Klienci -->
                    <div class="md:col-span-2 mt-4">
                        <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
                            <i class="pi pi-users"></i>
                            Klienci
                        </h3>
                    </div>

                    <FormField v-slot="$field" name="clientIds" class="md:col-span-2">
                        <div class="flex items-end gap-2">
                            <div class="flex-1">
                                <label class="font-medium">Przypisani klienci</label>
                                <MultiSelect
                                    v-model="selectedClientIds"
                                    :options="clientOptions"
                                    optionLabel="label"
                                    optionValue="value"
                                    placeholder="Wybierz klientów"
                                    class="w-full"
                                    filter
                                    display="chip"
                                />
                            </div>
                            <Button
                                icon="pi pi-plus"
                                severity="secondary"
                                outlined
                                v-tooltip.top="'Dodaj nowego klienta'"
                                @click="openClientQuickCreate"
                            />
                        </div>
                        <small class="text-muted-color">
                            Po wybraniu pojazdu możesz automatycznie przypisać jego właścicieli
                        </small>
                    </FormField>

                    <!-- Submit -->
                    <div class="md:col-span-2 flex justify-end gap-2 mt-6">
                        <Button
                            label="Anuluj"
                            severity="secondary"
                            outlined
                            @click="goBack"
                        />
                        <Button
                            type="submit"
                            :label="isEditMode ? 'Zapisz zmiany' : 'Dodaj naprawę'"
                            :loading="saving"
                            icon="pi pi-check"
                        />
                    </div>
                </Form>
            </template>
        </Card>

        <!-- Quick Create Dialogs -->
        <WMQuickCreateDialog
            ref="vehicleQuickCreateDialog"
            entityType="vehicle"
            @created="onVehicleCreated"
        />

        <WMQuickCreateDialog
            ref="clientQuickCreateDialog"
            entityType="client"
            @created="onClientCreated"
        />

        <ConfirmDialog />
        <Toast />
    </div>
</template>
