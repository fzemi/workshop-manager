<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import ClientService from '@/service/ClientService.js';
import VehicleService from '@/service/VehicleService.js';
import { clientSchema, clientDefaultValues } from '@/libs/schemas/clientSchemas.js';
import { toISODateString } from '@/libs/dateUtils.js';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const isEditMode = computed(() => !!route.params.id);
const clientId = computed(() => route.params.id);
const pageTitle = computed(() => isEditMode.value ? 'Edytuj klienta' : 'Dodaj klienta');

const loading = ref(false);
const saving = ref(false);
const vehicleOptions = ref([]);

// Form setup
const resolver = zodResolver(clientSchema);
const initialValues = ref({ ...clientDefaultValues });

onMounted(async () => {
    await loadVehicleOptions();
    
    if (isEditMode.value) {
        await loadClient();
    }
});

async function loadVehicleOptions() {
    try {
        vehicleOptions.value = await VehicleService.getOptions();
    } catch (error) {
        console.error('Failed to load vehicle options:', error);
    }
}

async function loadClient() {
    loading.value = true;
    try {
        const client = await ClientService.getById(clientId.value);
        initialValues.value = {
            firstname: client.firstname || '',
            surname: client.surname || '',
            pesel: client.pesel || '',
            nip: client.nip || '',
            phoneNumber: client.phoneNumber || '',
            email: client.email || '',
            country: client.country || 'Polska',
            postalCode: client.postalCode || '',
            city: client.city || '',
            address: client.address || '',
            birthDate: client.birthDate ? new Date(client.birthDate) : null,
            vehicles: client.vehicles?.map(v => v.id) || [],
        };
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
        // Prepare data for API - convert vehicle IDs to objects with id property
        const clientData = {
            ...values,
            birthDate: toISODateString(values.birthDate) || null,
            vehicles: values.vehicles?.map(vehicleId => ({ id: vehicleId })) || [],
        };

        if (isEditMode.value) {
            await ClientService.patch(clientId.value, clientData);
            toast.add({
                severity: 'success',
                summary: 'Sukces',
                detail: 'Klient został zaktualizowany',
                life: 3000,
            });
        } else {
            await ClientService.create(clientData);
            toast.add({
                severity: 'success',
                summary: 'Sukces',
                detail: 'Klient został utworzony',
                life: 3000,
            });
        }
        router.push('/clients');
    } catch (error) {
        toast.add({
            severity: 'error',
            summary: 'Błąd',
            detail: error.message || 'Nie udało się zapisać klienta',
            life: 3000,
        });
    } finally {
        saving.value = false;
    }
}

function goBack() {
    router.push('/clients');
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
                    <!-- Dane podstawowe -->
                    <div class="md:col-span-2">
                        <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
                            <i class="pi pi-user"></i>
                            Dane podstawowe
                        </h3>
                    </div>

                    <FormField v-slot="$field" name="firstname">
                        <label class="font-medium">Imię *</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="surname">
                        <label class="font-medium">Nazwisko *</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="pesel">
                        <label class="font-medium">PESEL</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="nip">
                        <label class="font-medium">NIP</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="birthDate">
                        <label class="font-medium">Data urodzenia</label>
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

                    <!-- Dane kontaktowe -->
                    <div class="md:col-span-2 mt-4">
                        <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
                            <i class="pi pi-phone"></i>
                            Dane kontaktowe
                        </h3>
                    </div>

                    <FormField v-slot="$field" name="phoneNumber">
                        <label class="font-medium">Telefon *</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="email">
                        <label class="font-medium">Email</label>
                        <InputText
                            v-bind="$field"
                            type="email"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <!-- Adres -->
                    <div class="md:col-span-2 mt-4">
                        <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
                            <i class="pi pi-map-marker"></i>
                            Adres
                        </h3>
                    </div>

                    <FormField v-slot="$field" name="country">
                        <label class="font-medium">Kraj</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="postalCode">
                        <label class="font-medium">Kod pocztowy</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="city">
                        <label class="font-medium">Miasto</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <FormField v-slot="$field" name="address">
                        <label class="font-medium">Adres</label>
                        <InputText
                            v-bind="$field"
                            class="w-full"
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
                    </FormField>

                    <!-- Pojazdy -->
                    <div class="md:col-span-2 mt-4">
                        <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
                            <i class="pi pi-car"></i>
                            Pojazdy
                        </h3>
                    </div>

                    <FormField v-slot="$field" name="vehicles" class="md:col-span-2">
                        <label class="font-medium">Przypisane pojazdy</label>
                        <MultiSelect
                            v-bind="$field"
                            :options="vehicleOptions"
                            optionLabel="label"
                            optionValue="value"
                            placeholder="Wybierz pojazdy"
                            class="w-full"
                            display="chip"
                            filter
                            :invalid="!!$field?.invalid"
                        />
                        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
                            {{ $field.error?.message }}
                        </Message>
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
                            :label="isEditMode ? 'Zapisz zmiany' : 'Dodaj klienta'"
                            :loading="saving"
                            icon="pi pi-check"
                        />
                    </div>
                </Form>
            </template>
        </Card>

        <Toast />
    </div>
</template>
