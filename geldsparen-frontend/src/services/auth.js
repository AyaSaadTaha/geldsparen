import api from './api';

export const authAPI = {
    login: (credentials) => api.post('/auth/login', {
        usernameOrEmail: credentials.username, // or credentials.email if needed
        password: credentials.password
    }),
    register: (userData) => api.post('/auth/register', userData),

    getCurrentUser: () => api.get('/auth/me'),

    verifyToken: (token) => {
        return api.get('/auth/verify', {
            headers: { Authorization: `Bearer ${token}` }
        });
    }
};