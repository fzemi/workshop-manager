import { z } from 'zod';

/**
 * Client validation schemas with Polish messages
 */

/**
 * Schema for creating a new client (quick create - minimal fields)
 */
export const clientQuickCreateSchema = z.object({
    firstname: z
        .string({ required_error: 'Imię jest wymagane' })
        .min(1, 'Imię jest wymagane')
        .max(50, 'Imię może mieć maksymalnie 50 znaków'),
    surname: z
        .string({ required_error: 'Nazwisko jest wymagane' })
        .min(1, 'Nazwisko jest wymagane')
        .max(50, 'Nazwisko może mieć maksymalnie 50 znaków'),
    phoneNumber: z
        .string({ required_error: 'Numer telefonu jest wymagany' })
        .min(1, 'Numer telefonu jest wymagany')
        .max(20, 'Numer telefonu może mieć maksymalnie 20 znaków'),
});

/**
 * Schema for creating/editing a client (full form)
 */
export const clientSchema = z.object({
    firstname: z
        .string({ required_error: 'Imię jest wymagane' })
        .min(1, 'Imię jest wymagane')
        .max(50, 'Imię może mieć maksymalnie 50 znaków'),
    surname: z
        .string({ required_error: 'Nazwisko jest wymagane' })
        .min(1, 'Nazwisko jest wymagane')
        .max(50, 'Nazwisko może mieć maksymalnie 50 znaków'),
    pesel: z
        .string()
        .max(11, 'PESEL musi mieć 11 znaków')
        .regex(/^\d*$/, 'PESEL może zawierać tylko cyfry')
        .optional()
        .or(z.literal('')),
    nip: z
        .string()
        .max(10, 'NIP musi mieć 10 znaków')
        .regex(/^\d*$/, 'NIP może zawierać tylko cyfry')
        .optional()
        .or(z.literal('')),
    phoneNumber: z
        .string({ required_error: 'Numer telefonu jest wymagany' })
        .min(1, 'Numer telefonu jest wymagany')
        .max(20, 'Numer telefonu może mieć maksymalnie 20 znaków'),
    email: z
        .string()
        .email('Nieprawidłowy format adresu email')
        .optional()
        .or(z.literal('')),
    country: z
        .string()
        .max(50, 'Nazwa kraju może mieć maksymalnie 50 znaków')
        .optional()
        .or(z.literal('')),
    postalCode: z
        .string()
        .max(10, 'Kod pocztowy może mieć maksymalnie 10 znaków')
        .optional()
        .or(z.literal('')),
    city: z
        .string()
        .max(100, 'Nazwa miasta może mieć maksymalnie 100 znaków')
        .optional()
        .or(z.literal('')),
    address: z
        .string()
        .max(200, 'Adres może mieć maksymalnie 200 znaków')
        .optional()
        .or(z.literal('')),
    birthDate: z
        .date({ invalid_type_error: 'Nieprawidłowy format daty' })
        .optional()
        .nullable(),
    vehicles: z
        .array(z.number())
        .optional()
        .default([]),
});

/**
 * Default values for client form
 */
export const clientDefaultValues = {
    firstname: '',
    surname: '',
    pesel: '',
    nip: '',
    phoneNumber: '',
    email: '',
    country: 'Polska',
    postalCode: '',
    city: '',
    address: '',
    birthDate: null,
    vehicles: [],
};

/**
 * Default values for quick create client form
 */
export const clientQuickCreateDefaultValues = {
    firstname: '',
    surname: '',
    phoneNumber: '',
};
