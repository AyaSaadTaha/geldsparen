import React, { useState } from "react";
import {
    Container,
    Grid,
    Typography,
    Paper,
    TextField,
    Button,
    FormControl,
    InputLabel,
    Select,
    MenuItem,
} from "@mui/material";

// date pickers
import { LocalizationProvider } from "@mui/x-date-pickers/LocalizationProvider";
import { DatePicker } from "@mui/x-date-pickers/DatePicker";
import { AdapterDayjs } from "@mui/x-date-pickers/AdapterDayjs";
import dayjs from "dayjs";
import "dayjs/locale/de";

function AddAccounts({ onAdd }) {
    const [form, setForm] = useState({
        goalName: "",
        amount: "",
        spare: "",
        invite: "",
        type: "saving", // new field type of account: 'saving' | 'debit (current)'
    });

    // save date as dayjs-obj for date-picker
    const [targetDate, setTargetDate] = useState(null);

    function handleChange(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

    function handleSubmit(e) {
        e.preventDefault();
        if (!form.goalName) return;

        // converting date into string 'YYYY-MM-DD'
        const dateStr = targetDate ? dayjs(targetDate).format("YYYY-MM-DD") : "";

        onAdd({
            ...form,
            targetDate: dateStr,
            amount: form.amount ? Number(form.amount) : 0,
            spare: form.spare ? Number(form.spare) : 0,
            id: Date.now().toString(),
        });

        // clear form
        setForm({ goalName: "", amount: "", spare: "", invite: "", type: "saving" });
        setTargetDate(null);
    }

    return (
        <div maxWidth="lg" sx={{ py: 6 }}>
            <Typography variant="h5"
                        fontWeight={800}
                        align="center"
                        mb={5}
                        mt={5}
            >
                Add Account
            </Typography>

            <Paper variant="outlined" sx={{ p: 3, maxWidth: 440,
                mx: "auto",
                borderRadius: 3}}>
                <LocalizationProvider dateAdapter={AdapterDayjs} adapterLocale="de">
                    <form onSubmit={handleSubmit} style={{ display: "flex", flexDirection: "column", gap: "16px" }}>
                        <Grid container spacing={2} direction="column">
                            <Grid item xs={12}>
                                <TextField
                                    fullWidth
                                    label="Name your saving goal"
                                    name="goalName"
                                    value={form.goalName}
                                    onChange={handleChange}
                                    required
                                />
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <DatePicker
                                    label="Target date"
                                    value={targetDate}
                                    onChange={(newValue) => setTargetDate(newValue)}
                                    slotProps={{
                                        textField: { fullWidth: true },
                                    }}
                                />
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    inputProps={{ min: 0, step: 1 }}
                                    label="Target amount (€)"
                                    name="amount"
                                    value={form.amount}
                                    onChange={handleChange}
                                />
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    type="number"
                                    inputProps={{ min: 0, step: 1 }}
                                    label="Monthly spare (€)"
                                    name="spare"
                                    value={form.spare}
                                    onChange={handleChange}
                                />
                            </Grid>

                            <Grid item xs={12} sm={6}>
                                <TextField
                                    fullWidth
                                    label="Invite member (email)"
                                    name="invite"
                                    value={form.invite}
                                    onChange={handleChange}
                                />
                            </Grid>

                            {/* select for account type*/}
                            <Grid item xs={12} sm={6}>
                                <FormControl fullWidth>
                                    <InputLabel id="type-label">Type</InputLabel>
                                    <Select
                                        labelId="type-label"
                                        label="Type"
                                        name="type"
                                        value={form.type}
                                        onChange={handleChange}
                                    >
                                        <MenuItem value="saving">Saving</MenuItem>
                                        <MenuItem value="debit">Debit (Current)</MenuItem>
                                    </Select>
                                </FormControl>
                            </Grid>

                            <Grid item xs={12}>
                                <Button variant="contained"
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
                                        }}>

                                    Add Account
                                </Button>
                            </Grid>
                        </Grid>
                    </form>
                </LocalizationProvider>
            </Paper>
        </div>
    );
}

export default AddAccounts;