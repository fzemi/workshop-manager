/**
 * Utility functions for date formatting and parsing
 * Default format: DD.MM.YYYY
 */

/**
 * Formats a date to DD.MM.YYYY string
 * @param {Date|string|null} date - Date to format
 * @returns {string} Formatted date string or empty string if invalid
 */
export function formatDate(date) {
    if (!date) return '';
    
    const d = date instanceof Date ? date : new Date(date);
    
    if (isNaN(d.getTime())) return '';
    
    const day = String(d.getDate()).padStart(2, '0');
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const year = d.getFullYear();
    
    return `${day}.${month}.${year}`;
}

/**
 * Parses a DD.MM.YYYY string to Date object
 * @param {string} dateString - Date string in DD.MM.YYYY format
 * @returns {Date|null} Parsed Date object or null if invalid
 */
export function parseDate(dateString) {
    if (!dateString) return null;
    
    const parts = dateString.split('.');
    if (parts.length !== 3) return null;
    
    const day = parseInt(parts[0], 10);
    const month = parseInt(parts[1], 10) - 1;
    const year = parseInt(parts[2], 10);
    
    const date = new Date(year, month, day);
    
    if (isNaN(date.getTime())) return null;
    
    return date;
}

/**
 * Formats a date to ISO string (YYYY-MM-DD) for API requests
 * Uses local date components to avoid timezone shifts
 * @param {Date|string|null} date - Date to format
 * @returns {string} ISO date string or empty string if invalid
 */
export function toISODateString(date) {
    if (!date) return '';
    
    const d = date instanceof Date ? date : new Date(date);
    
    if (isNaN(d.getTime())) return '';
    
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    
    return `${year}-${month}-${day}`;
}

/**
 * Parses an ISO date string (YYYY-MM-DD) to Date object
 * @param {string} isoString - ISO date string
 * @returns {Date|null} Parsed Date object or null if invalid
 */
export function fromISODateString(isoString) {
    if (!isoString) return null;
    
    const date = new Date(isoString);
    
    if (isNaN(date.getTime())) return null;
    
    return date;
}
