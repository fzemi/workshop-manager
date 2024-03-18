<script setup>
import { useRouter } from "vue-router";
import { ref } from "vue";

const router = useRouter();

const items = ref([
  {
    label: 'Strona Główna',
    command: () => {
      router.push('/');
    }
  },
  {
    label: 'Naprawy',
    command: () => {
      router.push('/repairs');
    }
  },
  {
    label: 'Auta',
    command: () => {
      router.push('/vehicles');
    }
  },
  {
    label: 'Klienci',
    command: () => {
      router.push('/clients');
    }
  }
]);

</script>

<template>
  <div class="card">
    <Menubar :model="items"/>
  </div>
  <!--  <header>-->
  <!--    <nav class="container">-->
  <!--      <div class="branding">-->
  <!--        <h1>Manager</h1>-->
  <!--      </div>-->
  <!--      <ul class="nav-routes">-->
  <!--        <RouterLink to="/">Strona główna</RouterLink>-->
  <!--        <RouterLink to="/vehicles">Auta</RouterLink>-->
  <!--      </ul>-->
  <!--    </nav>-->
  <!--  </header>-->
</template>

<style scoped>

</style>