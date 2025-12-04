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
    
    /**
     * Upload a file using FormData
     * @param {string} endpoint - API endpoint
     * @param {FormData} formData - FormData containing file(s)
     * @param {Function} onProgress - Optional progress callback (0-100)
     * @returns {Promise<Object>} Response with data and status
     */
    uploadFile: async (endpoint, formData, onProgress = null) => {
        const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
        
        return new Promise((resolve, reject) => {
            const xhr = new XMLHttpRequest();
            
            xhr.open('POST', url);
            
            // Set auth header
            const { user } = useAuthStore();
            if (user?.token) {
                xhr.setRequestHeader('Authorization', `Bearer ${user.token}`);
            }
            
            // Progress tracking
            if (onProgress) {
                xhr.upload.onprogress = (event) => {
                    if (event.lengthComputable) {
                        const percent = Math.round((event.loaded / event.total) * 100);
                        onProgress(percent);
                    }
                };
            }
            
            xhr.onload = () => {
                const text = xhr.responseText;
                const data = text ? JSON.parse(text) : null;
                
                if (xhr.status >= 200 && xhr.status < 300) {
                    resolve({ data, status: xhr.status });
                } else {
                    const { user, logout } = useAuthStore();
                    if ([401, 403].includes(xhr.status) && user) {
                        logout();
                    }
                    reject({
                        data,
                        status: xhr.status,
                        message: data?.message || 'Wystąpił błąd podczas przesyłania pliku',
                    });
                }
            };
            
            xhr.onerror = () => {
                reject({
                    data: null,
                    status: 0,
                    message: 'Błąd połączenia z serwerem',
                });
            };
            
            xhr.send(formData);
        });
    },
    
    /**
     * Download a file as blob
     * @param {string} endpoint - API endpoint
     * @returns {Promise<Blob>} File blob
     */
    downloadBlob: async (endpoint) => {
        const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
        
        const { user, logout } = useAuthStore();
        const headers = user?.token ? { Authorization: `Bearer ${user.token}` } : {};
        
        const response = await fetch(url, { headers });
        
        if (!response.ok) {
            if ([401, 403].includes(response.status) && user) {
                logout();
            }
            
            // Try to parse error as JSON
            const text = await response.text();
            const data = text ? JSON.parse(text) : null;
            
            return Promise.reject({
                data,
                status: response.status,
                message: data?.message || 'Wystąpił błąd podczas pobierania pliku',
            });
        }
        
        return response.blob();
    },
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
