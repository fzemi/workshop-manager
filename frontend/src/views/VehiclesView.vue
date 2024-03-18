<script setup>
import VehicleDataTable from "@/components/vehicles/VehicleDataTable.vue";
import VehicleCreator from "@/components/vehicles/VehicleCreator.vue";

</script>

<template>
  <main>
    <VehicleCreator/>
    <div class="container flex justify-content-center align-items-center">
      <VehicleDataTable/>
    </div>
  </main>
</template>

<style scoped>
.container {
  height: 50vh;
}

</style>