<script setup>
import { useToast } from 'primevue/usetoast';
import { useAuthStore } from '@/stores/AuthStore.js';
import errorMapper from '@/libs/errors/errorCodeMapper.js';
import { logInSchema } from "@/libs/schemas/logInSchemas.js";
import { zodResolver } from '@primevue/forms/resolvers/zod';
import Toast from 'primevue/toast';

const toast = useToast();
const resolver = zodResolver(logInSchema);

const onFormSubmit = (e) => {
  if (!e.valid) return;

  const authStore = useAuthStore();
  const { username, password } = e.values;
  authStore.login(username, password)
      .catch(error => {
        const { code, message } = errorMapper(error);
        toast.add({ severity: 'error', summary: `Błąd: ${code}`, detail: message, life: 5000 });
      });
};


</script>

<template>
  <Toast/>
  <div class="center">
    <Form :resolver="resolver" @submit="onFormSubmit" class="flex flex-col gap-4 w-full sm:w-56">
      <FormField v-slot="$field" as="section" name="username" initialValue="" class="flex flex-col gap-2">
        <InputText type="text" placeholder="Nazwa użytkownika"/>
        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">{{
            $field.error?.message
          }}
        </Message>
      </FormField>
      <FormField v-slot="$field" as="section" name="password" initialValue="" class="flex flex-col gap-2">
        <Password type="password" placeholder="Hasło" :feedback="false" toggleMask fluid/>
        <Message v-if="$field?.invalid" severity="error" size="small" variant="simple">{{
            $field.error?.message
          }}
        </Message>
      </FormField>
      <Button type="submit" label="Zaloguj"/>
    </Form>
  </div>
</template>

<style scoped>
.center {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>
