import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';

const MonthlyPaymentsPage = () => {
    const { goalId } = useParams();
    const [monthlyPayments, setMonthlyPayments] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [message, setMessage] = useState('');
    const [savingGoal, setSavingGoal] = useState(null);

    const [payment, setPayment] = useState({
        amount: '',
        dueDate: ''
    });

    useEffect(() => {
        fetchSavingGoal();
        fetchMonthlyPayments();
    }, [goalId]);

    const fetchSavingGoal = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`/api/saving-goals/${goalId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSavingGoal(response.data);
        } catch (error) {
            console.error(error);
            setMessage('Failed to fetch saving goal');
        }
    };

    const fetchMonthlyPayments = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`/api/saving-goals/${goalId}/monthly-payments`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setMonthlyPayments(response.data);
        } catch (error) {
            console.error(error);
            setMessage('Failed to fetch monthly payments');
        }
    };

    const handlePaymentChange = (e) => {
        const { name, value } = e.target;
        setPayment(prev => ({
            ...prev,
            [name]: name === 'amount' ? parseFloat(value) || 0 : value
        }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');
            await axios.post(`/api/saving-goals/${goalId}/monthly-payments`, payment, {
                headers: { Authorization: `Bearer ${token}` }
            });

            setPayment({ amount: '', dueDate: '' });
            setShowForm(false);
            fetchMonthlyPayments();
            fetchSavingGoal(); // Refresh goal to update current amount
            setMessage('Monthly payment added successfully');
        } catch (error) {
            console.error(error);
            setMessage('Failed to add monthly payment');
        }
    };

    const formatDate = (dateString) => {
        return new Date(dateString).toLocaleDateString();
    };

    if (!savingGoal) {
        return <div>Loading...</div>;
    }

    return (
        <div className="monthly-payments-page">
            <h2>Monthly Payments for {savingGoal.name}</h2>
            <p>Target: €{savingGoal.targetAmount} | Current: €{savingGoal.currentAmount || 0}</p>

            {message && <div className="message">{message}</div>}

            <button
                className="btn btn-primary"
                onClick={() => setShowForm(true)}
            >
                Add Monthly Payment
            </button>

            {showForm && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>Add Monthly Payment</h3>

                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Amount (€)</label>
                                <input
                                    type="number"
                                    name="amount"
                                    value={payment.amount}
                                    onChange={handlePaymentChange}
                                    required
                                    step="0.01"
                                />
                            </div>

                            <div className="form-group">
                                <label>Due Date</label>
                                <input
                                    type="date"
                                    name="dueDate"
                                    value={payment.dueDate}
                                    onChange={handlePaymentChange}
                                    required
                                />
                            </div>

                            <div className="form-actions">
                                <button type="submit" className="btn btn-primary">Add</button>
                                <button
                                    type="button"
                                    onClick={() => setShowForm(false)}
                                    className="btn btn-secondary"
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            <div className="monthly-payments-list">
                <h3>Payment History</h3>

                {monthlyPayments.length === 0 ? (
                    <p>No monthly payments yet.</p>
                ) : (
                    <table className="payments-table">
                        <thead>
                        <tr>
                            <th>Amount</th>
                            <th>Due Date</th>
                            <th>Status</th>
                            <th>Paid At</th>
                        </tr>
                        </thead>
                        <tbody>
                        {monthlyPayments.map(payment => (
                            <tr key={payment.id}>
                                <td>€{payment.amount}</td>
                                <td>{formatDate(payment.dueDate)}</td>
                                <td>{payment.status}</td>
                                <td>{payment.paidAt ? formatDate(payment.paidAt) : '-'}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default MonthlyPaymentsPage;