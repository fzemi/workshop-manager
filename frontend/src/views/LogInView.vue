<script setup>
import { useForm } from "vee-validate";
import { logInSchema } from "@/libs/schemas/logInSchemas.js";
import { useAuthStore } from "@/stores/AuthStore.js";
import { useToast } from "primevue/usetoast";
import errorMapper from "@/libs/errors/errorCodeMapper.js";

const { defineField, handleSubmit, errors } = useForm({
  validationSchema: logInSchema
});

const [username] = defineField("username");
const [password] = defineField("password");

const toast = useToast();

const onSubmit = handleSubmit((values) => {
  const authStore = useAuthStore();
  const { username, password } = values;

  return authStore.login(username, password)
      .catch(error => {
        const { code, message } = errorMapper(error);
        toast.add({ severity: "error", summary: `Błąd: ${code}`, detail: message, life: 5000 });
      });
});

</script>

<template>
  <Toast/>
  <form @submit="onSubmit" class="center">
    <div class="surface-card p-4 shadow-2 border-round w-full lg:w-3">
      <div class="text-center mb-5">
        <div class="text-900 text-3xl font-medium mb-3">Workshop Manager</div>
      </div>

      <div>
        <label for="username1" class="block text-900 font-medium mb-2">Nazwa użytkownika</label>
        <InputText id="username1"
                   type="text"
                   class="w-full"
                   v-model="username"
                   :class="{'p-invalid': errors.username, 'mb-3': !errors.username}"
        />
        <small id="username-help" class="p-error">
          {{ errors.username }}
        </small>

        <label for="password1"
               class="block text-900 font-medium mb-2"
               :class="{'mt-3': errors.username}"
        >Hasło</label>
        <InputText id="password1"
                   type="password"
                   class="w-full"
                   v-model="password"
                   :class="{'p-invalid': errors.password, 'mb-3': !errors.password}"
        />
        <small id="password-help" class="p-error ">
          {{ errors.password }}
        </small>

        <Button label="Log In"
                icon="pi pi-user"
                class="w-full"
                type="submit"
                :class="{'mt-3': errors.password}"
        >

        </Button>
      </div>
    </div>
  </form>
</template>

<style scoped>
.center {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
}
</style>