import { formatDate } from '@/libs/dateUtils.js';

/**
 * Maps repair, client, and vehicle data to document field values.
 * Returns a flat object with keys matching data-field attributes in templates.
 *
 * @param {Object} repair - Repair object
 * @param {Object} client - Client object (first client from repair.clients)
 * @param {Object} vehicle - Vehicle object
 * @returns {Object} Flat object with field values
 */
export function mapRepairToDocumentData(repair, client, vehicle) {
    return {
        // Repair fields
        'repair.orderNumber': repair?.number || '',
        'repair.orderDate': formatDate(repair?.startDate) || '',
        'repair.insuranceCompany': '',     // Mock - user will fill
        'repair.damageNumber': '',         // Mock - user will fill
        'repair.damageDate': '',           // Mock - user will fill

        // Client fields
        'client.fullName': formatFullName(client),
        'client.address': formatFullAddress(client),
        'client.addressLine1': formatAddressLine1(client),
        'client.addressLine2': client?.address || '',
        'client.peselOrNip': client?.nip || client?.pesel || '',
        'client.phone': client?.phoneNumber || '',
        'client.nip': client?.nip || '',
        'client.isVatPayer': false,        // Mock - boolean
        'client.canDeductVat': false,      // Mock - boolean

        // Vehicle fields
        'vehicle': formatVehicleName(vehicle),
        'vehicle.brandModel': formatVehicleName(vehicle),
        'vehicle.registrationNumber': vehicle?.licencePlate || '',

        // Document fields
        'document.number': repair?.number || '',

        // Company fields (hardcoded - from template defaults)
        // TODO: check if needed? (hardcoded in the html template)
        // 'company.name': '',
        // 'company.address': '',
        // 'company.fullAddress': '',
        // 'company.nip': '',
    };
}

/**
 * Formats client full name
 * @param {Object} client
 * @returns {string}
 */
function formatFullName(client) {
    if (!client) return '';
    return `${client.firstname || ''} ${client.surname || ''}`.trim();
}

/**
 * Formats full address (street, postal code, city)
 * @param {Object} client
 * @returns {string}
 */
function formatFullAddress(client) {
    if (!client) return '';
    const parts = [];
    if (client.address) parts.push(client.address);
    if (client.postalCode || client.city) {
        parts.push(`${client.postalCode || ''} ${client.city || ''}`.trim());
    }
    return parts.join(', ');
}

/**
 * Formats address line 1 (postal code, city)
 * @param {Object} client
 * @returns {string}
 */
function formatAddressLine1(client) {
    if (!client) return '';
    return `${client.postalCode || ''}, ${client.city || ''}`.trim().replace(/^,\s*/, '').replace(/,\s*$/, '');
}

/**
 * Formats vehicle name (manufacturer + model)
 * @param {Object} vehicle
 * @returns {string}
 */
function formatVehicleName(vehicle) {
    if (!vehicle) return '';
    return `${vehicle.manufacturer || ''} ${vehicle.model || ''}`.trim();
}

/**
 * Gets default empty document data structure
 * @returns {Object}
 */
export function getEmptyDocumentData() {
    return mapRepairToDocumentData(null, null, null);
}
