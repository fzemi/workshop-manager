<script setup>
import { useRouter } from "vue-router";
import { ref } from "vue";
import { useAuthStore } from "@/stores/AuthStore.js";

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

const authStore = useAuthStore();
const visibleLogoutDialog = ref(false);

</script>

<template>
  <div class="card">
    <Menubar :model="items">
      <template #end>
        <Button label="Wyloguj" icon="pi pi-power-off" @click="visibleLogoutDialog = true"/>
        <Dialog v-model:visible="visibleLogoutDialog" modal :draggable="false"
                :style="{ width: '25rem', height: '10rem' }">
          <template #header>
            <div class="inline-flex align-items-center justify-content-center gap-2">
              <span class="font-bold white-space-normal">Czy na pewno chcesz się wylogować?</span>
            </div>
          </template>
          <div class="flex justify-content-center">
            <Button label="Tak" @click="authStore.logout()" autofocus/>
          </div>
        </Dialog>
      </template>
    </Menubar>
  </div>
</template>

<style scoped>

</style>