import { z } from "zod";
import { password, username } from "@/libs/common-schemas.js";

const logInSchema = z.object({
    username: username.nonempty("Nazwa użytkownika jest wymagana."),
    password: password.nonempty("Hasło jest wymagane.")
});

export { logInSchema };
