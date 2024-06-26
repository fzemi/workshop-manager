import { defineStore } from "pinia";
import { httpService } from "@/api/index.js";
import router from "@/router/index.js";

const API_URL = import.meta.env.VITE_API_URL;
const API_PORT = import.meta.env.VITE_API_PORT;
const baseUrl = `${API_URL}:${API_PORT}/api/v1/auth`;

export const useAuthStore = defineStore({
    id: "auth",
    state: () => ({
        user: JSON.parse(localStorage.getItem("user")),
        returnUrl: null
    }),
    actions: {
        async login(username, password) {
            const user = await httpService.post(`${baseUrl}/login`, { username, password });

            this.user = user;

            localStorage.setItem("user", JSON.stringify(user));

            // redirect to previous url or default to home page
            router.push(this.returnUrl || "/");
        },
        logout() {
            this.user = null;
            localStorage.removeItem("user");
            router.push("/login");
        }
    }
});