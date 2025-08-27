import React from 'react'
import {Container, Paper, Typography} from "@mui/material";
import Card from "../components/Card.jsx";
import SavingsAccount from "../components/SavingsAccount.jsx";


function Overview({ accounts, onDelete }){
    return (
        <div>

            {accounts.length === 0 ? (
                <Paper variant="outlined" sx={{ p: 3 }}>
                    <Typography color="text.secondary">
                        Noch keine Sparziele hinzugefügt.
                    </Typography>
                </Paper>
            ) : (
                <>
                    <Typography variant="h5" fontWeight={700} gutterBottom marginTop={3}>
                        Girokonto
            </Typography>
                    <Card />
                   <SavingsAccount/>
                </>
            )}
        </div>
    );
}

export default Overview;

