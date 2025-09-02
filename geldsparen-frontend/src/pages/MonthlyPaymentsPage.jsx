import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams } from 'react-router-dom';
import {
    Box, Button, Typography, Paper, Grid, TextField, Card, CardContent,
    LinearProgress, Chip, Dialog, DialogTitle, DialogContent, DialogActions,
    Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    IconButton, Stack, Alert, Divider, Tabs, Tab, CircularProgress
} from "@mui/material";
import AddIcon from "@mui/icons-material/Add";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import PendingIcon from "@mui/icons-material/Pending";
import EventIcon from "@mui/icons-material/Event";
import PaidIcon from "@mui/icons-material/Paid";
import GroupIcon from "@mui/icons-material/Group";
import HistoryIcon from "@mui/icons-material/History";
import PersonIcon from "@mui/icons-material/Person";
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import "dayjs/locale/de";

const MonthlyPaymentsPage = () => {
    const { goalId } = useParams();
    const [monthlyPayments, setMonthlyPayments] = useState([]);
    const [showForm, setShowForm] = useState(false);
    const [message, setMessage] = useState('');
    const [savingGoal, setSavingGoal] = useState(null);
    const [groupMembers, setGroupMembers] = useState([]);
    const [groupMembersAusstehend, setGroupMembersAusstehend] = useState([]);
    const [memberContributions, setMemberContributions] = useState({});
    const [activeTab, setActiveTab] = useState(0);
    const [isLoading, setIsLoading] = useState(true);

    const [payment, setPayment] = useState({
        amount: '',
        dueDate: ''
    });

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            try {
                await Promise.all([
                    fetchSavingGoal(),
                    fetchMonthlyPayments(),
                    fetchGroupMembers()
                ]);
            } catch (error) {
                console.error('Error fetching data:', error);
            } finally {
                setIsLoading(false);
            }
        };
        fetchData();
    }, [goalId]);

    const fetchSavingGoal = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/saving-goals/${goalId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSavingGoal(response.data);
        } catch (error) {
            const errorMessage = handleApiError(error, 'Failed to fetch saving goal');
            setMessage(errorMessage);
            console.error(error);
        }
    };

    const fetchMonthlyPayments = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/monthly-payments/saving-goal/${goalId}`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setMonthlyPayments(Array.isArray(response.data) ? response.data : []);
        } catch (error) {
            // Spezifische Behandlung für 404 (keine Zahlungen gefunden)
            if (error.response && error.response.status === 404) {
                setMonthlyPayments([]);
                setMessage('Keine monatlichen Zahlungen gefunden');
            } else {
                const errorMessage = handleApiError(error, 'Failed to fetch monthly payments');
                setMessage(errorMessage);
                console.error('Error fetching payments:', error);
            }
        }
    };

    const fetchGroupMembers = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/groups/${goalId}/members`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            const data = response.data;
            const members = data.filter(m => m.invitationStatus === "ACCEPTED");
            const membersAusstehend = data.filter(m => m.invitationStatus === "PENDING");

            console.log("membersAusstehend", membersAusstehend)
            console.log("members", members)

            setGroupMembers(members);
            setGroupMembersAusstehend(membersAusstehend);

        } catch (error) {
            console.log('Group members not available or personal goal', error);
            setGroupMembers([]);
            setGroupMembersAusstehend([]);
        }
    };

    const fetchMemberContributions = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get(`http://localhost:8080/api/groups/saving-goal/${goalId}/contributions`, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setMemberContributions(response.data);
        } catch (error) {
            console.log('Member contributions not available',error);
            setMemberContributions({});
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
            await axios.post(`http://localhost:8080/api/monthly-payments/saving-goal/${goalId}`, payment, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setPayment({ amount: '', dueDate: '' });
            setShowForm(false);
            await fetchMonthlyPayments();
            await fetchSavingGoal();
            setMessage('Monatliche Zahlung erfolgreich hinzugefügt ✅');
        } catch (error) {
            console.error('Error adding payment:', error);
            setMessage('Monatliche Zahlung konnte nicht hinzugefügt werden');
        }
    };

    const updatePaymentStatus = async (paymentId, status) => {
        try {
            const token = localStorage.getItem('token');
            await axios.patch(`http://localhost:8080/api/monthly-payments/${paymentId}/status?status=${status}`, {}, {
                headers: { Authorization: `Bearer ${token}` }
            });

            await fetchMonthlyPayments();
            await fetchSavingGoal();
            setMessage('„Zahlungsstatus erfolgreich aktualisiert“ ✅');
        } catch (error) {
            console.error('„Fehler beim Aktualisieren des Zahlungsstatus“:');
            setMessage('Failed to update payment status');
        }
    };

    const calculateProgress = () => {
        if (!savingGoal || !savingGoal.targetAmount || savingGoal.targetAmount === 0) return 0;
        const current = savingGoal.currentAmount || 0;
        return Math.min(100, Math.round((current / savingGoal.targetAmount) * 100));
    };

    const getStatusIcon = (status) => {
        switch (status) {
            case 'PAID': return <CheckCircleIcon color="success" />;
            case 'PENDING': return <PendingIcon color="warning" />;
            case 'OVERDUE': return <EventIcon color="error" />;
            default: return <PendingIcon />;
        }
    };

    const getStatusText = (status) => {
        switch (status) {
            case 'PAID': return 'Bezahlt';
            case 'PENDING': return 'Ausstehend';
            case 'OVERDUE': return 'Überfällig';
            default: return status;
        }
    };

    const formatDate = (dateString) => {
        return dayjs(dateString).format('DD.MM.YYYY');
    };

    const formatCurrency = (amount) => {
        return `€${(amount || 0).toLocaleString('de-DE', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`;
    };

    const handleTabChange = (event, newValue) => {
        setActiveTab(newValue);
    };

    const handleApiError = (error, defaultMessage) => {
        if (error.response) {
            // Server responded with error status
            return error.response.data.message || defaultMessage;
        } else if (error.request) {
            // Request was made but no response received
            return 'Network error. Please check your connection.';
        } else {
            // Something else happened
            return error.message || defaultMessage;
        }
    };

    if (isLoading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
                <CircularProgress />
                <Typography variant="h6" sx={{ ml: 2 }}>
                    Loading...
                </Typography>
            </Box>
        );
    }

    if (!savingGoal) {
        return (
            <Box sx={{ p: 3 }}>
                <Alert severity="error">Sparziel nicht gefunden</Alert>
            </Box>
        );
    }

    const progress = calculateProgress();

    return (
        <Box sx={{ width: '100%', margin: '0 auto', p: 3 }}>
            {/* Header with progress */}
            <Paper elevation={3} sx={{ p: 4, mb: 4, borderRadius: 3, background: 'linear-gradient(45deg, #f5f5f5 30%, #e0e0e0 90%)' }}>
                <Box display="flex" justifyContent="space-between" alignItems="center" mb={2}>
                    <Typography variant="h4" fontWeight={800} color="primary">
                        {savingGoal.name}
                    </Typography>
                    {savingGoal.groupName && (
                        <Chip icon={<GroupIcon />} label={`Gruppe: ${savingGoal.groupName}`} color="primary" variant="outlined" />
                    )}
                </Box>

                <Typography variant="h6" gutterBottom>
                    Sparfortschritt: {progress}%
                </Typography>

                <LinearProgress
                    variant="determinate"
                    value={progress}
                    sx={{ height: 20, borderRadius: 5, mb: 2 }}
                    color={progress >= 100 ? 'success' : 'primary'}
                />

                <Grid container spacing={3}>
                    <Grid item xs={12} sm={4}>
                        <Typography variant="body2" color="text.secondary">
                            Gespart:
                        </Typography>
                        <Typography variant="h5" fontWeight={600}>
                            {formatCurrency(savingGoal.currentAmount)}
                        </Typography>
                    </Grid>
                    <Grid item xs={12} sm={4}>
                        <Typography variant="body2" color="text.secondary">
                            Zielbetrag jeder Monat:
                        </Typography>
                        <Typography variant="h5" fontWeight={600}>
                            {formatCurrency(savingGoal.monthlyAmount)}
                        </Typography>
                    </Grid>
                    <Grid item xs={12} sm={4}>
                        <Typography variant="body2" color="text.secondary">
                            Verbleibend:
                        </Typography>
                        <Typography variant="h5" fontWeight={600}>
                            {formatCurrency((savingGoal.total_monthly_amount) - (savingGoal.currentAmount || 0))}
                        </Typography>
                    </Grid>
                </Grid>

                {progress >= 100 && (
                    <Alert severity="success" sx={{ mt: 2 }}>
                        Glückwunsch! Sie haben Ihr Sparziel erreicht!
                    </Alert>
                )}
            </Paper>

            {message && (
                <Alert severity={message.includes('Failed') || message.includes('konnte nicht') ? 'error' : 'success'} sx={{ mb: 3 }}>
                    {message}
                </Alert>
            )}

            {/* Tabs for different sections */}
            <Paper sx={{ mb: 3 }}>
                <Tabs value={activeTab} onChange={handleTabChange} centered>
                    <Tab icon={<PaidIcon />} label="Zahlungen" />
                    <Tab icon={<GroupIcon />} label="Mitglieder Akzeptiert" />
                    <Tab icon={<GroupIcon />} label="Mitglieder Ausstehend" />
                </Tabs>
            </Paper>

            {/* Payments Tab */}
            {activeTab === 0 && (
                <Box>
                    <Box display="flex" justifyContent="space-between" alignItems="center" mb={3}>
                        <Typography variant="h5" fontWeight={700}>
                            Monatliche Zahlungen
                        </Typography>
                        <Button
                            variant="contained"
                            startIcon={<AddIcon />}
                            onClick={() => setShowForm(true)}
                            sx={{
                                background: "linear-gradient(45deg, #FF8C00 0%, #FF0000 100%)",
                                "&:hover": {
                                    background: "linear-gradient(45deg, #FF4500 0%, #CC0000 100%)",
                                },
                            }}
                        >
                            Zahlung hinzufügen
                        </Button>
                    </Box>

                    {/* Payments table */}
                    <TableContainer component={Paper} elevation={3} sx={{ borderRadius: 3 }}>
                        <Table>
                            <TableHead sx={{ bgcolor: 'primary.main' }}>
                                <TableRow>
                                    <TableCell sx={{ color: 'white', fontWeight: 600 }}>Betrag</TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 600 }}>Fälligkeitsdatum</TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 600 }}>Status</TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 600 }}>Bezahlt am</TableCell>
                                    <TableCell sx={{ color: 'white', fontWeight: 600 }}>Aktionen</TableCell>
                                </TableRow>
                            </TableHead>
                            <TableBody>
                                {monthlyPayments.length === 0 ? (
                                    <TableRow>
                                        <TableCell colSpan={5} align="center" sx={{ py: 3 }}>
                                            <Typography variant="body1" color="text.secondary">
                                                Noch keine Zahlungen vorhanden
                                            </Typography>
                                        </TableCell>
                                    </TableRow>
                                ) : (
                                    monthlyPayments.map((payment) => (
                                        <TableRow key={payment.id} hover>
                                            <TableCell>
                                                <Typography fontWeight={600}>
                                                    {formatCurrency(payment.amount)}
                                                </Typography>
                                            </TableCell>
                                            <TableCell>{formatDate(payment.dueDate)}</TableCell>
                                            <TableCell>
                                                <Chip
                                                    icon={getStatusIcon(payment.status)}
                                                    label={getStatusText(payment.status)}
                                                    variant="outlined"
                                                    color={
                                                        payment.status === 'PAID' ? 'success' :
                                                            payment.status === 'OVERDUE' ? 'error' : 'warning'
                                                    }
                                                />
                                            </TableCell>
                                            <TableCell>
                                                {payment.paidAt ? formatDate(payment.paidAt) : '-'}
                                            </TableCell>
                                            <TableCell>
                                                {payment.status !== 'PAID' && (
                                                    <Button
                                                        variant="outlined"
                                                        color="success"
                                                        startIcon={<PaidIcon />}
                                                        size="small"
                                                        onClick={() => updatePaymentStatus(payment.id, 'PAID')}
                                                    >
                                                        Als bezahlt markieren
                                                    </Button>
                                                )}
                                            </TableCell>
                                        </TableRow>
                                    ))
                                )}
                            </TableBody>
                        </Table>
                    </TableContainer>
                </Box>
            )}

            {/* Group Members Akzeptiert Tab */}
            {activeTab === 1 && groupMembers.length > 0 && (
                <Box>
                    <Typography variant="h5" fontWeight={700} mb={3}>
                        Gruppenmitglieder & ihre Beiträge
                    </Typography>
                    <Grid container spacing={2}>
                        {groupMembers.map((member, index) => (
                            <Grid item xs={12} sm={6} md={4} key={index}>
                                <Card variant="outlined" sx={{ height: '100%' }}>
                                    <CardContent>
                                        <Box display="flex" alignItems="center" mb={1}>
                                            <PersonIcon color="primary" sx={{ mr: 1 }} />
                                            <Typography variant="subtitle1" fontWeight={600}>
                                                {member.user?.username || member.email}
                                            </Typography>
                                        </Box>
                                        <Typography variant="body2" color="text.secondary">
                                            Beitrag: {formatCurrency(memberContributions[member.user?.username] || 0)}
                                        </Typography>
                                        <LinearProgress
                                            variant="determinate"
                                            value={savingGoal.targetAmount > 0 ?
                                                ((memberContributions[member.user?.username] || 0) / savingGoal.targetAmount) * 100 : 0}
                                            sx={{ mt: 1, height: 8, borderRadius: 4 }}
                                        />
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                </Box>
            )}

            {/* Group Members Ausstehend Tab */}
            {activeTab === 2 && groupMembersAusstehend.length > 0 && (
                <Box>
                    <Typography variant="h5" fontWeight={700} mb={3}>
                        Gruppen Mitglieder Ausstehend
                    </Typography>
                    <Grid container spacing={2}>
                        {groupMembersAusstehend.map((member, index) => (
                            <Grid item xs={12} sm={6} md={4} key={index}>
                                <Card variant="outlined" sx={{ height: '100%' }}>
                                    <CardContent>
                                        <Box display="flex" alignItems="center" mb={1}>
                                            <PersonIcon color="primary" sx={{ mr: 1 }} />
                                            <Typography variant="subtitle1" fontWeight={600}>
                                                {member.user?.username || member.email}
                                            </Typography>
                                        </Box>
                                        <Typography variant="body2" color="text.secondary">
                                            Beitrag: {formatCurrency(memberContributions[member.user?.username] || 0)}
                                        </Typography>
                                        <LinearProgress
                                            variant="determinate"
                                            value={savingGoal.targetAmount > 0 ?
                                                ((memberContributions[member.user?.username] || 0) / savingGoal.targetAmount) * 100 : 0}
                                            sx={{ mt: 1, height: 8, borderRadius: 4 }}
                                        />
                                    </CardContent>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                </Box>
            )}

            {/* Add Payment Dialog */}
            <Dialog open={showForm} onClose={() => setShowForm(false)} maxWidth="sm" fullWidth>
                <DialogTitle sx={{ fontWeight: 800, bgcolor: 'primary.main', color: 'white' }}>
                    <AddIcon sx={{ mr: 1 }} />
                    Monatliche Zahlung hinzufügen
                </DialogTitle>
                <DialogContent dividers sx={{ p: 3 }}>
                    <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="de">
                        <form onSubmit={handleSubmit}>
                            <Grid container spacing={2}>
                                <Grid item xs={12}>
                                    <TextField
                                        fullWidth
                                        type="number"
                                        inputProps={{ min: 0, step: 0.01 }}
                                        label="Betrag (€)"
                                        name="amount"
                                        value={payment.amount}
                                        onChange={handlePaymentChange}
                                        required
                                    />
                                </Grid>
                                <Grid item xs={12}>
                                    <DatePicker
                                        label="Fälligkeitsdatum"
                                        value={payment.dueDate ? dayjs(payment.dueDate) : null}
                                        onChange={(v) =>
                                            setPayment(prev => ({ ...prev, dueDate: v ? v.format('YYYY-MM-DD') : '' }))
                                        }
                                        slotProps={{ textField: { fullWidth: true, required: true } }}
                                    />
                                </Grid>
                            </Grid>
                        </form>
                    </LocalizationProvider>
                </DialogContent>
                <DialogActions sx={{ p: 2, borderTop: '1px solid', borderColor: 'divider' }}>
                    <Button onClick={() => setShowForm(false)} variant="outlined">
                        Abbrechen
                    </Button>
                    <Button
                        onClick={handleSubmit}
                        variant="contained"
                        sx={{
                            background: "linear-gradient(45deg, #FF8C00 0%, #FF0000 100%)",
                            "&:hover": {
                                background: "linear-gradient(45deg, #FF4500 0%, #CC0000 100%)",
                            },
                        }}
                    >
                        Zahlung hinzufügen
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
};

export default MonthlyPaymentsPage;