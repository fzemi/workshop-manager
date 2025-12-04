/**
 * Application constants and enum mappings
 * All labels are in Polish
 */

/**
 * Fuel type options for vehicles
 */
export const FUEL_TYPES = {
    GASOLINE: 'Benzyna',
    DIESEL: 'Diesel',
    LPG: 'LPG',
    ELECTRIC: 'Elektryczny',
};

/**
 * Fuel type options for PrimeVue Select/Dropdown components
 */
export const FUEL_TYPE_OPTIONS = [
    { label: 'Benzyna', value: 'GASOLINE' },
    { label: 'Diesel', value: 'DIESEL' },
    { label: 'LPG', value: 'LPG' },
    { label: 'Elektryczny', value: 'ELECTRIC' },
];

/**
 * Repair type options
 */
export const REPAIR_TYPES = {
    BODY: 'Blacharsko-lakiernicza',
    MECHANICAL: 'Mechaniczna',
};

/**
 * Repair type options for PrimeVue Select/Dropdown components
 */
export const REPAIR_TYPE_OPTIONS = [
    { label: 'Blacharsko-lakiernicza', value: 'BODY' },
    { label: 'Mechaniczna', value: 'MECHANICAL' },
];

/**
 * Get Polish label for fuel type
 * @param {string} fuelType - Fuel type enum value
 * @returns {string} Polish label
 */
export function getFuelTypeLabel(fuelType) {
    return FUEL_TYPES[fuelType] || fuelType;
}

/**
 * Get Polish label for repair type
 * @param {string} repairType - Repair type enum value
 * @returns {string} Polish label
 */
export function getRepairTypeLabel(repairType) {
    return REPAIR_TYPES[repairType] || repairType;
}

/**
 * API Base URL constructed from environment variables
 */
export const API_BASE_URL = `${import.meta.env.VITE_API_URL}:${import.meta.env.VITE_API_PORT}/api/v1`;
