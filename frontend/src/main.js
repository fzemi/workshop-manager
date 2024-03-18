import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import PrimeVue from "primevue/config";
import configurePrimeComponents from "@/prime-vue/primeComponents.js";
import 'primevue/resources/themes/aura-light-green/theme.css';
import 'primeflex/primeflex.css';
import '@fortawesome/fontawesome-free/css/all.css';

const app = createApp(App);

app.use(router);
app.use(PrimeVue);
configurePrimeComponents(app);

app.mount('#app');
