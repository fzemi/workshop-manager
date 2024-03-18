import Menubar from "primevue/menubar";
import Dialog from 'primevue/dialog';
import Button from 'primevue/button';
import DataTable from 'primevue/datatable';
import Column from 'primevue/column';
import InputText from "primevue/inputtext";
import Calendar from "primevue/calendar";
import Dropdown from "primevue/dropdown";
import Toast from "primevue/toast";
import AutoComplete from "primevue/autocomplete";
import ToastService from "primevue/toastservice";


/**
 * Import and configure PrimeVue components, so that they don't have to be imported in every component.
 * @param app The Vue app instance.
 */
function configurePrimeComponents(app) {
    app.component('Menubar', Menubar);
    app.component('Dialog', Dialog);
    app.component('Button', Button);
    app.component('DataTable', DataTable);
    app.component('Column', Column);
    app.component('InputText', InputText);
    app.component('Calendar', Calendar);
    app.component('Dropdown', Dropdown);
    app.component('Toast', Toast);
    app.component('AutoComplete', AutoComplete);

    app.use(ToastService);
}

export default configurePrimeComponents;
