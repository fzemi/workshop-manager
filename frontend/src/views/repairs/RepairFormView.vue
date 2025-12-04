<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import RepairService from '@/service/RepairService.js';
import VehicleService from '@/service/VehicleService.js';
import { repairSchema, repairDefaultValues } from '@/libs/schemas/repairSchemas.js';
import { REPAIR_TYPE_OPTIONS } from '@/libs/constants.js';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const isEditMode = computed(() => !!route.params.id);
const repairId = computed(() => route.params.id);
const pageTitle = computed(() => isEditMode.value ? 'Edytuj naprawę' : 'Dodaj naprawę');

const loading = ref(false);
const saving = ref(false);
const vehicleOptions = ref([]);

// Quick create dialog refs
const vehicleQuickCreateDialog = ref(null);
const clientQuickCreateDialog = ref(null);

// Form setup
const resolver = zodResolver(repairSchema);
const initialValues = ref({ ...repairDefaultValues });

onMounted(async () => {
    await loadVehicleOptions();
    
    if (isEditMode.value) {
        await loadRepair();
    }
});

async function loadVehicleOptions() {
    try {
        vehicleOptions.value = await VehicleService.getOptions();
    } catch (error) {
        console.error('Failed to load vehicle options:', error);
    }
}

async function loadRepair() {
    loading.value = true;
    try {
        const repair = await RepairService.getById(repairId.value);
        initialValues.value = {
            number: repair.number || '',
            startDate: repair.startDate ? new Date(repair.startDate) : new Date(),
            expectedEndDate: repair.expectedEndDate ? new Date(repair.expectedEndDate) : null,
            type: repair.type || null,
            vehicleId: repair.vehicle?.id || null,
        };
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
            startDate: values.startDate.toISOString().split('T')[0],
            expectedEndDate: values.expectedEndDate.toISOString().split('T')[0],
            type: values.type,
            vehicle: { id: values.vehicleId },
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
    toast.add({
        severity: 'info',
        summary: 'Informacja',
        detail: 'Nowy klient został utworzony. Przypisz go do pojazdu w edycji pojazdu.',
        life: 4000,
    });
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
                            v-bind="$field"
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
                            v-bind="$field"
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

                    <!-- Info o kliencie -->
                    <div class="md:col-span-2">
                        <Message severity="info" :closable="false">
                            <template #icon>
                                <i class="pi pi-info-circle"></i>
                            </template>
                            Klienci są przypisani do pojazdu. Aby dodać nowego klienta do naprawy, 
                            najpierw utwórz go, a następnie przypisz do wybranego pojazdu.
                            <Button
                                label="Dodaj nowego klienta"
                                icon="pi pi-user-plus"
                                size="small"
                                severity="info"
                                text
                                class="ml-2"
                                @click="openClientQuickCreate"
                            />
                        </Message>
                    </div>

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

        <Toast />
    </div>
</template>
