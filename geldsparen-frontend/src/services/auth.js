import api from './api';

export const authAPI = {
    login: (credentials) => api.post('/api/auth/login', {
        usernameOrEmail: credentials.username,
        password: credentials.password
    }),

    register: (userData) => api.post('/api/auth/register', userData),

    getCurrentUser: () => api.get('/api/auth/me'),

    createSavingGoals: (savingGoalData) => api.post('/api/saving-goals', savingGoalData),

    verifyToken: () => api.get('/api/auth/verify')
};