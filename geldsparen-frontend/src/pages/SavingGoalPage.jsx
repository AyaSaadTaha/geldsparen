import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';

const SavingGoalPage = () => {
    // Initialize savingGoals with an empty array to prevent the TypeError
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
            const response = await axios.get('http://localhost:8080/api/saving-goals', {
                headers: { Authorization: `Bearer ${token}` }
            });
            // Add a check to ensure the response data is an array before setting the state
            if (Array.isArray(response.data)) {
                setSavingGoals(response.data);
                alert(response.data);
            } else {
                // If the response is not an array, log the error and set to an empty array
                console.error("API response for saving goals is not an array:", response.data);
                setSavingGoals([]);
            }
        } catch (error) {
            console.error(error);
            setMessage('Failed to fetch saving goals');
            setSavingGoals([]); // Set to empty array on error to prevent breaking the UI
        }
    };

    const handleSavingGoalChange = (e) => {
        const { name, value } = e.target;
        setSavingGoal(prev => ({
            ...prev,
            [name]: value,
        }));
    };

    const handleMemberEmailChange = (index, e) => {
        const newMemberEmails = [...groupData.memberEmails];
        newMemberEmails[index] = e.target.value;
        setGroupData(prev => ({ ...prev, memberEmails: newMemberEmails }));
    };

    const addMemberEmail = () => {
        setGroupData(prev => ({ ...prev, memberEmails: [...prev.memberEmails, ''] }));
    };

    const removeMemberEmail = (index) => {
        const newMemberEmails = groupData.memberEmails.filter((_, i) => i !== index);
        setGroupData(prev => ({ ...prev, memberEmails: newMemberEmails }));
    };

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        setMessage('');

        try {
            const token = localStorage.getItem('token');
            let response;
            if (isGroup) {
                // Create a request body for group saving goal
                const groupRequestBody = {
                    savingGoal: savingGoal,
                    memberEmails: groupData.memberEmails.filter(email => email.trim() !== ''),
                    groupName: groupData.groupName
                };

                // Use the correct endpoint for creating a group saving goal
                response = await axios.post('http://localhost:8080/api/groups/saving-goals/group', groupRequestBody, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            } else {
                // Endpoint for creating a personal saving goal
                response = await axios.post('http://localhost:8080/api/saving-goals', savingGoal, {
                    headers: { Authorization: `Bearer ${token}` }
                });
               alert(response)
            }

            if (response.status === 200 || response.status === 201) {
                setMessage('Saving goal created successfully!');
                setShowForm(false);
                fetchSavingGoals(); // Refresh the list of goals
                // Reset form fields
                setSavingGoal({ name: '', targetAmount: '', deadline: '', type: 'OTHER' });
                setGroupData({ groupName: '', memberEmails: [''] });
                setIsGroup(false);
            }
        } catch (error) {
            if (error.response) {
                // This will give a more specific error message from the backend
                setMessage(`Error: ${error.response.data.message || error.response.statusText}`);
            } else {
                setMessage('An unexpected error occurred. Please try again.');
            }
            console.error(error);
        }
    };

    const viewMonthlyPayments = (goalId) => {
        navigate(`/monthly-payments/${goalId}`);
    };

    return (
        <div className="saving-goals-container">
            <h1>My Saving Goals</h1>
            <button className="btn btn-primary" onClick={() => setShowForm(true)}>
                Create New Goal
            </button>

            {message && <div className="message-box">{message}</div>}

            {showForm && (
                <div className="modal">
                    <div className="modal-content">
                        <h2>Create New Saving Goal</h2>
                        <form onSubmit={handleFormSubmit}>
                            <div className="form-group">
                                <label>
                                    <input
                                        type="checkbox"
                                        checked={isGroup}
                                        onChange={(e) => setIsGroup(e.target.checked)}
                                    />
                                    Group Goal
                                </label>
                            </div>
                            {isGroup && (
                                <div className="form-group">
                                    <label>Group Name</label>
                                    <input
                                        type="text"
                                        name="groupName"
                                        value={groupData.groupName}
                                        onChange={(e) => setGroupData(prev => ({ ...prev, groupName: e.target.value }))}
                                        required
                                    />
                                </div>
                            )}
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
                                <label>Type</label>
                                <select name="type" value={savingGoal.type} onChange={handleSavingGoalChange}>
                                    <option value="TRIP">Trip</option>
                                    <option value="BIRTHDAY">Birthday</option>
                                    <option value="WEDDING">Wedding</option>
                                    <option value="OTHER">Other</option>
                                </select>
                            </div>
                            {isGroup && (
                                <div className="form-group member-emails-container">
                                    <label>Member Emails</label>
                                    {groupData.memberEmails.map((email, index) => (
                                        <div key={index} className="member-email-input">
                                            <input
                                                type="email"
                                                value={email}
                                                onChange={(e) => handleMemberEmailChange(index, e)}
                                                placeholder="Enter member email"
                                                required
                                            />
                                            {groupData.memberEmails.length > 1 && (
                                                <button
                                                    type="button"
                                                    className="btn-remove"
                                                    onClick={() => removeMemberEmail(index)}
                                                >
                                                    -
                                                </button>
                                            )}
                                        </div>
                                    ))}
                                    <button
                                        type="button"
                                        className="btn-add"
                                        onClick={addMemberEmail}
                                    >
                                        + Add Another Member
                                    </button>
                                </div>
                            )}
                            <div className="form-actions">
                                <button type="submit" className="btn btn-success">
                                    Create Goal
                                </button>
                                <button
                                    type="button"
                                    className="btn btn-secondary"
                                    onClick={() => setShowForm(false)}
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