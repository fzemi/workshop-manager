<script setup>
import { ref, reactive } from "vue";
import VehicleService from "@/service/VehicleService.js";
import ClientService from "@/service/ClientService.js";
import { FuelFormat } from "@/service/config/RequestEnums.js";
import { useToast } from "primevue/usetoast";

const visible = ref(false);
const vehicleForm = reactive({
  vin: null,
  manufacturer: null,
  model: null,
  licencePlate: null,
  productionDate: null,
  color: null,
  engineCapacity: null,
  fuelType: "GASOLINE",
  power: null,
  clients: [], // clientIDs
});
const allClients = ref([]);
const filteredClients = ref([]);

const fuelTypes = ref(Object.values(FuelFormat));
const toast = useToast();

async function saveVehicle() {
  // TODO: Add validation
  const response = await VehicleService.createVehicle(vehicleForm);


  if (response.status === 201) {
    toast.add({severity: "success", summary: "Sukces", detail: "Pomyślnie dodano nowe auto.", life: 3000});
    visible.value = false;

    vehicleForm.clients = vehicleForm.clients.map(client => {
      return {id: allClients.value.find(c => c.fullName.includes(client)).id};
    });

    const responseJson = await response.json();
    const responseAddClients = await VehicleService.addVehicleClients(responseJson.id, vehicleForm.clients);

    if (responseAddClients.status !== 200) {
      toast.add({
        severity: "error",
        summary: "Błąd",
        detail: "Nie udało się dodać właścicieli do nowego auta.",
        life: 3000
      });
    }
  } else {
    toast.add({severity: "error", summary: "Błąd", detail: "Nie udało się dodać nowego auta.", life: 3000});
  }
  allClients.value = [];
}

async function fetchClients() {
  // if (allClients.value.length === 0) {
  allClients.value = await ClientService.getClientsIdAndName();
  // }
}

function search(event) {
  filteredClients.value = allClients.value
      .filter(client => client.fullName.toLowerCase().includes(event.query.toLowerCase()))
      .map(client => client.fullName);
}

</script>

<template>
  <div class="card flex justify-content-center">
    <Toast/>
    <Button label="Dodaj" icon="fa-solid fa-plus" @click="visible = true"/>
    <Dialog v-model:visible="visible" modal :draggable="false" :style="{ width: '50rem', height: '60rem' }">
      <template #header>
        <div class="inline-flex align-items-center justify-content-center gap-2">
          <span class="font-bold white-space-nowrap">Dodaj nowe auto</span>
        </div>
      </template>
      <span class="p-text-secondary block mb-5">Update your information.</span>
      <div class="flex align-items-center gap-3 mb-3">
        <label for="manufacturer" class="font-semibold w-6rem">Marka</label>
        <!--        Dodać autocomplete-->
        <InputText id="manufacturer"
                   class="flex-auto"
                   autocomplete="off"
                   v-model="vehicleForm.manufacturer"
        />
      </div>
      <div class="flex align-items-center gap-3 mb-2">
        <label for="model" class="font-semibold w-6rem">Model</label>
        <InputText id="model"
                   class="flex-auto"
                   autocomplete="off"
                   v-model="vehicleForm.model"
        />
      </div>
      <div class="flex align-items-center gap-3 mb-2">
        <label for="licencePlate" class="font-semibold w-6rem">Tablica rejestracyjna</label>
        <InputText id="licencePlate"
                   class="flex-auto"
                   autocomplete="off"
                   v-model="vehicleForm.licencePlate"
        />
      </div>
      <div class="flex align-items-center gap-3 mb-2">
        <label for="vin" class="font-semibold w-6rem">Numer VIN</label>
        <InputText id="vin"
                   class="flex-auto"
                   autocomplete="off"
                   v-model="vehicleForm.vin"
        />
      </div>
      <div class="flex align-items-center gap-3 mb-2">
        <label for="color" class="font-semibold w-6rem">Kolor</label>
        <InputText id="color"
                   class="flex-auto"
                   autocomplete="off"
                   v-model="vehicleForm.color"
        />
      </div>
      <div class="flex align-items-center gap-3 mb-2">
        <label for="productionDate" class="font-semibold w-6rem">Data produkcji</label>
        <Calendar id="productionDate"
                  class="flex-auto"
                  dateFormat="yy-mm-dd"
                  v-model="vehicleForm.productionDate"
        />
      </div>
      <div class="flex align-items-center gap-3 mb-2">
        <label for="fuelType" class="font-semibold w-6rem">Paliwo</label>
        <Dropdown id="fuelType"
                  class="flex-auto"
                  placeholder="Wybierz paliwo"
                  v-model="vehicleForm.fuelType"
                  :options="fuelTypes"
        />
      </div>
      <div class="flex align-items-center gap-3 mb-2">
        <label for="clients" class="font-semibold w-6rem">Właściciel</label>
        <AutoComplete multiple
                      id="clients"
                      class="flex-auto"
                      placeholder="Wpisz imię i nazwisko klienta"
                      v-model="vehicleForm.clients"
                      :suggestions="filteredClients"
                      @focus="fetchClients"
                      @complete="search"
        />
      </div>
      <template #footer>
        <Button label="Anuluj" text severity="secondary" @click="visible = false" autofocus/>
        <Button label="Zapisz" outlined severity="secondary" @click="saveVehicle" autofocus/>
      </template>
    </Dialog>
  </div>
</template>

<style scoped>

</style>