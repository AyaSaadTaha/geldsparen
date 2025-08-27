import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add token to requests
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Handle response errors
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response?.status === 401) {
            localStorage.removeItem('token');
            window.location.href = '/login';
        }
        return Promise.reject(error);
    }
);


export const authAPI = {
    login: (credentials) => api.post('/auth/login', {
        usernameOrEmail: credentials.username,
        password: credentials.password
    }),
    register: (userData) => api.post('/auth/register', userData),
};

export const userAPI = {
    getProfile: () => api.get('/users/profile'),
    updateProfile: (userData) => api.put('/users/profile', userData),
};

export const currentAccountAPI = {
    get: () => api.get('/current-account'),
    create: (accountData) => api.post('/current-account', accountData),
    update: (accountData) => api.put('/current-account', accountData),
};

export const savingGoalsAPI = {
    getAll: () => api.get('/saving-goals'),
    getById: (id) => api.get(`/saving-goals/${id}`),
    create: (goalData) => api.post('/saving-goals', goalData),
    update: (id, goalData) => api.put(`/saving-goals/${id}`, goalData),
    delete: (id) => api.delete(`/saving-goals/${id}`),
};

export const notificationsAPI = {
    getAll: () => api.get('/notifications'),
    markAsRead: (id) => api.put(`/notifications/${id}/read`),
    markAllAsRead: () => api.put('/notifications/read-all'),
};

export default api;