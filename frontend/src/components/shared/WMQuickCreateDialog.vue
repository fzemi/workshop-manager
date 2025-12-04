<script setup>
import { ref, computed } from 'vue';
import { useToast } from 'primevue/usetoast';
import { zodResolver } from '@primevue/forms/resolvers/zod';
import ClientService from '@/service/ClientService.js';
import VehicleService from '@/service/VehicleService.js';
import { clientQuickCreateSchema, clientQuickCreateDefaultValues } from '@/libs/schemas/clientSchemas.js';
import { vehicleQuickCreateSchema, vehicleQuickCreateDefaultValues } from '@/libs/schemas/vehicleSchemas.js';
import { FUEL_TYPE_OPTIONS } from "@/libs/constants.js";

const props = defineProps({
  /**
   * Entity type to create ('client' or 'vehicle')
   */
  entityType: {
    type: String,
    required: true,
    validator: (value) => ['client', 'vehicle'].includes(value),
  },
});

const emit = defineEmits(['created', 'cancel']);

const toast = useToast();

const visible = ref(false);
const saving = ref(false);

const dialogTitle = computed(() => {
  return props.entityType === 'client'
      ? 'Szybkie dodawanie klienta'
      : 'Szybkie dodawanie pojazdu';
});

const resolver = computed(() => {
  return props.entityType === 'client'
      ? zodResolver(clientQuickCreateSchema)
      : zodResolver(vehicleQuickCreateSchema);
});

const initialValues = computed(() => {
  return props.entityType === 'client'
      ? { ...clientQuickCreateDefaultValues }
      : { ...vehicleQuickCreateDefaultValues };
});

function show() {
  visible.value = true;
}

function hide() {
  visible.value = false;
}

async function onFormSubmit({ valid, values }) {
  if (!valid) {
    return;
  }

  saving.value = true;
  try {
    let created;
    if (props.entityType === 'client') {
      created = await ClientService.create(values);
      toast.add({
        severity: 'success',
        summary: 'Sukces',
        detail: 'Klient został utworzony. Pamiętaj o uzupełnieniu pozostałych danych.',
        life: 5000,
      });
    } else {
      created = await VehicleService.create(values);
      toast.add({
        severity: 'success',
        summary: 'Sukces',
        detail: 'Pojazd został utworzony. Pamiętaj o uzupełnieniu pozostałych danych.',
        life: 5000,
      });
    }

    emit('created', created);
    hide();
  } catch (error) {
    toast.add({
      severity: 'error',
      summary: 'Błąd',
      detail: error.message || 'Nie udało się utworzyć rekordu',
      life: 3000,
    });
  } finally {
    saving.value = false;
  }
}

function onCancel() {
  emit('cancel');
  hide();
}

// Expose show/hide methods
defineExpose({ show, hide });
</script>

<template>
  <Dialog
      v-model:visible="visible"
      modal
      :draggable="false"
      :header="dialogTitle"
      :style="{ width: '30rem' }"
      :closable="!saving"
      :closeOnEscape="!saving"
  >
    <!-- Client quick create form -->
    <Form
        v-if="entityType === 'client'"
        :resolver="resolver"
        :initialValues="initialValues"
        @submit="onFormSubmit"
        class="flex flex-col gap-4"
    >
      <FormField v-slot="$field" name="firstname">
        <label class="font-medium">Imię *</label>
        <InputText
            v-bind="$field"
            class="w-full"
            :invalid="!!$field?.invalid"
            :disabled="saving"
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
            :disabled="saving"
        />
        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
          {{ $field.error?.message }}
        </Message>
      </FormField>

      <FormField v-slot="$field" name="phoneNumber">
        <label class="font-medium">Telefon *</label>
        <InputText
            v-bind="$field"
            class="w-full"
            :invalid="!!$field?.invalid"
            :disabled="saving"
        />
        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
          {{ $field.error?.message }}
        </Message>
      </FormField>

      <div class="flex justify-end gap-2 mt-4">
        <Button
            label="Anuluj"
            severity="secondary"
            outlined
            :disabled="saving"
            @click="onCancel"
        />
        <Button
            type="submit"
            label="Dodaj klienta"
            :loading="saving"
            icon="pi pi-check"
        />
      </div>
    </Form>

    <!-- Vehicle quick create form -->
    <Form
        v-else
        :resolver="resolver"
        :initialValues="initialValues"
        @submit="onFormSubmit"
        class="flex flex-col gap-4"
    >
      <FormField v-slot="$field" name="manufacturer">
        <label class="font-medium">Marka *</label>
        <InputText
            v-bind="$field"
            class="w-full"
            :invalid="!!$field?.invalid"
            :disabled="saving"
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
            :disabled="saving"
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
            :disabled="saving"
        />
        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
          {{ $field.error?.message }}
        </Message>
      </FormField>

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
            :disabled="saving"
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
            :disabled="saving"
        />
        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
          {{ $field.error?.message }}
        </Message>
      </FormField>

      <div class="flex justify-end gap-2 mt-4">
        <Button
            label="Anuluj"
            severity="secondary"
            outlined
            :disabled="saving"
            @click="onCancel"
        />
        <Button
            type="submit"
            label="Dodaj pojazd"
            :loading="saving"
            icon="pi pi-check"
        />
      </div>
    </Form>
  </Dialog>
</template>
