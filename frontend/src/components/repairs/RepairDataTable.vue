<script setup>
import { onMounted, ref } from 'vue';
import RepairService from "@/service/RepairService.js";

const repairs = ref([]);
const mappedRepairs = ref([]);

const columns = [
  {field: 'number', header: 'Numer'},
  {field: 'vehicle', header: 'Auto'},
  {field: 'clients', header: 'Klient'},
  {field: 'type', header: 'Typ naprawy'},
  {field: 'startDate', header: 'Data rozpoczęcia'},
  {field: 'expectedEndDate', header: 'Data zakończenia'}
];

onMounted(async () => {
  repairs.value = await RepairService.getRepairsWithClients();
  mapRepairs(mappedRepairs);
});

function mapRepairs(repairsToMap) {
  repairsToMap.value = repairs.value.map(repair => {
    return {
      ...repair,
      vehicle: repair.vehicle.manufacturer
          + ' '
          + repair.vehicle.model,
      clients: repair.clients.map(client => client.firstname + ' ' + client.surname).join(', ')
    };
  });
}

</script>

<template>
  <div class="card">
    <DataTable :value="mappedRepairs" scrollable scrollHeight="400px">
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