import React from "react";
import {
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

const SavingsAccount = ({accounts, onDelete}) => {
    console.log("SavingsAccount ---- props", accounts);


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
                        typeof acc.currentAmount === "number" && typeof acc.currentAmount === "number"
                            ? Math.max(0, acc.targetAmount - acc.currentAmount)
                            : null;

                    const members = acc.members ?? acc.invite ?? "—";

                    return (
                        <Grid item xs={12} sm={6} md={4} key={acc.id} marginBottom={5}>
                            <Card variant="outlined" sx={{ borderRadius: 3 }} className="myCustomCard">
                                <CardHeader
                                    title={
                                        <Typography variant="h6" fontWeight={700} noWrap>
                                            {acc.name}
                                        </Typography>
                                    }
                                    action={
                                        <IconButton
                                            aria-label="Delete goal"
                                            onClick={() => onDelete(acc.id)}
                                            size="small"
                                        >
                                            <DeleteOutlineIcon />
                                        </IconButton>
                                    }
                                    sx={{ pb: 1.5 }}
                                />

                                <CardContent sx={{ pt: 0 }}>
                                    {/* Status + Type */}
                                    <Stack
                                        direction="row"
                                        justifyContent="space-between"
                                        alignItems="center"
                                        mb={2}
                                    >
                                        <Chip
                                            label={acc.status}
                                            color={acc.status === "ACTIVE" ? "success" : "default"}
                                            variant="outlined"
                                            sx={{ fontWeight: 600 }}
                                        />
                                        <Chip label={acc.type} variant="outlined" sx={{ fontWeight: 600 }} />
                                    </Stack>

                                    {/* Details */}
                                    <Stack spacing={1.2} mb={1.5}>
                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography color="text.secondary">Ziel</Typography>
                                            <Typography fontWeight={600}>{fmtMoney(acc.targetAmount)}</Typography>
                                        </Stack>

                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography color="text.secondary">Monatlich</Typography>
                                            <Typography fontWeight={600}>{fmtMoney(acc.monthlyAmount)}</Typography>
                                        </Stack>

                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography color="text.secondary">Datum</Typography>
                                            <Typography fontWeight={600}>
                                                {new Date(acc.deadline).toLocaleDateString("de-DE")}
                                            </Typography>
                                        </Stack>
                                    </Stack>
                                    <Stack direction="row" justifyContent="space-between">
                                        <Typography color="text.secondary">Mitglied</Typography>
                                        <Typography fontWeight={600}>{members}</Typography>
                                    </Stack>

                                    <Divider sx={{ my: 1.5 }} />



                                    {/*Progress bar*/}
                                    <Stack spacing={1}>
                                        <Stack direction="row" alignItems="center" justifyContent="space-between">
                                            <Typography variant="body2" color="text.secondary">
                                                Gespart
                                            </Typography>
                                            <Chip
                                                size="small"
                                                label={`${acc.progressPercentage ?? 0}%`}
                                                variant="outlined"
                                            />
                                        </Stack>

                                        <LinearProgress
                                            variant="determinate"
                                            value={Math.min(100, Math.max(0, acc.progressPercentage || 0))}
                                            sx={{ height: 10, borderRadius: 999 }}
                                        />

                                        <Stack direction="row" justifyContent="space-between">
                                            <Typography fontWeight={700}>{fmtMoney(acc.currentAmount)}</Typography>
                                            <Typography color="text.secondary">
                                                Rest: {remaining !== null ? fmtMoney(remaining) : "—"}
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

