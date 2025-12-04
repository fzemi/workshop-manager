import { z } from 'zod';

/**
 * Common schemas for frontend
 */

/**
 * Universal props
 */
const ID = z
    .number()
    .int();

/**
 * Users props
 */
const email = z
    .string()
    .email()
    .trim();
const username = z
    .string()
    .trim();
const password = z
    .string()
    .trim();

export {
    ID,
    email,
    username,
    password
};
