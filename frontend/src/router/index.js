import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from "@/stores/AuthStore.js";

import HomeView from '@/views/HomeView.vue';
import LogInView from "@/views/LogInView.vue";


const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView
        },
        {
            path: '/login',
            name: 'login',
            component: LogInView
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

router.beforeEach(async (to) => {
    const publicPages = ['/login'];
    const authRequired = !publicPages.includes(to.path);
    const auth = useAuthStore();

    if (authRequired && !auth.user) {
        auth.returnUrl = to.fullPath;
        return '/login';
    }
});

export default router;
