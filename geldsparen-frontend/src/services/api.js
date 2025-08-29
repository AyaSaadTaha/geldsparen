import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080';

// إنشاء instance مخصصة لـ axios
const api = axios.create({
    baseURL: API_BASE_URL,
    timeout: 1000,
    headers: {
        'Content-Type': 'application/json',
    },
});

// interceptor لإضافة token تلقائياً إلى كل طلب
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

// interceptor للتعامل مع responses
api.interceptors.response.use(
    (response) => {
        return response;
    },
    (error) => {
        if (error.response?.status === 401) {
            // Unauthorized - حذف token وتوجيه إلى Login
            localStorage.removeItem('token');
            localStorage.removeItem('user');
        } else if (error.response?.status === 403) {
            // Forbidden - عرض رسالة خطأ
            console.error('Access forbidden');
        } else if (error.code === 'ECONNABORTED') {
            console.error('Request timeout');
        } else if (!error.response) {
            console.error('Network error - server may be down');
        }
        return Promise.reject(error);
    }
);

export default api;