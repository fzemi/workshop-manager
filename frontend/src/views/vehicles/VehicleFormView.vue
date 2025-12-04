<script setup>
import { ref, onMounted, computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useToast } from 'primevue/usetoast';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import VehicleService from '@/service/VehicleService.js';
import ClientService from '@/service/ClientService.js';
import { vehicleSchema, vehicleDefaultValues } from '@/libs/schemas/vehicleSchemas.js';
import { FUEL_TYPE_OPTIONS } from '@/libs/constants.js';

const route = useRoute();
const router = useRouter();
const toast = useToast();

const isEditMode = computed(() => !!route.params.id);
const vehicleId = computed(() => route.params.id);
const pageTitle = computed(() => isEditMode.value ? 'Edytuj pojazd' : 'Dodaj pojazd');

const loading = ref(false);
const saving = ref(false);
const clientOptions = ref([]);

// Form setup
const resolver = zodResolver(vehicleSchema);
const initialValues = ref({ ...vehicleDefaultValues });

onMounted(async () => {
  await loadClientOptions();

  if (isEditMode.value) {
    await loadVehicle();
  }
});

async function loadClientOptions() {
  try {
    clientOptions.value = await ClientService.getOptions();
  } catch (error) {
    console.error('Failed to load client options:', error);
  }
}

async function loadVehicle() {
  loading.value = true;
  try {
    const vehicle = await VehicleService.getById(vehicleId.value);
    initialValues.value = {
      vin: vehicle.vin || '',
      manufacturer: vehicle.manufacturer || '',
      model: vehicle.model || '',
      licencePlate: vehicle.licencePlate || '',
      productionDate: vehicle.productionDate ? new Date(vehicle.productionDate) : null,
      color: vehicle.color || '',
      engineCapacity: vehicle.engineCapacity || null,
      fuelType: vehicle.fuelType || null,
      power: vehicle.power || null,
      clients: vehicle.clients?.map(c => c.id) || [],
    };
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
    // Prepare data for API - convert client IDs to objects with id property
    const vehicleData = {
      ...values,
      productionDate: values.productionDate ? values.productionDate.toISOString().split('T')[0] : null,
      clients: values.clients?.map(clientId => ({ id: clientId })) || [],
    };

    if (isEditMode.value) {
      await VehicleService.patch(vehicleId.value, vehicleData);
      toast.add({
        severity: 'success',
        summary: 'Sukces',
        detail: 'Pojazd został zaktualizowany',
        life: 3000,
      });
    } else {
      await VehicleService.create(vehicleData);
      toast.add({
        severity: 'success',
        summary: 'Sukces',
        detail: 'Pojazd został utworzony',
        life: 3000,
      });
    }
    router.push('/vehicles');
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Błąd',
      detail: error.message || 'Nie udało się zapisać pojazdu',
      life: 3000,
    });
  } finally {
    saving.value = false;
  }
}

function goBack() {
  router.push('/vehicles');
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
      <ProgressSpinner/>
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
              <i class="pi pi-car"></i>
              Dane podstawowe
            </h3>
          </div>

          <FormField v-slot="$field" name="manufacturer">
            <label class="font-medium">Marka *</label>
            <InputText
                v-bind="$field"
                class="w-full"
                :invalid="!!$field?.invalid"
            />
            <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
              {{ $field.error?.message }}
            </Message>
          </FormField>

          <FormField v-slot="$field" name="model">
            <label class="font-medium">Model *</label>
            <InputText
                v-bind="$field"
                class="w-full"
                :invalid="!!$field?.invalid"
            />
            <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
              {{ $field.error?.message }}
            </Message>
          </FormField>

          <FormField v-slot="$field" name="licencePlate">
            <label class="font-medium">Nr rejestracyjny *</label>
            <InputText
                v-bind="$field"
                class="w-full"
                :invalid="!!$field?.invalid"
            />
            <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
              {{ $field.error?.message }}
            </Message>
          </FormField>

          <FormField v-slot="$field" name="vin">
            <label class="font-medium">VIN</label>
            <InputText
                v-bind="$field"
                class="w-full font-mono"
                :invalid="!!$field?.invalid"
            />
            <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
              {{ $field.error?.message }}
            </Message>
          </FormField>

          <FormField v-slot="$field" name="productionDate">
            <label class="font-medium">Data produkcji</label>
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

          <FormField v-slot="$field" name="color">
            <label class="font-medium">Kolor</label>
            <InputText
                v-bind="$field"
                class="w-full"
                :invalid="!!$field?.invalid"
            />
            <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
              {{ $field.error?.message }}
            </Message>
          </FormField>

          <!-- Dane techniczne -->
          <div class="md:col-span-2 mt-4">
            <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
              <i class="pi pi-cog"></i>
              Dane techniczne
            </h3>
          </div>

          <FormField v-slot="$field" name="fuelType">
            <label class="font-medium">Rodzaj paliwa *</label>
            <Select
                v-bind="$field"
                :options="FUEL_TYPE_OPTIONS"
                optionLabel="label"
                optionValue="value"
                placeholder="Wybierz rodzaj paliwa"
                class="w-full"
                :invalid="!!$field?.invalid"
            />
            <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
              {{ $field.error?.message }}
            </Message>
          </FormField>

          <FormField v-slot="$field" name="engineCapacity">
            <label class="font-medium">Pojemność silnika (cm³)</label>
            <InputNumber
                v-bind="$field"
                class="w-full"
                :minFractionDigits="0"
                :maxFractionDigits="1"
                :invalid="!!$field?.invalid"
            />
            <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
              {{ $field.error?.message }}
            </Message>
          </FormField>

          <FormField v-slot="$field" name="power">
            <label class="font-medium">Moc (kW)</label>
            <InputNumber
                v-bind="$field"
                class="w-full"
                :invalid="!!$field?.invalid"
            />
            <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
              {{ $field.error?.message }}
            </Message>
          </FormField>

          <!-- Właściciele -->
          <div class="md:col-span-2 mt-4">
            <h3 class="text-lg font-medium mb-4 flex items-center gap-2">
              <i class="pi pi-users"></i>
              Właściciele
            </h3>
          </div>

          <FormField v-slot="$field" name="clients" class="md:col-span-2">
            <label class="font-medium">Przypisani klienci</label>
            <MultiSelect
                v-bind="$field"
                :options="clientOptions"
                optionLabel="label"
                optionValue="value"
                placeholder="Wybierz klientów"
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
                :label="isEditMode ? 'Zapisz zmiany' : 'Dodaj pojazd'"
                :loading="saving"
                icon="pi pi-check"
            />
          </div>
        </Form>
      </template>
    </Card>

    <Toast/>
  </div>
</template>
