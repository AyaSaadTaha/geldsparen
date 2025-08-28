import api from './api';

export const authAPI = {
    login: (credentials) => api.post('/api/auth/login', {
        usernameOrEmail: credentials.username,
        password: credentials.password
    }),

    register: (userData) => api.post('/api/auth/register', userData),

    getCurrentUser: () => api.get('/api/auth/me'),

    verifyToken: () => api.get('/api/auth/verify'),
    /*

        createSavingGoal: (data) => api.post('/api/saving-goals/createSavingGoal', {
            usernameOrEmail: credentials.username,
            password: credentials.password
        }),
        getUserSavingGoals: (data) => api.get('/api/saving-goals/getUserSavingGoals',{
            headers: { Authorization: `Bearer ${token}`
        }),
        createSavingGoalUndGroup: (data) => api.post('/api/saving-goals/createSavingGoalUndGroup'),
    */
};