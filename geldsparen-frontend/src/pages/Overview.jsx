import React from 'react'
import {Paper, Typography} from "@mui/material";
import AccountCard from "../components/AccountCard.jsx";
import SavingsAccount from "../components/SavingsAccount.jsx";


function Overview({ accounts, currentAccount, onDelete }){
    return (
        <div>
            {/* Girokonto */}
            <Typography variant="h5" fontWeight={700} gutterBottom marginTop={3}>
                Girokonto
            </Typography>

            {currentAccount ? (
                <AccountCard currentAccount={currentAccount[0]} />
            ) : (
                <Paper variant="outlined" sx={{ p: 3, mb: 2 }}>
                    <Typography color="text.secondary">
                        Noch kein Girokonto hinzugefügt.
                    </Typography>
                </Paper>
            )}

            {/* Savings Accounts*/}
            {accounts.length === 0 ? (
                <Paper variant="outlined" sx={{ p: 3 }}>
                    <Typography color="text.secondary">
                        Noch keine Sparziele hinzugefügt.
                    </Typography>
                </Paper>
            ) : (
                <SavingsAccount accounts={accounts} onDelete={onDelete} />
            )}
        </div>
    );
}

export default Overview;

