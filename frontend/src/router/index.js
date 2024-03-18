import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/vehicles',
            name: 'vehicles',
            // route level code-splitting
            // this generates a separate chunk (About.[hash].js) for this route
            // which is lazy-loaded when the route is visited.
            component: () => import('@/views/VehiclesView.vue')
        },
        {
            path: '/clients',
            name: 'clients',
            component: () => import('@/views/ClientView.vue')
        },
        {
            path: '/repairs',
            name: 'repairs',
            component: () => import('@/views/RepairView.vue')
        }
    ]
});

export default router;
