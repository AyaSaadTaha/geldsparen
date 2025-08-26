import React from 'react'
import {Container, Grid, IconButton, Paper, Typography} from "@mui/material";
import DeleteIcon from "@mui/icons-material/Delete";


function Overview({ accounts, onDelete }){
    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Typography variant="h5" fontWeight={800} gutterBottom>
                Overview
            </Typography>

            {accounts.length === 0 ? (
                <Paper variant="outlined" sx={{ p: 3 }}>
                    <Typography color="text.secondary">
                        Noch keine Sparziele hinzugefügt.
                    </Typography>
                </Paper>
            ) : (
                <Grid container spacing={2}>
                    {accounts.map(acc => (
                        <Grid item xs={12} md={6} key={acc.id}>
                            <Paper variant="outlined" sx={{ p: 2 }}>
                                <Typography variant="h6">{acc.goalName}</Typography>
                                <Typography variant="body2">Ziel: {acc.amount} €</Typography>
                                <Typography variant="body2">Monatlich: {acc.spare} €</Typography>
                                <Typography variant="body2">Datum: {acc.targetDate}</Typography>
                                <Typography variant="body2">Mitglied: {acc.invite}</Typography>
                                <IconButton onClick={() => onDelete(acc.id)}>
                                    <DeleteIcon />
                                </IconButton>
                            </Paper>
                        </Grid>
                    ))}
                </Grid>
            )}
        </Container>
    );
}

export default Overview;

