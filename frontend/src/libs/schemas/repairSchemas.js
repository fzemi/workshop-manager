import { z } from 'zod';

/**
 * Repair validation schemas with Polish messages
 */

/**
 * Valid repair type values
 */
const repairTypeValues = ['BODY', 'MECHANICAL'];

/**
 * Schema for creating/editing a repair
 */
export const repairSchema = z.object({
    number: z
        .string({ required_error: 'Numer naprawy jest wymagany' })
        .min(1, 'Numer naprawy jest wymagany')
        .max(20, 'Numer naprawy może mieć maksymalnie 20 znaków'),
    startDate: z
        .date({ 
            required_error: 'Data rozpoczęcia jest wymagana',
            invalid_type_error: 'Nieprawidłowy format daty' 
        }),
    expectedEndDate: z
        .date({ 
            required_error: 'Przewidywana data zakończenia jest wymagana',
            invalid_type_error: 'Nieprawidłowy format daty' 
        }),
    type: z
        .enum(repairTypeValues, { 
            message: 'Wybierz typ naprawy'
        }),
    vehicleId: z
        .number({ 
            required_error: 'Pojazd jest wymagany',
            invalid_type_error: 'Wybierz pojazd' 
        })
        .positive('Wybierz pojazd'),
}).refine(
    (data) => data.expectedEndDate >= data.startDate,
    {
        message: 'Data zakończenia nie może być wcześniejsza niż data rozpoczęcia',
        path: ['expectedEndDate'],
    }
);

/**
 * Default values for repair form
 */
export const repairDefaultValues = {
    number: '',
    startDate: new Date(),
    expectedEndDate: null,
    type: null,
    vehicleId: null,
};
