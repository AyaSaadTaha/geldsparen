import React, { useState } from "react";
import {
    Container,
    Grid,
    Card,
    CardHeader,
    CardContent,
    IconButton,
    Typography,
    Stack,
    Box,
    LinearProgress,
    Chip,
    Divider,
} from "@mui/material";
import DeleteOutlineIcon from "@mui/icons-material/DeleteOutline";
import "./savingsaccount.css"

const SavingsAccount = () => {
    // Mock data
    const [accounts, setAccounts] = useState([
        {
            id: 1,
            goalName: "Reise nach Ägypten",
            amount: 5000,
            spare: 900,
            targetDate: "2025-09-31",
            members: 2,
            currentSaved: 2300,
            progress: 46,
        },
        {
            id: 2,
            goalName: "Geburtstag Party",
            amount: 3000,
            spare: 500,
            targetDate: "2025-12-15",
            invite: 2,
            currentSaved: 1200,
            progress: 40,
        },
    ]);

    const handleDelete = (id) => {
        setAccounts(accounts.filter((acc) => acc.id !== id));
    };

    const formatDate = (dateString) => {
        const d = new Date(dateString);
        return isNaN(d) ? "—" : d.toLocaleDateString("de-DE");
    };

    const fmtMoney = (n) =>
        typeof n === "number"
            ? n.toLocaleString("de-DE", { minimumFractionDigits: 0 })
            : "—";

    return (
        <div>
            <Stack direction="row" justifyContent="center" alignItems="center" mb={3} marginTop={3}>
                <Typography variant="h5" fontWeight={700}>
                    Sparziele
                </Typography>
                {/* add button on AddAccount Page
        <Button variant="contained" startIcon={<AddIcon />}>Neues Ziel</Button>
        */}
            </Stack>

            <Grid className="savings-container" container spacing={3}>
                {accounts.map((acc) => {
                    const remaining =
                        typeof acc.amount === "number" && typeof acc.currentSaved === "number"
                            ? Math.max(0, acc.amount - acc.currentSaved)
                            : null;

                    const members = acc.invite ?? acc.members ?? "—";

                    return (
                        <Grid item xs={12} sm={6} md={4} key={acc.id} marginBottom={5}>
                            <Card variant="outlined" sx={{ borderRadius: 3 }} className="myCustomCard">
                                <CardHeader
                                    title={
                                        <Typography variant="h6" fontWeight={700} noWrap>
                                            {acc.goalName}
                                        </Typography>
                                    }
                                    action={
                                        <IconButton
                                            aria-label="Delete goal"
                                            onClick={() => handleDelete(acc.id)}
                                            size="small"
                                        >
                                            <DeleteOutlineIcon />
                                        </IconButton>
                                    }
                                    sx={{ pb: 1.5 }}
                                />

                                <CardContent sx={{ pt: 0 }}>
                                    <Stack spacing={1.2} mb={1.5}>
                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography color="text.secondary">Ziel</Typography>
                                            <Typography fontWeight={600}>{fmtMoney(acc.amount)} €</Typography>
                                        </Stack>

                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography color="text.secondary">Monatlich</Typography>
                                            <Typography fontWeight={600}>{fmtMoney(acc.spare)} €</Typography>
                                        </Stack>

                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography color="text.secondary">Datum</Typography>
                                            <Typography fontWeight={600}>{formatDate(acc.targetDate)}</Typography>
                                        </Stack>

                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography color="text.secondary">Mitglied</Typography>
                                            <Typography fontWeight={600}>{members}</Typography>
                                        </Stack>
                                    </Stack>

                                    <Divider sx={{ my: 1.5 }} />

                                    {/* Прогресс */}
                                    <Stack spacing={1}>
                                        <Stack direction="row" alignItems="center" justifyContent="space-between">
                                            <Typography variant="body2" color="text.secondary">
                                                Gespart
                                            </Typography>
                                            <Chip
                                                size="small"
                                                label={`${acc.progress ?? 0}%`}
                                                variant="outlined"
                                            />
                                        </Stack>

                                        <LinearProgress
                                            variant="determinate"
                                            value={Math.min(100, Math.max(0, acc.progress || 0))}
                                            sx={{ height: 10, borderRadius: 999 }}
                                        />

                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography fontWeight={700}>
                                                {fmtMoney(acc.currentSaved)} €
                                            </Typography>
                                            <Typography color="text.secondary">
                                                Rest: {remaining !== null ? fmtMoney(remaining) : "—"} €
                                            </Typography>
                                        </Stack>
                                    </Stack>
                                </CardContent>
                            </Card>
                        </Grid>
                    );
                })}

                {accounts.length === 0 && (
                    <Grid item xs={12}>
                        <Box
                            sx={{
                                py: 8,
                                border: "1px dashed",
                                borderColor: "divider",
                                borderRadius: 3,
                                textAlign: "center",
                            }}
                        >
                            <Typography color="text.secondary">
                                Keine Sparziele. Füge ein neues Ziel hinzu.
                            </Typography>
                        </Box>
                    </Grid>
                )}
            </Grid>
        </div>
    );
};

export default SavingsAccount;

