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
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const initAuth = async () => {
            if (token) {
                try {
                    // هنا يجب أن يكون لديك endpoint للتحقق من الـ token
                    const userData = await authAPI.getCurrentUser();
                    setUser(userData.data);
                } catch (error) {
                    console.error('Token verification failed:', error);
                    localStorage.removeItem('token');
                    setToken(null);
                }
            }
            setLoading(false);
        };

        initAuth();
    }, [token]);

    const login = async (credentials) => {
        try {
            const response = await authAPI.login(credentials);
            const { accessToken } = response.data;

            localStorage.setItem('token', accessToken);
            setToken(accessToken);

            // الحصول على بيانات المستخدم بعد AuthForm
            const userResponse = await authAPI.getCurrentUser();
            setUser(userResponse.data);

            return { success: true };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'AuthForm failed'
            };
        }
    };

    const register = async (userData) => {
        try {
            const response = await authAPI.register(userData);
            return { success: true, data: response.data };
        } catch (error) {
            return {
                success: false,
                error: error.response?.data?.message || 'Registration failed'
            };
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
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