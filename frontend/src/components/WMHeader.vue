<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/AuthStore.js';

const router = useRouter();
const authStore = useAuthStore();

const items = ref([
    {
        label: 'Strona Główna',
        icon: 'pi pi-home',
        route: '/',
    },
    {
        label: 'Naprawy',
        icon: 'pi pi-wrench',
        route: '/repairs',
    },
    {
        label: 'Pojazdy',
        icon: 'pi pi-car',
        route: '/vehicles',
    },
    {
        label: 'Klienci',
        icon: 'pi pi-users',
        route: '/clients',
    },
]);

const showLogoutDialog = ref(false);

function handleLogout() {
    showLogoutDialog.value = false;
    authStore.logout();
}
</script>

<template>
    <header class="sticky top-0 z-50">
        <Menubar :model="items" class="border-0 rounded-none">
            <template #start>
                <span class="font-bold text-xl text-primary-600 mr-4 ml-2">
                    Workshop Manager
                </span>
            </template>
            <template #item="{ item, props }">
                <router-link v-slot="{ href, navigate }" :to="item.route" custom>
                    <a :href="href" v-bind="props.action" @click="navigate">
                        <span :class="item.icon" class="mr-2" />
                        <span>{{ item.label }}</span>
                    </a>
                </router-link>
            </template>
            <template #end>
                <Button
                    icon="pi pi-sign-out"
                    severity="secondary"
                    text
                    rounded
                    v-tooltip.bottom="'Wyloguj'"
                    @click="showLogoutDialog = true"
                />
            </template>
        </Menubar>

        <Dialog
            v-model:visible="showLogoutDialog"
            modal
            :draggable="false"
            header="Wylogowanie"
            :style="{ width: '25rem' }"
        >
            <div class="flex items-center gap-4 mb-6">
                <i class="pi pi-exclamation-triangle text-3xl text-yellow-500"></i>
                <span>Czy na pewno chcesz się wylogować?</span>
            </div>
            <div class="flex justify-end gap-2">
                <Button
                    label="Anuluj"
                    severity="secondary"
                    outlined
                    @click="showLogoutDialog = false"
                />
                <Button
                    label="Wyloguj"
                    severity="danger"
                    @click="handleLogout"
                />
            </div>
        </Dialog>
    </header>
</template>