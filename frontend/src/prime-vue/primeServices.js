import { ToastService, ConfirmationService } from "primevue";

/**
 * Import and configure PrimeVue services, so that they don't have to be imported in every component.
 * @param app The Vue app instance.
 */
function configurePrimeServices(app) {
    app.use(ToastService);
    app.use(ConfirmationService);
}

export default configurePrimeServices;
