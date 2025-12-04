import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/AuthStore.js';

import HomeView from '@/views/HomeView.vue';
import LogInView from '@/views/LogInView.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
            meta: { title: 'Strona główna' },
        },
        {
            path: '/login',
            name: 'login',
            component: LogInView,
            meta: { title: 'Logowanie' },
        },
        // Clients
        {
            path: '/clients',
            name: 'clients',
            component: () => import('@/views/clients/ClientListView.vue'),
            meta: { title: 'Klienci' },
        },
        {
            path: '/clients/new',
            name: 'client-create',
            component: () => import('@/views/clients/ClientFormView.vue'),
            meta: { title: 'Dodaj klienta' },
        },
        {
            path: '/clients/:id',
            name: 'client-detail',
            component: () => import('@/views/clients/ClientDetailView.vue'),
            meta: { title: 'Szczegóły klienta' },
        },
        {
            path: '/clients/:id/edit',
            name: 'client-edit',
            component: () => import('@/views/clients/ClientFormView.vue'),
            meta: { title: 'Edytuj klienta' },
        },
        // Vehicles
        {
            path: '/vehicles',
            name: 'vehicles',
            component: () => import('@/views/vehicles/VehicleListView.vue'),
            meta: { title: 'Pojazdy' },
        },
        {
            path: '/vehicles/new',
            name: 'vehicle-create',
            component: () => import('@/views/vehicles/VehicleFormView.vue'),
            meta: { title: 'Dodaj pojazd' },
        },
        {
            path: '/vehicles/:id',
            name: 'vehicle-detail',
            component: () => import('@/views/vehicles/VehicleDetailView.vue'),
            meta: { title: 'Szczegóły pojazdu' },
        },
        {
            path: '/vehicles/:id/edit',
            name: 'vehicle-edit',
            component: () => import('@/views/vehicles/VehicleFormView.vue'),
            meta: { title: 'Edytuj pojazd' },
        },
        // Repairs
        {
            path: '/repairs',
            name: 'repairs',
            component: () => import('@/views/repairs/RepairListView.vue'),
            meta: { title: 'Naprawy' },
        },
        {
            path: '/repairs/new',
            name: 'repair-create',
            component: () => import('@/views/repairs/RepairFormView.vue'),
            meta: { title: 'Dodaj naprawę' },
        },
        {
            path: '/repairs/:id',
            name: 'repair-detail',
            component: () => import('@/views/repairs/RepairDetailView.vue'),
            meta: { title: 'Szczegóły naprawy' },
        },
        {
            path: '/repairs/:id/edit',
            name: 'repair-edit',
            component: () => import('@/views/repairs/RepairFormView.vue'),
            meta: { title: 'Edytuj naprawę' },
        },
    ],
});

// Auth guard
router.beforeEach(async (to) => {
    const publicPages = ['/login'];
    const authRequired = !publicPages.includes(to.path);
    const auth = useAuthStore();

    if (authRequired && !auth.user) {
        auth.returnUrl = to.fullPath;
        return '/login';
    }
});

// Update document title
router.afterEach((to) => {
    const baseTitle = 'Workshop Manager';
    document.title = to.meta.title ? `${to.meta.title} | ${baseTitle}` : baseTitle;
});

export default router;
