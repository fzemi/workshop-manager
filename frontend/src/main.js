import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import PrimeVue from "primevue/config";
import configurePrimeServices from "@/prime-vue/primeServices.js";
import './assets/styles.css';
import { createPinia } from "pinia";
import Aura from '@primeuix/themes/aura';

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(PrimeVue, {
    theme: {
        preset: Aura
    }
});
configurePrimeServices(app);

app.mount('#app');
