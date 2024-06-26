<script setup>
import { RouterLink, RouterView } from 'vue-router';
import WMHeader from "@/components/WMHeader.vue";
import { useAuthStore } from "@/stores/AuthStore.js";
import { storeToRefs } from "pinia";

const authStore = useAuthStore();
const { user: authUser } = storeToRefs(authStore);

</script>

<template>
  <WMHeader v-if="authUser"/>
  <RouterView/>
</template>

<style>

</style>