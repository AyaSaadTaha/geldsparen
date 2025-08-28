import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import {
    Box, Button, Typography, Paper, Grid, TextField, FormControlLabel, Checkbox,
    FormControl, InputLabel, Select, MenuItem, IconButton, Stack, Alert, Dialog, DialogTitle, DialogContent
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import "dayjs/locale/de";

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
        <div>
            <Typography variant="h5" fontWeight={800} align="center" mb={4} mt={4}>
                My Saving Goals
            </Typography>

            <Stack direction="row" justifyContent="center" mb={3}>
                <Button
                    variant="contained"
                    onClick={() => setShowForm(true)}
                    sx={{
                        background: "linear-gradient(45deg, #FF8C00 0%, #FF0000 100%)",
                        color: "white",
                        fontWeight: 600,
                        textTransform: "none",
                        px: 3,
                        py: 1,
                        borderRadius: 2,
                        transition: "0.3s",
                        "&:hover": {
                            background: "linear-gradient(45deg, #FF4500 0%, #CC0000 100%)",
                            transform: "scale(1.03)",
                            boxShadow: "0 4px 12px rgba(255, 69, 0, 0.4)",
                        },
                    }}
                >
                    Create New Goal
                </Button>
            </Stack>

            {message && (
                <Stack alignItems="center" mb={3}>
                    <Alert severity="info" sx={{ maxWidth: 480, width: "100%" }}>
                        {message}
                    </Alert>
                </Stack>
            )}

            {showForm && (
                <Dialog open={showForm} onClose={() => setShowForm(false)} maxWidth="sm" fullWidth>
                    <DialogTitle sx={{ fontWeight: 800 }}>Create New Saving Goal</DialogTitle>
                    <DialogContent dividers>
                        <Paper variant="outlined" sx={{ p: 3, borderRadius: 3 }}>
                            <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="de">
                                <form onSubmit={handleFormSubmit}>
                                    <Grid container spacing={2}>

                                        {/* Group Goal checkbox */}
                                        <Grid item xs={12}>
                                            <FormControlLabel
                                                control={
                                                    <Checkbox
                                                        checked={isGroup}
                                                        onChange={(e) => setIsGroup(e.target.checked)}
                                                    />
                                                }
                                                label="Group Goal"
                                            />
                                        </Grid>

                                        {/* Group Name (conditionally) */}
                                        {isGroup && (
                                            <Grid item xs={12}>
                                                <TextField
                                                    fullWidth
                                                    label="Group Name"
                                                    name="groupName"
                                                    value={groupData.groupName}
                                                    onChange={(e) =>
                                                        setGroupData((prev) => ({ ...prev, groupName: e.target.value }))
                                                    }
                                                    required
                                                />
                                            </Grid>
                                        )}

                                        {/* Goal Name */}
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                label="Goal Name"
                                                name="name"
                                                value={savingGoal.name}
                                                onChange={handleSavingGoalChange}
                                                required
                                            />
                                        </Grid>

                                        {/* Target Amount */}
                                        <Grid item xs={12} sm={6}>
                                            <TextField
                                                fullWidth
                                                type="number"
                                                inputProps={{ min: 0, step: 1 }}
                                                label="Target Amount (€)"
                                                name="targetAmount"
                                                value={savingGoal.targetAmount}
                                                onChange={handleSavingGoalChange}
                                                required
                                            />
                                        </Grid>

                                        {/* Deadline (DatePicker) */}
                                        <Grid item xs={12} sm={6}>
                                            <DatePicker
                                                label="Deadline"
                                                value={savingGoal.deadline ? dayjs(savingGoal.deadline) : null}
                                                onChange={(v) =>
                                                    handleSavingGoalChange({
                                                        target: { name: "deadline", value: v ? v.format("YYYY-MM-DD") : "" },
                                                    })
                                                }
                                                slotProps={{ textField: { fullWidth: true, required: true } }}
                                            />
                                        </Grid>

                                        {/* Type Select */}
                                        <Grid item xs={12} sm={6}>
                                            <FormControl fullWidth>
                                                <InputLabel id="type-label">Type</InputLabel>
                                                <Select
                                                    labelId="type-label"
                                                    label="Type"
                                                    name="type"
                                                    value={savingGoal.type}
                                                    onChange={handleSavingGoalChange}
                                                >
                                                    <MenuItem value="TRIP">Trip</MenuItem>
                                                    <MenuItem value="BIRTHDAY">Birthday</MenuItem>
                                                    <MenuItem value="WEDDING">Wedding</MenuItem>
                                                    <MenuItem value="OTHER">Other</MenuItem>
                                                </Select>
                                            </FormControl>
                                        </Grid>

                                        {/* Member Emails (conditionally for group) */}
                                        {isGroup && (
                                            <Grid item xs={12}>
                                                <Typography variant="subtitle2" sx={{ mb: 1 }}>
                                                    Member Emails
                                                </Typography>

                                                <Stack spacing={1}>
                                                    {groupData.memberEmails.map((email, index) => (
                                                        <Stack key={index} direction="row" spacing={1} alignItems="center">
                                                            <TextField
                                                                fullWidth
                                                                type="email"
                                                                placeholder="Enter member email"
                                                                value={email}
                                                                onChange={(e) => handleMemberEmailChange(index, e)}
                                                                required
                                                            />
                                                            {groupData.memberEmails.length > 1 && (
                                                                <IconButton
                                                                    aria-label="remove member"
                                                                    onClick={() => removeMemberEmail(index)}
                                                                    size="small"
                                                                >
                                                                    <RemoveIcon />
                                                                </IconButton>
                                                            )}
                                                        </Stack>
                                                    ))}

                                                    <Button
                                                        type="button"
                                                        startIcon={<AddIcon />}
                                                        onClick={addMemberEmail}
                                                        sx={{
                                                            alignSelf: "flex-start",
                                                            textTransform: "none",
                                                            fontWeight: 600,
                                                        }}
                                                    >
                                                        Add Another Member
                                                    </Button>
                                                </Stack>
                                            </Grid>
                                        )}

                                        {/* Actions */}
                                        <Grid item xs={12}>
                                            <Stack direction="row" spacing={2} justifyContent="flex-end">
                                                <Button
                                                    type="button"
                                                    variant="outlined"
                                                    onClick={() => setShowForm(false)}
                                                    sx={{ textTransform: "none", borderRadius: 2 }}
                                                >
                                                    Cancel
                                                </Button>

                                                <Button
                                                    variant="contained"
                                                    type="submit"
                                                    sx={{
                                                        background: "linear-gradient(45deg, #FF8C00 0%, #FF0000 100%)",
                                                        color: "white",
                                                        fontWeight: 600,
                                                        textTransform: "none",
                                                        px: 3,
                                                        py: 1,
                                                        borderRadius: 2,
                                                        transition: "0.3s",
                                                        "&:hover": {
                                                            background: "linear-gradient(45deg, #FF4500 0%, #CC0000 100%)",
                                                            transform: "scale(1.03)",
                                                            boxShadow: "0 4px 12px rgba(255, 69, 0, 0.4)",
                                                        },
                                                    }}
                                                >
                                                    Create Goal
                                                </Button>
                                            </Stack>
                                        </Grid>
                                    </Grid>
                                </form>
                            </LocalizationProvider>
                        </Paper>
                    </DialogContent>
                </Dialog>
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