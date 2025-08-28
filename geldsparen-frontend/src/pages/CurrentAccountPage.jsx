import React, { useState } from 'react';
import {Box, Paper, Grid, TextField, Typography, Button, Alert, Stack} from "@mui/material";

const CurrentAccountForm = () => {
    const [salary, setSalary] = useState('');
    const [payday, setPayday] = useState('');
    const [iban, setIban] = useState('');
    const [message, setMessage] = useState('');

    const handleSubmit = async (e) => {
        e.preventDefault();
        setMessage('Adding account...');
        const token =localStorage.getItem('token');
        alert(token)
        try {
            const response = await fetch('http://localhost:8080/api/current-accounts', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // IMPORTANT: Include the JWT token from the user's login response
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify({ salary, payday, iban }),
            });

            if (response.ok) {
                const data = await response.json();
                setMessage('Account added successfully!');
                console.log(data);
            } else if (response.status === 409) {
                setMessage('Error: Account already exists for this user.');
            } else {
                setMessage('Error adding account.');
            }
        } catch (error) {
            console.error('Network error:', error);
            setMessage('Network error, please try again.');
        }
    };

    return (
        <Box sx={{ py: 6 }}>
            <Typography
                variant="h5"
                fontWeight={800}
                align="center"
                mb={5}
                mt={5}
            >
                Add Current Account
            </Typography>

            <Paper
                variant="outlined"
                sx={{ p: 3, maxWidth: 440, mx: "auto", borderRadius: 3 }}
            >
                <form onSubmit={handleSubmit}>
                    <Stack spacing={2}>
                        <TextField
                            fullWidth
                            type="number"
                            inputProps={{ min: 0, step: 1 }}
                            label="Salary (€)"
                            value={salary}
                            onChange={(e) => setSalary(e.target.value)}
                            required
                        />

                        <TextField
                            fullWidth
                            type="number"
                            inputProps={{ min: 1, max: 31, step: 1 }}
                            label="Payday (1–31)"
                            value={payday}
                            onChange={(e) => setPayday(e.target.value)}
                            required
                        />

                        <TextField
                            fullWidth
                            label="IBAN"
                            value={iban}
                            onChange={(e) => setIban(e.target.value)}
                            required
                        />

                        <Button
                            variant="contained"
                            type="submit"
                            sx={{
                                background: "linear-gradient(45deg, #FF8C00 0%, #FF0000 100%)",
                                color: "white",
                                fontWeight: 600,
                                textTransform: "none",
                                px: 3,
                                py: 1.25,
                                borderRadius: 2,
                                transition: "0.3s",
                                "&:hover": {
                                    background: "linear-gradient(45deg, #FF4500 0%, #CC0000 100%)",
                                    transform: "scale(1.03)",
                                    boxShadow: "0 4px 12px rgba(255, 69, 0, 0.4)",
                                },
                            }}
                        >
                            Submit
                        </Button>

                        {message && <Alert severity="info">{message}</Alert>}
                    </Stack>
                </form>
            </Paper>
        </Box>
    );
};

export default CurrentAccountForm;