<script setup>
import { onMounted, ref } from 'vue';
import ClientService from "@/service/ClientService.js";

const clients = ref([]);
const mappedClients = ref([]);

const columns = [
  {field: 'firstname', header: 'Imię'},
  {field: 'surname', header: 'Nazwisko'},
  {field: 'pesel', header: 'Pesel'},
  {field: 'nip', header: 'NIP'},
  {field: 'phoneNumber', header: 'Numer telefonu'},
  {field: 'email', header: 'Email'},
  {field: 'country', header: 'Kraj'},
  {field: 'postalCode', header: 'Kod pocztowy'},
  {field: 'city', header: 'Miasto'},
  {field: 'address', header: 'Adres'},
  {field: 'birthDate', header: 'Data urodzenia'},
  {field: 'vehicles', header: 'Auta'},
];

onMounted(async () => {
  clients.value = await ClientService.getClientsData();
  mapClients(mappedClients);
});

function mapClients(clientsToMap) {
  clientsToMap.value = clients.value.map(client => {
    return {
      ...client,
      vehicles: client.vehicles.map(vehicle =>
          vehicle.manufacturer
          + ' '
          + vehicle.model
          + ' '
          + vehicle.licencePlate
      ).join(', ')
    };
  });
}


</script>

<template>
  <div class="card">
    <DataTable :value="mappedClients" scrollable scrollHeight="400px">
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