import * as Yup from "yup";
import { password, username } from "@/libs/common-schemas.js";

const logInSchema = Yup.object().shape({
    username: username.required("Nazwa użytkownika jest wymagana."),
    password: password.required("Hasło jest wymagane.")
});

export { logInSchema };