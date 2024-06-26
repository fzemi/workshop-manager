import * as Yup from 'yup';

/**
 * Common schemas for frontend
 */

/**
 * Universal props
 */
const ID = Yup
    .number()
    .integer();

/**
 * Users props
 */
const email = Yup
    .string()
    .email()
    .trim();
const username = Yup
    .string()
    .trim();
const password = Yup
    .string()
    .trim();

export {
    ID,
    email,
    username,
    password
};