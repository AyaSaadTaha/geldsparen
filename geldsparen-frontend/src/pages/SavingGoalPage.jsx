import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import {
    Box, Button, Typography, Paper, Grid, TextField, FormControlLabel, Checkbox,
    FormControl, InputLabel, Select, MenuItem, IconButton, Stack, Alert, Dialog,
    DialogTitle, DialogContent, Card, CardContent, LinearProgress, Chip,
    InputAdornment, Fab, Divider, Tabs, Tab
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import RemoveIcon from "@mui/icons-material/Remove";
import SearchIcon from "@mui/icons-material/Search";
import GroupIcon from "@mui/icons-material/Group";
import PersonIcon from "@mui/icons-material/Person";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import EventIcon from "@mui/icons-material/Event";
import SavingsIcon from "@mui/icons-material/Savings";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import "dayjs/locale/de";

const SavingGoalPage = () => {
    const [savingGoals, setSavingGoals] = useState([]);
    const [filteredGoals, setFilteredGoals] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [isGroup, setIsGroup] = useState(false);
    const [message, setMessage] = useState('');
    const [searchQuery, setSearchQuery] = useState('');
    const [filterType, setFilterType] = useState('ALL');
    const [tabValue, setTabValue] = useState(0); // 0 for all, 1 for personal, 2 for group
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

    useEffect(() => {
        filterGoals();
    }, [savingGoals, searchQuery, filterType, tabValue]);

    const fetchSavingGoals = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:8080/api/saving-goals', {
                headers: { Authorization: `Bearer ${token}` }
            });

            if (Array.isArray(response.data)) {
                setSavingGoals(response.data);
            } else {
                console.error("API response for saving goals is not an array:", response.data);
                setSavingGoals([]);
            }
        } catch (error) {
            console.error(error);
            setMessage('Failed to fetch saving goals');
            setSavingGoals([]);
        }
    };

    const filterGoals = () => {
        let results = savingGoals;

        // Filter by search query
        if (searchQuery) {
            results = results.filter(goal =>
                goal.name.toLowerCase().includes(searchQuery.toLowerCase()) ||
                (goal.groupName && goal.groupName.toLowerCase().includes(searchQuery.toLowerCase()))
            );
        }

        // Filter by type
        if (filterType !== 'ALL') {
            results = results.filter(goal => goal.type === filterType);
        }

        // Filter by tab selection
        if (tabValue === 1) { // Personal goals only
            results = results.filter(goal => goal.isPersonal);
        } else if (tabValue === 2) { // Group goals only
            results = results.filter(goal => goal.isGroup);
        }

        setFilteredGoals(results);
    };

    const handleTabChange = (event, newValue) => {
        setTabValue(newValue);
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
                const groupRequestBody = {
                    savingGoal: savingGoal,
                    memberEmails: groupData.memberEmails.filter(email => email.trim() !== ''),
                    groupName: groupData.groupName
                };

                response = await axios.post('http://localhost:8080/api/groups/saving-goals/group', groupRequestBody, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            } else {
                response = await axios.post('http://localhost:8080/api/saving-goals', savingGoal, {
                    headers: { Authorization: `Bearer ${token}` }
                });
            }

            if (response.status === 200 || response.status === 201) {
                setMessage('Sparziel erfolgreich erstellt!');
                setShowForm(false);
                fetchSavingGoals();
                setSavingGoal({ name: '', targetAmount: '', deadline: '', type: 'OTHER' });
                setGroupData({ groupName: '', memberEmails: [''] });
                setIsGroup(false);
            }
        } catch (error) {
            if (error.response) {
                setMessage(`Fehler: ${error.response.data.message || error.response.statusText}`);
            } else {
                setMessage('Ein unerwarteter Fehler ist aufgetreten. Bitte versuchen Sie es erneut.');
            }
            console.error(error);
        }
    };

    const viewMonthlyPayments = (goalId) => {
        navigate(`/monthly-payments/${goalId}`);
    };

    const calculateProgress = (goal) => {
        if (!goal.targetAmount || goal.targetAmount === 0) return 0;
        const current = goal.currentAmount || 0;
        return Math.min(100, Math.round((current / goal.targetAmount) * 100));
    };

    const getDaysRemaining = (deadline) => {
        if (!deadline) return 'N/A';
        const today = dayjs();
        const targetDate = dayjs(deadline);
        const diffDays = targetDate.diff(today, 'day');
        return diffDays > 0 ? `${diffDays} Tage` : 'Abgelaufen';
    };

    const getTypeLabel = (type) => {
        const types = {
            'TRIP': 'Reise',
            'BIRTHDAY': 'Geburtstag',
            'WEDDING': 'Hochzeit',
            'OTHER': 'Anderes'
        };
        return types[type] || type;
    };

    return (
        <Box sx={{ maxWidth: 1200, margin: '0 auto', p: 3 }}>
            <Typography variant="h4" fontWeight={800} align="center" mb={2} color="primary">
                Meine Sparziele
            </Typography>

            <Typography variant="subtitle1" align="center" mb={4} color="text.secondary">
                Verwalten Sie Ihre persönlichen und gemeinsamen Sparziele
            </Typography>

            {/* Tabs for filtering */}
            <Paper elevation={2} sx={{ p: 1, mb: 3, borderRadius: 3 }}>
                <Tabs
                    value={tabValue}
                    onChange={handleTabChange}
                    indicatorColor="primary"
                    textColor="primary"
                    centered
                >
                    <Tab label="Alle Sparziele" />
                    <Tab label="Persönliche Ziele" />
                    <Tab label="Gruppenziele" />
                </Tabs>
            </Paper>

            {/* Search and Filter Section */}
            <Paper elevation={2} sx={{ p: 3, mb: 4, borderRadius: 3 }}>
                <Grid container spacing={2} alignItems="center">
                    <Grid item xs={12} md={6}>
                        <TextField
                            fullWidth
                            placeholder="Sparziele durchsuchen..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            InputProps={{
                                startAdornment: (
                                    <InputAdornment position="start">
                                        <SearchIcon color="action" />
                                    </InputAdornment>
                                ),
                            }}
                        />
                    </Grid>
                    <Grid item xs={12} md={4}>
                        <FormControl fullWidth>
                            <InputLabel>Zieltyp filtern</InputLabel>
                            <Select
                                value={filterType}
                                label="Zieltyp filtern"
                                onChange={(e) => setFilterType(e.target.value)}
                            >
                                <MenuItem value="ALL">Alle anzeigen</MenuItem>
                                <MenuItem value="TRIP">Reise</MenuItem>
                                <MenuItem value="BIRTHDAY">Geburtstag</MenuItem>
                                <MenuItem value="WEDDING">Hochzeit</MenuItem>
                                <MenuItem value="OTHER">Anderes</MenuItem>
                            </Select>
                        </FormControl>
                    </Grid>
                    <Grid item xs={12} md={2} display="flex" justifyContent="flex-end">
                        <Fab
                            color="primary"
                            aria-label="add saving goal"
                            onClick={() => setShowForm(true)}
                            sx={{
                                background: "linear-gradient(45deg, #FF8C00 0%, #FF0000 100%)",
                                "&:hover": {
                                    background: "linear-gradient(45deg, #FF4500 0%, #CC0000 100%)",
                                },
                            }}
                        >
                            <AddIcon />
                        </Fab>
                    </Grid>
                </Grid>
            </Paper>

            {message && (
                <Alert severity={message.includes('Fehler') ? 'error' : 'success'} sx={{ mb: 3 }}>
                    {message}
                </Alert>
            )}

            {/* Saving Goals List */}
            {filteredGoals.length === 0 ? (
                <Paper sx={{ p: 4, textAlign: 'center', borderRadius: 3 }}>
                    <SavingsIcon sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                    <Typography variant="h6" color="text.secondary" gutterBottom>
                        {savingGoals.length === 0 ? 'Noch keine Sparziele' : 'Keine passenden Sparziele gefunden'}
                    </Typography>
                    <Typography variant="body2" color="text.secondary" paragraph>
                        {savingGoals.length === 0
                            ? 'Erstellen Sie Ihr erstes Sparziel, um zu beginnen!'
                            : 'Versuchen Sie, Ihre Suchkriterien zu ändern'}
                    </Typography>
                    <Button
                        variant="contained"
                        onClick={() => setShowForm(true)}
                        sx={{
                            background: "linear-gradient(45deg, #FF8C00 0%, #FF0000 100%)",
                            "&:hover": {
                                background: "linear-gradient(45deg, #FF4500 0%, #CC0000 100%)",
                            },
                        }}
                    >
                        Neues Sparziel erstellen
                    </Button>
                </Paper>
            ) : (
                <Grid container spacing={3}>
                    {filteredGoals.map(goal => (
                        <Grid item xs={12} md={6} lg={4} key={goal.id}>
                            <Card
                                elevation={3}
                                sx={{
                                    height: '100%',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    borderRadius: 3,
                                    transition: 'transform 0.2s, box-shadow 0.2s',
                                    '&:hover': {
                                        transform: 'translateY(-4px)',
                                        boxShadow: 6
                                    }
                                }}
                            >
                                <CardContent sx={{ flexGrow: 1 }}>
                                    <Box display="flex" justifyContent="space-between" alignItems="flex-start" mb={2}>
                                        <Typography variant="h6" fontWeight={600} noWrap sx={{ maxWidth: '70%' }}>
                                            {goal.name}
                                        </Typography>
                                        <Chip
                                            icon={goal.isGroup ? <GroupIcon /> : <PersonIcon />}
                                            label={goal.isGroup ? 'Gruppe' : 'Einzel'}
                                            size="small"
                                            color={goal.isGroup ? 'primary' : 'default'}
                                        />
                                    </Box>

                                    {goal.groupName && (
                                        <Typography variant="body2" color="primary" gutterBottom>
                                            Gruppe: {goal.groupName}
                                        </Typography>
                                    )}

                                    {/* إضافة مؤشر إذا كان الهدف تمت دعوة المستخدم إليه */}
                                    {goal.isGroup && goal.isInvited && (
                                        <Chip
                                            label="Eingeladen"
                                            size="small"
                                            color="secondary"
                                            variant="outlined"
                                            sx={{ mb: 1 }}
                                        />
                                    )}

                                    <Chip
                                        label={getTypeLabel(goal.type)}
                                        size="small"
                                        variant="outlined"
                                        sx={{ mb: 2 }}
                                    />

                                    <Box display="flex" alignItems="center" mb={1}>
                                        <TrendingUpIcon fontSize="small" color="action" sx={{ mr: 1 }} />
                                        <Typography variant="body2">
                                            Fortschritt: {calculateProgress(goal)}%
                                        </Typography>
                                    </Box>

                                    <LinearProgress
                                        variant="determinate"
                                        value={calculateProgress(goal)}
                                        sx={{ height: 8, borderRadius: 4, mb: 2 }}
                                        color={calculateProgress(goal) >= 100 ? 'success' : 'primary'}
                                    />

                                    <Grid container spacing={1} mb={2}>
                                        <Grid item xs={6}>
                                            <Typography variant="body2" color="text.secondary">
                                                Gespart:
                                            </Typography>
                                            <Typography variant="body1" fontWeight="medium">
                                                €{(goal.currentAmount || 0).toLocaleString('de-DE')}
                                            </Typography>
                                        </Grid>
                                        <Grid item xs={6}>
                                            <Typography variant="body2" color="text.secondary">
                                                Zielbetrag:
                                            </Typography>
                                            <Typography variant="body1" fontWeight="medium">
                                                €{goal.targetAmount.toLocaleString('de-DE')}
                                            </Typography>
                                        </Grid>
                                    </Grid>

                                    <Box display="flex" alignItems="center" mb={2}>
                                        <EventIcon fontSize="small" color="action" sx={{ mr: 1 }} />
                                        <Typography variant="body2" sx={{ mr: 2 }}>
                                            {goal.deadline ? dayjs(goal.deadline).format('DD.MM.YYYY') : 'Kein Datum'}
                                        </Typography>
                                        <Chip
                                            label={getDaysRemaining(goal.deadline)}
                                            size="small"
                                            color={getDaysRemaining(goal.deadline).includes('Abgelaufen') ? 'error' : 'default'}
                                            variant="outlined"
                                        />
                                    </Box>

                                    <Button
                                        fullWidth
                                        variant="outlined"
                                        onClick={() => viewMonthlyPayments(goal.id)}
                                        sx={{ mt: 1 }}
                                    >
                                        Zahlungen anzeigen
                                    </Button>
                                </CardContent>
                            </Card>
                        </Grid>
                    ))}
                </Grid>
            )}

            {/* Create Goal Dialog */}
            <Dialog open={showForm} onClose={() => setShowForm(false)} maxWidth="sm" fullWidth>
                <DialogTitle sx={{
                    fontWeight: 800,
                    bgcolor: 'primary.main',
                    color: 'white',
                    display: 'flex',
                    alignItems: 'center'
                }}>
                    <AddIcon sx={{ mr: 1 }} />
                    Neues Sparziel erstellen
                </DialogTitle>
                <DialogContent dividers sx={{ p: 0 }}>
                    <Box sx={{ p: 3 }}>
                        <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="de">
                            <form onSubmit={handleFormSubmit}>
                                <Grid container spacing={2}>
                                    <Grid item xs={12}>
                                        <FormControlLabel
                                            control={
                                                <Checkbox
                                                    checked={isGroup}
                                                    onChange={(e) => setIsGroup(e.target.checked)}
                                                    color="primary"
                                                />
                                            }
                                            label="Gruppensparziel"
                                        />
                                    </Grid>

                                    {isGroup && (
                                        <Grid item xs={12}>
                                            <TextField
                                                fullWidth
                                                label="Gruppenname"
                                                name="groupName"
                                                value={groupData.groupName}
                                                onChange={(e) =>
                                                    setGroupData((prev) => ({ ...prev, groupName: e.target.value }))
                                                }
                                                required
                                            />
                                        </Grid>
                                    )}

                                    <Grid item xs={12}>
                                        <TextField
                                            fullWidth
                                            label="Sparziel Name"
                                            name="name"
                                            value={savingGoal.name}
                                            onChange={handleSavingGoalChange}
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <TextField
                                            fullWidth
                                            type="number"
                                            inputProps={{ min: 0, step: 1 }}
                                            label="Zielbetrag (€)"
                                            name="targetAmount"
                                            value={savingGoal.targetAmount}
                                            onChange={handleSavingGoalChange}
                                            required
                                        />
                                    </Grid>

                                    <Grid item xs={12} sm={6}>
                                        <DatePicker
                                            label="Zieldatum"
                                            value={savingGoal.deadline ? dayjs(savingGoal.deadline) : null}
                                            onChange={(v) =>
                                                handleSavingGoalChange({
                                                    target: { name: "deadline", value: v ? v.format("YYYY-MM-DD") : "" },
                                                })
                                            }
                                            slotProps={{ textField: { fullWidth: true, required: true } }}
                                        />
                                    </Grid>

                                    <Grid item xs={12}>
                                        <FormControl fullWidth>
                                            <InputLabel id="type-label">Zieltyp</InputLabel>
                                            <Select
                                                labelId="type-label"
                                                label="Zieltyp"
                                                name="type"
                                                value={savingGoal.type}
                                                onChange={handleSavingGoalChange}
                                            >
                                                <MenuItem value="TRIP">Reise</MenuItem>
                                                <MenuItem value="BIRTHDAY">Geburtstag</MenuItem>
                                                <MenuItem value="WEDDING">Hochzeit</MenuItem>
                                                <MenuItem value="OTHER">Anderes</MenuItem>
                                            </Select>
                                        </FormControl>
                                    </Grid>

                                    {isGroup && (
                                        <>
                                            <Grid item xs={12}>
                                                <Divider sx={{ my: 1 }} />
                                                <Typography variant="subtitle2" sx={{ mb: 1, fontWeight: 'bold' }}>
                                                    Mitglieder-E-Mails
                                                </Typography>

                                                <Stack spacing={2}>
                                                    {groupData.memberEmails.map((email, index) => (
                                                        <Stack key={index} direction="row" spacing={1} alignItems="center">
                                                            <TextField
                                                                fullWidth
                                                                type="email"
                                                                placeholder="E-Mail-Adresse eingeben"
                                                                value={email}
                                                                onChange={(e) => handleMemberEmailChange(index, e)}
                                                                required
                                                            />
                                                            {groupData.memberEmails.length > 1 && (
                                                                <IconButton
                                                                    aria-label="remove member"
                                                                    onClick={() => removeMemberEmail(index)}
                                                                    size="small"
                                                                    color="error"
                                                                >
                                                                    <RemoveIcon />
                                                                </IconButton>
                                                            )}
                                                        </Stack>
                                                    ))}
                                                </Stack>
                                            </Grid>

                                            <Grid item xs={12}>
                                                <Button
                                                    type="button"
                                                    startIcon={<AddIcon />}
                                                    onClick={addMemberEmail}
                                                    variant="outlined"
                                                    sx={{
                                                        textTransform: "none",
                                                        fontWeight: 600,
                                                    }}
                                                >
                                                    Weiteres Mitglied hinzufügen
                                                </Button>
                                            </Grid>
                                        </>
                                    )}
                                </Grid>
                            </form>
                        </LocalizationProvider>
                    </Box>

                    {/* BUTTONS PLACED AT THE BOTTOM OF THE DIALOG */}
                    <Box sx={{
                        p: 2,
                        display: 'flex',
                        justifyContent: 'flex-end',
                        gap: 2,
                        borderTop: '1px solid',
                        borderColor: 'divider'
                    }}>
                        <Button
                            variant="outlined"
                            onClick={() => setShowForm(false)}
                            sx={{ textTransform: "none", borderRadius: 2 }}
                        >
                            Abbrechen
                        </Button>

                        <Button
                            variant="contained"
                            onClick={handleFormSubmit}
                            sx={{
                                background: "linear-gradient(45deg, #FF8C00 0%, #FF0000 100%)",
                                color: "white",
                                fontWeight: 600,
                                textTransform: "none",
                                px: 3,
                                py: 1,
                                borderRadius: 2,
                                "&:hover": {
                                    background: "linear-gradient(45deg, #FF4500 0%, #CC0000 100%)",
                                },
                            }}
                        >
                            Ziel erstellen
                        </Button>
                    </Box>
                </DialogContent>
            </Dialog>
        </Box>
    );
};

export default SavingGoalPage;