<script setup>
import { ref, onMounted } from 'vue';
import VehicleService from "@/service/VehicleService.js";

const vehicles = ref([]);
const mappedVehicles = ref([]);

const columns = [
  {field: 'manufacturer', header: 'Marka'},
  {field: 'model', header: 'Model'},
  {field: 'licencePlate', header: 'Tablica rejestracyjna'},
  {field: 'vin', header: 'VIN'},
  {field: 'productionDate', header: 'Data produkcji'},
  {field: 'color', header: 'Kolor'},
  {field: 'engineCapacity', header: 'Pojemność silnika'},
  {field: 'fuelType', header: 'Paliwo'},
  {field: 'power', header: 'Moc (kW)'},
  {field: 'clients', header: 'Właściciel'}
];

onMounted(async () => {
  vehicles.value = await VehicleService.getVehiclesData();
  mapVehicles(mappedVehicles);
});

function mapVehicles(vehiclesToMap) {
  vehiclesToMap.value = vehicles.value.map(vehicle => {
    return {
      ...vehicle,
      clients: vehicle.clients.map(client => client.firstname + ' ' + client.surname).join(', ')
    };
  });
}

async function updateVehicleList() {
  vehicles.value = await VehicleService.getVehiclesData();
}

</script>

<template>
  <div class="card">
    <DataTable :value="mappedVehicles" scrollable scrollHeight="400px">
      <Column v-for="column in columns"
              :key="column.field"
              :field="column.field"
              :header="column.header"
              style="min-width: 150px"
      ></Column>
    </DataTable>
  </div>
</template>

<style scoped>
.card {
  width: 100%;
}
</style>