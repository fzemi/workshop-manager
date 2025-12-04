# Workshop Manager Frontend - AI Coding Instructions

## Project Overview
A Vue 3 frontend for a car workshop management system. Built with **PrimeVue 4.5+** component library, **Tailwind CSS v4**, and **Pinia** for state management.

**Language**: Polish (UI labels, error messages, validation)

---

## Tech Stack & Key Dependencies

| Technology | Version | Purpose |
|------------|---------|---------|
| Vue | 3.5+ | Frontend framework |
| Vite | 6.x | Build tool (requires Node.js 20.12+) |
| PrimeVue | 4.5+ | UI Component library (Aura theme) |
| Tailwind CSS | 4.x | Utility-first CSS |
| Pinia | 2.3+ | State management |
| Vue Router | 4.5+ | Routing |
| Zod | 3.24+ | Schema validation |

---

## Project Structure

```
src/
├── api/                    # HTTP layer
│   ├── index.js           # Re-exports httpService
│   └── httpService.js     # Fetch wrapper with auth headers
├── assets/
│   └── styles.css         # Global styles (Tailwind + PrimeUI imports)
├── components/            # Reusable components by domain
│   ├── clients/
│   │   └── ClientDataTable.vue
│   ├── repairs/
│   │   └── RepairDataTable.vue
│   ├── vehicles/
│   │   ├── VehicleCreator.vue
│   │   └── VehicleDataTable.vue
│   └── WMHeader.vue       # Main navigation (Menubar)
├── libs/                  # Shared utilities
│   ├── common-schemas.js  # Zod base schemas (ID, email, username, password)
│   ├── errors/
│   │   ├── errorCodes.js     # Error code constants with Polish messages
│   │   └── errorCodeMapper.js # Maps API errors to user messages
│   └── schemas/
│       └── logInSchemas.js   # Login form validation
├── prime-vue/
│   └── primeServices.js   # PrimeVue service configuration (Toast, etc.)
├── router/
│   └── index.js           # Vue Router config with auth guards
├── service/               # API service layer (domain-specific)
│   ├── ClientService.js
│   ├── RepairService.js
│   ├── VehicleService.js
│   └── config/
│       └── RequestEnums.js # Enum mappings (FuelFormat, RepairTypeFormat)
├── stores/
│   └── AuthStore.js       # Pinia auth store (login, logout, JWT)
├── views/                 # Route-level components
│   ├── ClientView.vue
│   ├── HomeView.vue
│   ├── LogInView.vue
│   ├── RepairView.vue
│   └── vehicles/
│       ├── VehicleDetailsView.vue
│       └── VehiclesView.vue
├── App.vue                # Root component
└── main.js                # App entry point
```

---

## Key Patterns & Conventions

### 1. PrimeVue Components (Auto-imported)
Components are **auto-imported** via `unplugin-vue-components` with `PrimeVueResolver`. No need to import PrimeVue components manually in templates.

```vue
<!-- Just use directly in template - no import needed -->
<template>
  <DataTable :value="items">
    <Column field="name" header="Name" />
  </DataTable>
  <Button label="Click me" />
  <Dialog v-model:visible="visible">...</Dialog>
</template>
```

**Commonly used components**: `DataTable`, `Column`, `Button`, `Dialog`, `InputText`, `Password`, `Toast`, `Menubar`, `AutoComplete`, `Calendar`, `Dropdown`, `Message`, `Form`, `FormField`

### 2. PrimeVue Forms with Zod Validation
Use `@primevue/forms` with `zodResolver` for form handling:

```vue
<script setup>
import { zodResolver } from '@primevue/forms/resolvers/zod';
import { z } from 'zod';

const schema = z.object({
  fieldName: z.string().nonempty("Pole jest wymagane.")
});
const resolver = zodResolver(schema);

const onFormSubmit = (e) => {
  if (!e.valid) return;
  // e.values contains form data
};
</script>

<template>
  <Form :resolver="resolver" @submit="onFormSubmit">
    <FormField v-slot="$field" name="fieldName" initialValue="">
      <InputText type="text" />
      <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">
        {{ $field.error?.message }}
      </Message>
    </FormField>
    <Button type="submit" label="Submit" />
  </Form>
</template>
```

