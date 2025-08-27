import api from './api';

export const authAPI = {
    login: (credentials) => api.post('/auth/login', credentials),

    register: (userData) => api.post('/auth/register', userData),

    getCurrentUser: () => api.get('/user/me'),

    verifyToken: (token) => {
        return api.get('/auth/verify', {
            headers: { Authorization: `Bearer ${token}` }
        });
    }
};