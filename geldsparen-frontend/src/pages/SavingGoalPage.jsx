import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const SavingGoalPage = () => {
    const [savingGoals, setSavingGoals] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [isGroup, setIsGroup] = useState(false);
    const [message, setMessage] = useState('');
    const navigate = useNavigate();

    const [savingGoal, setSavingGoal] = useState({
        name: '',
        targetAmount: '',
        deadline: '',
        type: 'OTHER'
    });

    const [groupData, setGroupData] = useState({
        groupName: '',
        memberEmails: ['']
    });

    useEffect(() => {
        fetchSavingGoals();
    }, []);

    const fetchSavingGoals = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('/api/saving-goals', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSavingGoals(response.data);
        } catch (error) {
            console.error(error);
            setMessage('Failed to fetch saving goals');
        }
    };

    const handleSavingGoalChange = (e) => {
        const { name, value } = e.target;
        setSavingGoal(prev => ({
            ...prev,
            [name]: name === 'targetAmount' ? parseFloat(value) || 0 : value
        }));
    };

    const handleGroupDataChange = (e) => {
        const { name, value } = e.target;
        setGroupData(prev => ({ ...prev, [name]: value }));
    };

    const handleMemberEmailChange = (index, value) => {
        const newEmails = [...groupData.memberEmails];
        newEmails[index] = value;
        setGroupData(prev => ({ ...prev, memberEmails: newEmails }));
    };

    const addEmailField = () => {
        setGroupData(prev => ({
            ...prev,
            memberEmails: [...prev.memberEmails, '']
        }));
    };

    const removeEmailField = (index) => {
        const newEmails = [...groupData.memberEmails];
        newEmails.splice(index, 1);
        setGroupData(prev => ({ ...prev, memberEmails: newEmails }));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            const token = localStorage.getItem('token');

            if (isGroup) {
                const request = {
                    savingGoal: savingGoal,
                    memberEmails: groupData.memberEmails.filter(email => email.trim() !== ''),
                    groupName: groupData.groupName
                };

                await axios.post('/api/saving-goals/group', request, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            } else {
                await axios.post('/api/saving-goals', savingGoal, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            }

            setShowForm(false);
            setSavingGoal({ name: '', targetAmount: '', deadline: '', type: 'OTHER' });
            setGroupData({ groupName: '', memberEmails: [''] });
            setIsGroup(false);
            fetchSavingGoals();
            setMessage('Saving goal created successfully');
        } catch (error) {
            console.error(error);
            setMessage('Failed to create saving goal');
        }
    };

    const viewMonthlyPayments = (goalId) => {
        navigate(`/monthly-payments/${goalId}`);
    };

    return (
        <div className="saving-goal-page">
            <div className="page-header">
                <h2>Saving Goals</h2>
                <button
                    className="btn btn-primary"
                    onClick={() => setShowForm(true)}
                >
                    Add New Saving Goal
                </button>
            </div>

            {message && <div className="message">{message}</div>}

            {showForm && (
                <div className="modal-overlay">
                    <div className="modal">
                        <h3>{isGroup ? 'Create Group Saving Goal' : 'Create Personal Saving Goal'}</h3>

                        <form onSubmit={handleSubmit}>
                            <div className="form-group">
                                <label>Goal Name</label>
                                <input
                                    type="text"
                                    name="name"
                                    value={savingGoal.name}
                                    onChange={handleSavingGoalChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Target Amount (€)</label>
                                <input
                                    type="number"
                                    name="targetAmount"
                                    value={savingGoal.targetAmount}
                                    onChange={handleSavingGoalChange}
                                    required
                                    step="0.01"
                                />
                            </div>

                            <div className="form-group">
                                <label>Deadline</label>
                                <input
                                    type="date"
                                    name="deadline"
                                    value={savingGoal.deadline}
                                    onChange={handleSavingGoalChange}
                                    required
                                />
                            </div>

                            <div className="form-group">
                                <label>Goal Type</label>
                                <select
                                    name="type"
                                    value={savingGoal.type}
                                    onChange={handleSavingGoalChange}
                                    required
                                >
                                    <option value="TRIP">Trip</option>
                                    <option value="BIRTHDAY">Birthday</option>
                                    <option value="WEDDING">Wedding</option>
                                    <option value="OTHER">Other</option>
                                </select>
                            </div>

                            <div className="form-group">
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={isGroup}
                                        onChange={(e) => setIsGroup(e.target.checked)}
                                    />
                                    Group Saving Goal
                                </label>
                            </div>

                            {isGroup && (
                                <>
                                    <div className="form-group">
                                        <label>Group Name</label>
                                        <input
                                            type="text"
                                            name="groupName"
                                            value={groupData.groupName}
                                            onChange={handleGroupDataChange}
                                            required
                                        />
                                    </div>

                                    <div className="form-group">
                                        <label>Member Emails</label>
                                        {groupData.memberEmails.map((email, index) => (
                                            <div key={index} className="email-input-group">
                                                <input
                                                    type="email"
                                                    value={email}
                                                    onChange={(e) => handleMemberEmailChange(index, e.target.value)}
                                                    placeholder="Enter member email"
                                                />
                                                {groupData.memberEmails.length > 1 && (
                                                    <button
                                                        type="button"
                                                        onClick={() => removeEmailField(index)}
                                                        className="btn btn-danger"
                                                    >
                                                        Remove
                                                    </button>
                                                )}
                                            </div>
                                        ))}
                                        <button
                                            type="button"
                                            onClick={addEmailField}
                                            className="btn btn-secondary"
                                        >
                                            Add Another Email
                                        </button>
                                    </div>
                                </>
                            )}

                            <div className="form-actions">
                                <button type="submit" className="btn btn-primary">Create</button>
                                <button
                                    type="button"
                                    onClick={() => {
                                        setShowForm(false);
                                        setIsGroup(false);
                                        setSavingGoal({ name: '', targetAmount: '', deadline: '', type: 'OTHER' });
                                        setGroupData({ groupName: '', memberEmails: [''] });
                                    }}
                                    className="btn btn-secondary"
                                >
                                    Cancel
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}

            <div className="saving-goals-list">
                {savingGoals.length === 0 ? (
                    <p>No saving goals yet. Create your first one!</p>
                ) : (
                    savingGoals.map(goal => (
                        <div key={goal.id} className="saving-goal-card">
                            <h3>{goal.name}</h3>
                            <p>Target: €{goal.targetAmount}</p>
                            <p>Current: €{goal.currentAmount || 0}</p>
                            <p>Progress: {goal.getProgressPercentage ? goal.getProgressPercentage() : 0}%</p>
                            <p>Deadline: {goal.deadline}</p>
                            <p>Type: {goal.type}</p>
                            <p>Status: {goal.status}</p>

                            <button
                                className="btn btn-primary"
                                onClick={() => viewMonthlyPayments(goal.id)}
                            >
                                View Monthly Payments
                            </button>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default SavingGoalPage;