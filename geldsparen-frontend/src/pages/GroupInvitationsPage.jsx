import React, { useState, useEffect } from 'react';
import axios from 'axios';
import {
    Box, Typography, Paper, List, ListItem, ListItemText,
    Button, Chip, Alert, Divider, CircularProgress
} from "@mui/material";
import GroupIcon from "@mui/icons-material/Group";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";
import CancelIcon from "@mui/icons-material/Cancel";

const GroupInvitationsPage = () => {
    const [invitations, setInvitations] = useState([]);
    const [message, setMessage] = useState('');
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        fetchInvitations();
    }, []);

    const fetchInvitations = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:8080/api/group-members/invitations', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setInvitations(response.data);
        } catch (error) {
            console.error('Error fetching invitations:', error);
            setMessage('Failed to fetch invitations');
        } finally {
            setIsLoading(false);
        }
    };

    const respondToInvitation = async (groupMemberId, response) => {
        try {
            const token = localStorage.getItem('token');
            await axios.patch(
                `http://localhost:8080/api/group-members/${groupMemberId}/respond`,
                { response },
                { headers: { Authorization: `Bearer ${token}` } }
            );

            setMessage('Response submitted successfully');
            fetchInvitations();
        } catch (error) {
            console.error('Error responding to invitation:', error);
            if (error.response?.data?.message) {
                setMessage(error.response.data.message);
            } else {
                setMessage('Failed to submit response');
            }
        }
    };

    if (isLoading) {
        return (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '50vh' }}>
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box sx={{ maxWidth: 800, margin: '0 auto', p: 3 }}>
            <Typography variant="h4" fontWeight={800} align="center" mb={2} color="primary">
                Group Invitations
            </Typography>

            {message && (
                <Alert severity={message.includes('Failed') ? 'error' : 'success'} sx={{ mb: 3 }}>
                    {message}
                </Alert>
            )}

            {invitations.length === 0 ? (
                <Paper sx={{ p: 4, textAlign: 'center' }}>
                    <GroupIcon sx={{ fontSize: 60, color: 'text.secondary', mb: 2 }} />
                    <Typography variant="h6" color="text.secondary">
                        No pending invitations
                    </Typography>
                </Paper>
            ) : (
                <Paper elevation={3}>
                    <List>
                        {invitations.map((invitation, index) => (
                            <React.Fragment key={invitation.id}>
                                <ListItem alignItems="flex-start">
                                    <ListItemText
                                        primary={
                                            <Box display="flex" alignItems="center" mb={1}>
                                                <Typography variant="h6" sx={{ mr: 1 }}>
                                                    {invitation.groupName}
                                                </Typography>
                                                <Chip
                                                    label={invitation.savingGoalName}
                                                    size="small"
                                                    color="primary"
                                                    variant="outlined"
                                                />
                                            </Box>
                                        }
                                        secondary={
                                            <>
                                                <Typography variant="body2" color="text.secondary">
                                                    Invited by: {invitation.invitedBy}
                                                </Typography>
                                                <Typography variant="body2" color="text.secondary">
                                                    Your monthly contribution: €{invitation.monthlyContribution}
                                                </Typography>
                                                <Box mt={2}>
                                                    <Button
                                                        variant="contained"
                                                        color="success"
                                                        startIcon={<CheckCircleIcon />}
                                                        onClick={() => respondToInvitation(invitation.id, 'ACCEPTED')}
                                                        sx={{ mr: 1 }}
                                                    >
                                                        Accept
                                                    </Button>
                                                    <Button
                                                        variant="outlined"
                                                        color="error"
                                                        startIcon={<CancelIcon />}
                                                        onClick={() => respondToInvitation(invitation.id, 'DECLINED')}
                                                    >
                                                        Decline
                                                    </Button>
                                                </Box>
                                            </>
                                        }
                                    />
                                </ListItem>
                                {index < invitations.length - 1 && <Divider />}
                            </React.Fragment>
                        ))}
                    </List>
                </Paper>
            )}
        </Box>
    );
};

export default GroupInvitationsPage;