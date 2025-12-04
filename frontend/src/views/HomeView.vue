<script setup>
import { useRouter } from 'vue-router';

const router = useRouter();

const quickActions = [
    {
        label: 'Naprawy',
        icon: 'pi pi-wrench',
        description: 'Zarządzaj naprawami pojazdów',
        route: '/repairs',
        color: 'bg-blue-500',
    },
    {
        label: 'Pojazdy',
        icon: 'pi pi-car',
        description: 'Przeglądaj i dodawaj pojazdy',
        route: '/vehicles',
        color: 'bg-green-500',
    },
    {
        label: 'Klienci',
        icon: 'pi pi-users',
        description: 'Zarządzaj bazą klientów',
        route: '/clients',
        color: 'bg-purple-500',
    },
];

function navigateTo(route) {
    router.push(route);
}
</script>

<template>
    <div>
        <div class="mb-8">
            <h1 class="text-3xl font-bold text-surface-800 dark:text-surface-100 mb-2">
                Witaj w Workshop Manager
            </h1>
            <p class="text-surface-600 dark:text-surface-400">
                System zarządzania warsztatem samochodowym
            </p>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card
                v-for="action in quickActions"
                :key="action.route"
                class="cursor-pointer hover:shadow-lg transition-shadow"
                @click="navigateTo(action.route)"
            >
                <template #content>
                    <div class="flex items-start gap-4">
                        <div
                            :class="[action.color, 'w-12 h-12 rounded-lg flex items-center justify-center']"
                        >
                            <i :class="[action.icon, 'text-white text-xl']"></i>
                        </div>
                        <div class="flex-1">
                            <h3 class="text-lg font-semibold text-surface-800 dark:text-surface-100 mb-1">
                                {{ action.label }}
                            </h3>
                            <p class="text-sm text-surface-600 dark:text-surface-400">
                                {{ action.description }}
                            </p>
                        </div>
                        <i class="pi pi-chevron-right text-surface-400"></i>
                    </div>
                </template>
            </Card>
        </div>

        <div class="mt-8">
            <Card>
                <template #title>
                    <div class="flex items-center gap-2">
                        <i class="pi pi-info-circle"></i>
                        Szybki start
                    </div>
                </template>
                <template #content>
                    <div class="grid grid-cols-1 md:grid-cols-2 gap-4">
                        <div class="flex items-start gap-3">
                            <span class="bg-primary-100 text-primary-600 w-8 h-8 rounded-full flex items-center justify-center font-bold">1</span>
                            <div>
                                <p class="font-medium">Dodaj klienta</p>
                                <p class="text-sm text-surface-500">Utwórz wpis dla nowego klienta warsztatu</p>
                            </div>
                        </div>
                        <div class="flex items-start gap-3">
                            <span class="bg-primary-100 text-primary-600 w-8 h-8 rounded-full flex items-center justify-center font-bold">2</span>
                            <div>
                                <p class="font-medium">Dodaj pojazd</p>
                                <p class="text-sm text-surface-500">Zarejestruj pojazd i przypisz go do klienta</p>
                            </div>
                        </div>
                        <div class="flex items-start gap-3">
                            <span class="bg-primary-100 text-primary-600 w-8 h-8 rounded-full flex items-center justify-center font-bold">3</span>
                            <div>
                                <p class="font-medium">Utwórz naprawę</p>
                                <p class="text-sm text-surface-500">Rozpocznij nową naprawę dla pojazdu</p>
                            </div>
                        </div>
                        <div class="flex items-start gap-3">
                            <span class="bg-primary-100 text-primary-600 w-8 h-8 rounded-full flex items-center justify-center font-bold">4</span>
                            <div>
                                <p class="font-medium">Zarządzaj naprawami</p>
                                <p class="text-sm text-surface-500">Śledź postęp i aktualizuj status napraw</p>
                            </div>
                        </div>
                    </div>
                </template>
            </Card>
        </div>
    </div>
</template>
