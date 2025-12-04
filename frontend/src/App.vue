<script setup>
import { storeToRefs } from 'pinia';
import WMHeader from '@/components/WMHeader.vue';
import { useAuthStore } from '@/stores/AuthStore.js';

const authStore = useAuthStore();
const { user: authUser } = storeToRefs(authStore);
</script>

<template>
    <div class="min-h-screen bg-surface-50 dark:bg-surface-900">
        <WMHeader v-if="authUser" />
        <main class="p-6">
            <div class="max-w-7xl mx-auto">
                <RouterView />
            </div>
        </main>
    </div>
</template>
