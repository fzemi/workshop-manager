import { API_BASE_URL } from '@/libs/constants.js';
import { useAuthStore } from '@/stores/AuthStore.js';

/**
 * Service for Document Template API operations
 * Handles fetching document templates and their content
 */
const DocumentService = {
    /**
     * Get list of available document templates
     * @returns {Promise<Array<{name: string, displayName: string, description: string}>>}
     */
    async getAvailableTemplates() {
        const url = `${API_BASE_URL}/documents/templates`;
        const { user } = useAuthStore();
        const headers = user?.token ? { Authorization: `Bearer ${user.token}` } : {};
        
        const response = await fetch(url, { headers });
        if (!response.ok) {
            throw new Error('Nie udało się pobrać listy szablonów');
        }
        return response.json();
    },

    /**
     * Get template HTML content by name
     * @param {string} templateName - Template name (e.g., 'RepairOrder', 'VatDeclaration')
     * @returns {Promise<string>} HTML content
     */
    async getTemplateContent(templateName) {
        const url = `${API_BASE_URL}/documents/templates/${templateName}`;
        const { user } = useAuthStore();
        const headers = user?.token ? { Authorization: `Bearer ${user.token}` } : {};
        
        const response = await fetch(url, { headers });
        if (!response.ok) {
            throw new Error(`Nie udało się pobrać szablonu: ${templateName}`);
        }
        return response.text();
    },

    /**
     * Get URL for template image (header/footer)
     * @param {string} imageName - Image name (e.g., 'header.png', 'footer.png')
     * @returns {string} Full URL to the image
     */
    getTemplateImageUrl(imageName) {
        return `${API_BASE_URL}/documents/templates/images/${imageName}`;
    },
};

export default DocumentService;
