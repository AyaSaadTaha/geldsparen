import React, { useState, useEffect } from 'react';
import api from '../services/api';

const CurrentAccountPage = () => {
    const [currentAccount, setCurrentAccount] = useState({
        salary: '',
        payday: 1,
        iban: ''
    });
    const [message, setMessage] = useState('');
    const [isEditing, setIsEditing] = useState(false);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        // التحقق من وجود token قبل جلب البيانات
        const token = localStorage.getItem('token');
        if (!token) {
            window.location.href = '/login';
            return;
        }
        fetchCurrentAccount();
    }, []);

    const fetchCurrentAccount = async () => {
        try {
            setIsLoading(true);
            const response = await api.get('/api/current-accounts');
            setCurrentAccount({
                salary: response.data.salary || '',
                payday: response.data.payday || 1,
                iban: response.data.iban || '',
                id: response.data.id
            });
        } catch (error) {
            if (error.response?.status === 404) {
                // لا يوجد حساب حالى، ابدأ بحالة التحرير
                setIsEditing(true);
            } else if (error.response?.status === 401) {
                // Unauthorized - تم التعامل معه في interceptor
                console.log('Redirecting to login...');
            } else {
                setMessage('Failed to fetch current account: ' + (error.response?.data?.message || error.message));
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            let response;

            if (currentAccount.id) {
                response = await api.put(`/api/current-accounts/${currentAccount.id}`, currentAccount);
            } else {
                response = await api.post('/api/current-accounts', currentAccount);
            }

            setCurrentAccount(response.data);
            setIsEditing(false);
            setMessage('Current account saved successfully');

            // إخفاء الرسالة بعد 3 ثواني
            setTimeout(() => setMessage(''), 3000);
        } catch (error) {
            if (error.response?.status === 401) {
                // Unauthorized - تم التعامل معه في interceptor
                console.log('Redirecting to login...');
            } else {
                setMessage('Failed to save current account: ' + (error.response?.data?.message || error.message));

                // إخفاء رسالة الخطأ بعد 5 ثواني
                setTimeout(() => setMessage(''), 5000);
            }
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setCurrentAccount(prev => ({
            ...prev,
            [name]: name === 'salary' ? (value === '' ? '' : parseFloat(value) || 0) :
                name === 'payday' ? (value === '' ? 1 : parseInt(value) || 1) : value
        }));
    };

    const handleCancel = () => {
        setIsEditing(false);
        fetchCurrentAccount(); // إعادة تحميل البيانات الأصلية
        setMessage(''); // مسح أي رسائل
    };

    if (isLoading) {
        return (
            <div className="current-account-page">
                <div className="loading">Loading your account information...</div>
            </div>
        );
    }

    return (
        <div className="current-account-page">
            <h2>Current Account</h2>

            {message && (
                <div className={`message ${message.includes('Failed') ? 'error' : 'success'}`}>
                    {message}
                </div>
            )}

            <form onSubmit={handleSubmit} className="current-account-form">
                <div className="form-group">
                    <label>Salary (€)</label>
                    <input
                        type="number"
                        name="salary"
                        value={currentAccount.salary}
                        onChange={handleChange}
                        required
                        disabled={!isEditing}
                        step="0.01"
                        min="0"
                        placeholder="Enter your monthly salary"
                    />
                </div>

                <div className="form-group">
                    <label>Payday (Day of Month)</label>
                    <input
                        type="number"
                        name="payday"
                        value={currentAccount.payday}
                        onChange={handleChange}
                        required
                        disabled={!isEditing}
                        min="1"
                        max="31"
                        placeholder="e.g., 1 for the 1st of each month"
                    />
                </div>

                {currentAccount.iban && (
                    <div className="form-group">
                        <label>IBAN</label>
                        <input
                            type="text"
                            value={currentAccount.iban}
                            disabled
                            className="iban-disabled"
                        />
                    </div>
                )}

                <div className="form-actions">
                    {isEditing ? (
                        <>
                            <button type="submit" className="btn btn-primary">
                                Save Account
                            </button>
                            <button
                                type="button"
                                onClick={handleCancel}
                                className="btn btn-secondary"
                            >
                                Cancel
                            </button>
                        </>
                    ) : (
                        <button
                            type="button"
                            onClick={() => setIsEditing(true)}
                            className="btn btn-primary"
                        >
                            Edit Account
                        </button>
                    )}
                </div>
            </form>
        </div>
    );
};

export default CurrentAccountPage;