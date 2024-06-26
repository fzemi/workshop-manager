import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import PrimeVue from "primevue/config";
import configurePrimeComponents from "@/prime-vue/primeComponents.js";
import 'primevue/resources/themes/aura-light-green/theme.css';
import 'primeflex/primeflex.css';
import 'primeicons/primeicons.css';
import '@fortawesome/fontawesome-free/css/all.css';
import { createPinia } from "pinia";

const app = createApp(App);

app.use(createPinia());
app.use(router);
app.use(PrimeVue);
configurePrimeComponents(app);

app.mount('#app');
