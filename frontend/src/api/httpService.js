import { useAuthStore } from '@/stores/AuthStore.js';
import { API_BASE_URL } from '@/libs/constants.js';

/**
 * HTTP Service for API requests
 * Handles authentication headers and response parsing
 */
export const httpService = {
    get: request('GET'),
    post: request('POST'),
    put: request('PUT'),
    patch: request('PATCH'),
    delete: request('DELETE'),
};

/**
 * Creates a request function for the specified HTTP method
 * @param {string} method - HTTP method
 * @returns {Function} Request function
 */
function request(method) {
    return async (endpoint, body = null) => {
        const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
        
        const requestOptions = {
            method,
            headers: getAuthHeader(),
        };

        if (body) {
            requestOptions.headers['Content-Type'] = 'application/json';
            requestOptions.body = JSON.stringify(body);
        }

        const response = await fetch(url, requestOptions);
        return handleResponse(response);
    };
}

/**
 * Gets the authorization header with JWT token if available
 * @returns {Object} Headers object
 */
function getAuthHeader() {
    const { user } = useAuthStore();
    const isLoggedIn = !!user?.token;

    if (isLoggedIn) {
        return { Authorization: `Bearer ${user.token}` };
    }
    return {};
}

/**
 * Handles API response
 * @param {Response} response - Fetch response
 * @returns {Promise<Object>} Parsed response with data and status
 */
async function handleResponse(response) {
    const text = await response.text();
    const data = text ? JSON.parse(text) : null;

    if (!response.ok) {
        const { user, logout } = useAuthStore();
        
        // Auto logout on auth errors
        if ([401, 403].includes(response.status) && user) {
            logout();
        }

        return Promise.reject({
            data,
            status: response.status,
            message: data?.message || 'Wystąpił błąd',
        });
    }

    return {
        data,
        status: response.status,
    };
}