### 3. Toast Notifications
Toast service is globally configured. Use in components:

```vue
<script setup>
import { useToast } from 'primevue/usetoast';

const toast = useToast();

// Success
toast.add({ severity: 'success', summary: 'Sukces', detail: 'Operacja zakończona.', life: 3000 });

// Error
toast.add({ severity: 'error', summary: 'Błąd', detail: 'Coś poszło nie tak.', life: 5000 });
</script>

<template>
  <Toast />  <!-- Include once in component -->
</template>
```

### 4. HTTP Service Pattern
All API calls go through `httpService` which handles auth headers and response parsing:

```javascript
import { httpService } from "@/api/index.js";

const API_URL = import.meta.env.VITE_API_URL;
const API_PORT = import.meta.env.VITE_API_PORT;

// Returns { data, status }
const response = await httpService.get(`${API_URL}:${API_PORT}/api/v1/endpoint`);
const response = await httpService.post(`${API_URL}:${API_PORT}/api/v1/endpoint`, bodyObject);
```

**Response format**: `{ data: object, status: number }`
**Error format**: `{ data: { errorDescription: string }, status: number }`

### 5. Service Layer Pattern
Domain services handle API calls and data transformation:

```javascript
// src/service/ExampleService.js
import { httpService } from "@/api/index.js";

const API_URL = import.meta.env.VITE_API_URL;
const API_PORT = import.meta.env.VITE_API_PORT;

const ExampleService = {
    getData: async function () {
        try {
            const response = await httpService.get(`${API_URL}:${API_PORT}/api/v1/examples`);
            // Transform data if needed
            return response;
        } catch (error) {
            throw new Error(error);
        }
    },
};

export default ExampleService;
```

### 6. Error Handling
Errors from API have `errorDescription` field. Use error mapper for Polish messages:

```javascript
import errorMapper from '@/libs/errors/errorCodeMapper.js';

try {
    await someApiCall();
} catch (error) {
    const { code, message } = errorMapper(error.data);
    toast.add({ severity: 'error', summary: `Błąd: ${code}`, detail: message, life: 5000 });
}
```

### 7. Pinia Store Pattern
```javascript
import { defineStore } from "pinia";

export const useExampleStore = defineStore("example", {
    state: () => ({
        data: null,
    }),
    actions: {
        async fetchData() {
            // ...
        }
    }
});
```

### 8. Router with Auth Guards
Routes require authentication by default. Add to `publicPages` array for public routes:

```javascript
// In router/index.js
const publicPages = ['/login'];  // Add public routes here
```

---

## Styling Guidelines

### Tailwind CSS v4 + PrimeUI
Global styles in `src/assets/styles.css`:
```css
@import "tailwindcss";
@import "tailwindcss-primeui";
@import 'primeicons/primeicons.css';
@import '@fortawesome/fontawesome-free/css/all.css';
```

### Use Tailwind utilities with PrimeVue
```vue
<template>
  <div class="flex flex-col gap-4 w-full sm:w-56">
    <Button class="mr-2" />
  </div>
</template>
```

### PrimeVue Aura Theme
Configured in `main.js` with Aura preset. Component styling via Tailwind utilities.

---

## Environment Variables
Required in `.env` file:
```
VITE_API_URL=http://localhost
VITE_API_PORT=8080
```

---

## Quick Reference

| Task | Pattern |
|------|---------|
| Add new view | Create in `views/`, add route in `router/index.js` |
| Add new component | Create in `components/{domain}/` |
| Add API endpoint | Create/update service in `service/` |
| Add form validation | Create Zod schema in `libs/schemas/` |
| Add error code | Add to `libs/errors/errorCodes.js` |
| Add enum mapping | Add to `service/config/RequestEnums.js` |

---

## PrimeVue Documentation
For component API and examples, refer to [prime-vue-docs.md](prime-vue-docs.md) or https://primevue.org/
