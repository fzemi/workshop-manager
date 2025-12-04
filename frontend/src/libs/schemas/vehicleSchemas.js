import { z } from 'zod';

/**
 * Vehicle validation schemas with Polish messages
 */

/**
 * Valid fuel type values
 */
const fuelTypeValues = ['GASOLINE', 'DIESEL', 'LPG', 'ELECTRIC'];

/**
 * Schema for creating a new vehicle (quick create - minimal fields)
 */
export const vehicleQuickCreateSchema = z.object({
    manufacturer: z
        .string({ required_error: 'Marka jest wymagana' })
        .min(1, 'Marka jest wymagana')
        .max(50, 'Marka może mieć maksymalnie 50 znaków'),
    model: z
        .string({ required_error: 'Model jest wymagany' })
        .min(1, 'Model jest wymagany')
        .max(50, 'Model może mieć maksymalnie 50 znaków'),
    licencePlate: z
        .string({ required_error: 'Numer rejestracyjny jest wymagany' })
        .min(1, 'Numer rejestracyjny jest wymagany')
        .max(15, 'Numer rejestracyjny może mieć maksymalnie 15 znaków'),
    fuelType: z
        .enum(fuelTypeValues, {
            errorMap: () => ({ message: 'Wybierz rodzaj paliwa' })
        }),
    vin: z
        .string()
        .max(17, 'VIN może mieć maksymalnie 17 znaków')
        .optional()
        .or(z.literal('')),
});

/**
 * Schema for creating/editing a vehicle (full form)
 */
export const vehicleSchema = z.object({
    vin: z
        .string()
        .max(17, 'VIN może mieć maksymalnie 17 znaków')
        .optional()
        .or(z.literal('')),
    manufacturer: z
        .string({ required_error: 'Marka jest wymagana' })
        .min(1, 'Marka jest wymagana')
        .max(50, 'Marka może mieć maksymalnie 50 znaków'),
    model: z
        .string({ required_error: 'Model jest wymagany' })
        .min(1, 'Model jest wymagany')
        .max(50, 'Model może mieć maksymalnie 50 znaków'),
    licencePlate: z
        .string({ required_error: 'Numer rejestracyjny jest wymagany' })
        .min(1, 'Numer rejestracyjny jest wymagany')
        .max(15, 'Numer rejestracyjny może mieć maksymalnie 15 znaków'),
    productionDate: z
        .date({ invalid_type_error: 'Nieprawidłowy format daty' })
        .optional()
        .nullable(),
    color: z
        .string()
        .max(30, 'Kolor może mieć maksymalnie 30 znaków')
        .optional()
        .or(z.literal('')),
    engineCapacity: z
        .number({ invalid_type_error: 'Pojemność silnika musi być liczbą' })
        .positive('Pojemność silnika musi być większa od 0')
        .optional()
        .nullable(),
    fuelType: z
        .enum(fuelTypeValues, {
            errorMap: () => ({ message: 'Wybierz rodzaj paliwa' })
        }),
    power: z
        .number({ invalid_type_error: 'Moc musi być liczbą' })
        .int('Moc musi być liczbą całkowitą')
        .positive('Moc musi być większa od 0')
        .optional()
        .nullable(),
    clients: z
        .array(z.number())
        .optional()
        .default([]),
});

/**
 * Default values for vehicle form
 */
export const vehicleDefaultValues = {
    vin: '',
    manufacturer: '',
    model: '',
    licencePlate: '',
    productionDate: null,
    color: '',
    engineCapacity: null,
    fuelType: null,
    power: null,
    clients: [],
};

/**
 * Default values for quick create vehicle form
 */
export const vehicleQuickCreateDefaultValues = {
    manufacturer: '',
    model: '',
    licencePlate: '',
    vin: '',
    fuelType: null,
};
