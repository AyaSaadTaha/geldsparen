import React, { createContext, useContext, useState, useEffect } from 'react';
import { authAPI } from '../services/auth';

const AuthContext = createContext();

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) {
        throw new Error('useAuth must be used within an AuthProvider');
    }
    return context;
};

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(JSON.parse(localStorage.getItem('user')) || null);
    const [token, setToken] = useState(localStorage.getItem('token') || null);
    const [loading, setLoading] = useState(true);


    useEffect(() => {
        const initAuth = async () => {
            const storedToken = localStorage.getItem('token');
            const storedUser = localStorage.getItem('user');

            if (storedToken && storedUser) {
                try {
                    await authAPI.verifyToken();
                    setToken(storedToken);
                    setUser(JSON.parse(storedUser));
                } catch (error) {
                    console.error('Token verification failed:', error);
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    setToken(null);
                    setUser(null);
                }
            }
            setLoading(false);
        };

        initAuth();
    }, []);

    const login = async (credentials) => {
        try {
            console.log("Login credentials:", credentials);

            // استخدمي await مباشرة بدون .then()
            const response = await authAPI.login(credentials);
            console.log("Login response:", response.data);

            const { accessToken } = response.data;

            localStorage.setItem('token', accessToken);
            setToken(accessToken);

            // الحصول على بيانات المستخدم
            const userResponse = await authAPI.getCurrentUser();
            console.log("User response:", userResponse.data);

            setUser(userResponse.data);
            localStorage.setItem('user', JSON.stringify(userResponse.data));

            return { success: true };
        } catch (error) {
            console.error("Login error:", error);
            const errorMessage = error.response?.data?.message ||
                error.response?.data ||
                'Login failed';
            return {
                success: false,
                error: errorMessage
            };
        }
    };

    const register = async (userData) => {
        try {
            const response = await authAPI.register(userData);
            return {
                success: true,
                data: response.data,
                message: response.data?.message || 'Registration successful'
            };
        } catch (error) {
            const errorMessage = error.response?.data?.message ||
                error.response?.data ||
                'Registration failed';
            return {
                success: false,
                error: errorMessage
            };
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        setToken(null);
        setUser(null);
    };

    const value = {
        user,
        token,
        loading,
        login,
        register,
        logout,
        isAuthenticated: !!token && !!user
    };

    return (
        <AuthContext.Provider value={value}>
            {children}
        </AuthContext.Provider>
    );
};
