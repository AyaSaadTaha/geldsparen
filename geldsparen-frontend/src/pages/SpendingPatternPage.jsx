import React, { useState, useEffect } from 'react';
import axios from 'axios';

const SpendingPatternPage = () => {
    const [spendingPattern, setSpendingPattern] = useState({
        food: 0,
        clothes: 0,
        miscellaneous: 0,
        savings: 0
    });
    const [message, setMessage] = useState('');
    const [isEditing, setIsEditing] = useState(false);

    useEffect(() => {
        fetchSpendingPattern();
    }, []);

    const fetchSpendingPattern = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('/api/spending-patterns', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSpendingPattern(response.data);
        } catch (error) {
            if (error.response?.status === 404) {
                setIsEditing(true);
            } else {
                setMessage('Failed to fetch spending pattern');
            }
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');
            let response;

            if (spendingPattern.id) {
                response = await axios.put(`/api/spending-patterns/${spendingPattern.id}`, spendingPattern, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            } else {
                response = await axios.post('/api/spending-patterns', spendingPattern, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            }

            setSpendingPattern(response.data);
            setIsEditing(false);
            setMessage('Spending pattern saved successfully');
        } catch (error) {
            console.error(error);
            setMessage('Failed to save spending pattern');
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        setSpendingPattern(prev => ({
            ...prev,
            [name]: parseFloat(value) || 0
        }));
    };

    const calculateTotalExpenses = () => {
        return (spendingPattern.food || 0) +
            (spendingPattern.clothes || 0) +
            (spendingPattern.miscellaneous || 0);
    };

    const calculateTotalIncome = () => {
        return calculateTotalExpenses() + (spendingPattern.savings || 0);
    };

    const calculateRemaining = () => {
        return calculateTotalIncome() - calculateTotalExpenses() - (spendingPattern.savings || 0);
    };

    return (
        <div className="spending-pattern-page">
            <h2>Spending Pattern</h2>

            {message && <div className="message">{message}</div>}

            <form onSubmit={handleSubmit} className="spending-pattern-form">
                <div className="form-row">
                    <div className="form-group">
                        <label>Food (€)</label>
                        <input
                            type="number"
                            name="food"
                            value={spendingPattern.food}
                            onChange={handleChange}
                            required
                            disabled={!isEditing}
                            step="0.01"
                        />
                    </div>

                    <div className="form-group">
                        <label>Clothes (€)</label>
                        <input
                            type="number"
                            name="clothes"
                            value={spendingPattern.clothes}
                            onChange={handleChange}
                            required
                            disabled={!isEditing}
                            step="0.01"
                        />
                    </div>
                </div>

                <div className="form-row">
                    <div className="form-group">
                        <label>Miscellaneous (€)</label>
                        <input
                            type="number"
                            name="miscellaneous"
                            value={spendingPattern.miscellaneous}
                            onChange={handleChange}
                            required
                            disabled={!isEditing}
                            step="0.01"
                        />
                    </div>

                    <div className="form-group">
                        <label>Savings (€)</label>
                        <input
                            type="number"
                            name="savings"
                            value={spendingPattern.savings}
                            onChange={handleChange}
                            required
                            disabled={!isEditing}
                            step="0.01"
                        />
                    </div>
                </div>

                <div className="form-actions">
                    {isEditing ? (
                        <>
                            <button type="submit" className="btn btn-primary">Save</button>
                            <button type="button" onClick={() => setIsEditing(false)} className="btn btn-secondary">
                                Cancel
                            </button>
                        </>
                    ) : (
                        <button type="button" onClick={() => setIsEditing(true)} className="btn btn-primary">
                            Edit
                        </button>
                    )}
                </div>
            </form>

            <div className="spending-summary">
                <h3>Summary</h3>
                <div className="summary-item">
                    <span>Total Expenses:</span>
                    <span>€{calculateTotalExpenses().toFixed(2)}</span>
                </div>
                <div className="summary-item">
                    <span>Savings:</span>
                    <span>€{(spendingPattern.savings || 0).toFixed(2)}</span>
                </div>
                <div className="summary-item">
                    <span>Total Income (calculated):</span>
                    <span>€{calculateTotalIncome().toFixed(2)}</span>
                </div>
                <div className="summary-item">
                    <span>Remaining:</span>
                    <span>€{calculateRemaining().toFixed(2)}</span>
                </div>
            </div>
        </div>
    );
};

export default SpendingPatternPage;