import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './SpendingPatternPage.css';

const SpendingPatternPage = () => {
    const [spendingPattern, setSpendingPattern] = useState({
        id: null,
        food: '',
        clothes: '',
        miscellaneous: '',
        savings: '',
        total_expenses: '',
        total_income: '',
        renter: ''
    });
    const [message, setMessage] = useState('');
    const [isEditing, setIsEditing] = useState(false);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState('');

    useEffect(() => {
        fetchSpendingPattern();
    }, []);

    const fetchSpendingPattern = async () => {
        try {
            setIsLoading(true);
            setError('');
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:8080/api/spending-patterns', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSpendingPattern({
                id: response.data.id || null,
                food: response.data.food !== 0 ? response.data.food.toString() : '',
                clothes: response.data.clothes !== 0 ? response.data.clothes.toString() : '',
                miscellaneous: response.data.miscellaneous !== 0 ? response.data.miscellaneous.toString() : '',
                savings: response.data.savings !== 0 ? response.data.savings.toString() : '',
                total_expenses: response.data.total_expenses !== 0 ? response.data.total_expenses.toString() : '',
                total_income: response.data.total_income !== 0 ? response.data.total_income.toString() : '',
                renter: response.data.renter !== 0 ? response.data.renter.toString() : ''
            });
            setIsEditing(false);
        } catch (error) {
            if (error.response?.status === 404) {
                setIsEditing(true);
            } else {
                setError('Ausgabemuster konnte nicht abgerufen werden: ' + (error.response?.data?.message || error.message));
            }
        } finally {
            setIsLoading(false);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            setError('');
            const token = localStorage.getItem('token');
            const dataToSend = {
                food: parseFloat(spendingPattern.food) || 0,
                clothes: parseFloat(spendingPattern.clothes) || 0,
                miscellaneous: parseFloat(spendingPattern.miscellaneous) || 0,
                renter: parseFloat(spendingPattern.renter) || 0
            };

            let response;
            if (spendingPattern.id) {
                response = await axios.put(`http://localhost:8080/api/spending-patterns/${spendingPattern.id}`, dataToSend, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            } else {
                response = await axios.post('http://localhost:8080/api/spending-patterns', dataToSend, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            }

            setSpendingPattern({
                ...response.data,
                id: response.data.id || null,
                food: response.data.food !== 0 ? response.data.food.toString() : '',
                clothes: response.data.clothes !== 0 ? response.data.clothes.toString() : '',
                miscellaneous: response.data.miscellaneous !== 0 ? response.data.miscellaneous.toString() : '',
                total_expenses: response.data.total_expenses !== 0 ? response.data.total_expenses.toString() : '',
                total_income: response.data.total_income !== 0 ? response.data.total_income.toString() : '',
                renter: response.data.renter !== 0 ? response.data.renter.toString() : '',
                savings: response.data.savings !== 0 ? response.data.savings.toString() : ''
            });

            setIsEditing(false);
            setMessage('Ausgabemuster erfolgreich gespeichert!');

            setTimeout(() => {
                setMessage('');
            }, 3000);
        } catch (error) {
            console.error('Error details:', error.response?.data);
            setError('Ausgabemuster konnte nicht gespeichert werden: ' + (error.response?.data?.message || error.message));
        }
    };

    const handleChange = (e) => {
        const { name, value } = e.target;
        const formattedValue = value.replace(/[^0-9.]/g, '');
        setSpendingPattern(prev => ({
            ...prev,
            [name]: formattedValue
        }));
    };

    const handleFocus = (e) => {
        e.target.select();
    };

    const handleCancel = () => {
        if (spendingPattern.id) {
            fetchSpendingPattern();
        } else {
            setSpendingPattern({
                id: null,
                food: '',
                clothes: '',
                miscellaneous: '',
                total_expenses: '',
                total_income: '',
                renter: '',
                savings: ''
            });
        }
        setIsEditing(false);
    };

    if (isLoading) {
        return (
            <div className="spending-pattern-page">
                <div className="loading">
                    <div className="spinner"></div>
                    <p>Lade dein Ausgabemuster...</p>
                </div>
            </div>
        );
    }

    return (
        <div className="spending-pattern-page">
            <div className="header">
                <h2>Ausgabemuster</h2>
                <p>Verwalte deine monatlichen Ausgabenkategorien</p>
            </div>

            {message && <div className="message success">{message}</div>}
            {error && <div className="message error">{error}</div>}

            <div className="card">
                <form onSubmit={handleSubmit} className="spending-pattern-form">
                    <div className="form-section">
                        <h3>{spendingPattern.id ? 'Ausgabemuster bearbeiten' : 'Ausgabemuster erstellen'}</h3>

                        <div className="input-grid">
                            <div className="form-group">
                                <label>Lebensmittel (€)</label>
                                <input
                                    type="text"
                                    name="food"
                                    value={spendingPattern.food}
                                    onChange={handleChange}
                                    onFocus={handleFocus}
                                    placeholder="Betrag eingeben"
                                    required
                                    disabled={!isEditing}
                                    className={!isEditing ? 'read-only' : ''}
                                />
                            </div>

                            <div className="form-group">
                                <label>Kleidung (€)</label>
                                <input
                                    type="text"
                                    name="clothes"
                                    value={spendingPattern.clothes}
                                    onChange={handleChange}
                                    onFocus={handleFocus}
                                    placeholder="Betrag eingeben"
                                    required
                                    disabled={!isEditing}
                                    className={!isEditing ? 'read-only' : ''}
                                />
                            </div>

                            <div className="form-group">
                                <label>Miete (€)</label>
                                <input
                                    type="text"
                                    name="renter"
                                    value={spendingPattern.renter}
                                    onChange={handleChange}
                                    onFocus={handleFocus}
                                    placeholder="Betrag eingeben"
                                    required
                                    disabled={!isEditing}
                                    className={!isEditing ? 'read-only' : ''}
                                />
                            </div>

                            <div className="form-group">
                                <label>Sonstiges (€)</label>
                                <input
                                    type="text"
                                    name="miscellaneous"
                                    value={spendingPattern.miscellaneous}
                                    onChange={handleChange}
                                    onFocus={handleFocus}
                                    placeholder="Betrag eingeben"
                                    required
                                    disabled={!isEditing}
                                    className={!isEditing ? 'read-only' : ''}
                                />
                            </div>


                        </div>
                    </div>

                    <div className="form-actions">
                        {!isEditing && (
                            <button
                                type="button"
                                onClick={() => setIsEditing(true)}
                                className="btn btn-primary"
                            >
                                {spendingPattern.id ? 'Ausgabemuster bearbeiten' : 'Ausgabemuster erstellen'}
                            </button>
                        )}

                        {isEditing && (
                            <>
                                <button type="submit" className="btn btn-primary">Änderungen speichern</button>
                                <button type="button" onClick={handleCancel} className="btn btn-secondary">
                                    Abbrechen
                                </button>
                            </>
                        )}
                    </div>
                </form>
            </div>

            {spendingPattern.id && !isEditing && (
                <div className="summary-card">
                    <h3>Monatliche Budgetübersicht</h3>
                    <div className="summary-grid">
                        <div className="summary-item">
                            <span className="label">Lebensmittel:</span>
                            <span className="value">€{parseFloat(spendingPattern.food || 0).toFixed(2)}</span>
                        </div>
                        <div className="summary-item">
                            <span className="label">Kleidung:</span>
                            <span className="value">€{parseFloat(spendingPattern.clothes || 0).toFixed(2)}</span>
                        </div>
                        <div className="summary-item">
                            <span className="label">Miete:</span>
                            <span className="value">€{parseFloat(spendingPattern.renter || 0).toFixed(2)}</span>
                        </div>
                        <div className="summary-item">
                            <span className="label">Sonstiges:</span>
                            <span className="value">€{parseFloat(spendingPattern.miscellaneous || 0).toFixed(2)}</span>
                        </div>

                        <div className="summary-item highlight">
                            <span className="label">Gesamtausgaben:</span>
                            <span className="value">€{parseFloat(spendingPattern.total_expenses || 0).toFixed(2)}</span>
                        </div>
                        <div className="summary-item highlight">
                            <span className="label">Gesamteinkommen:</span>
                            <span className="value">€{parseFloat(spendingPattern.total_income || 0).toFixed(2)}</span>
                        </div>
                        <div className="summary-item highlight">
                            <span className="label">Monatliche Ersparnisse:</span>
                            <span className="value">€{parseFloat(spendingPattern.savings || 0).toFixed(2)}</span>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
};

export default SpendingPatternPage;
