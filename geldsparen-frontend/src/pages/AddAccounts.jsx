import React, {useState} from 'react'
import { Container, Grid, Typography, Paper, TextField, Button } from "@mui/material";


function AddAccounts({onAdd}){

    const [form, setForm] = useState({
        goalName: "",
        targetDate: "",
        amount: "",
        spare: "",
        invite: "",
    });

    function handleChange(e) {
        setForm({ ...form, [e.target.name]: e.target.value });
    }

    function handleSubmit(e) {
        e.preventDefault();
        if (!form.goalName) return;
        onAdd({ ...form, id: Date.now().toString() });
        setForm({ goalName: "", targetDate: "", amount: "", spare: "", invite: "" }); // clear form
    }


    return (
        <Container maxWidth="lg" sx={{ py: 4 }}>
            <Typography variant="h5" fontWeight={800} gutterBottom>
                Add Saving Account
            </Typography>

            <Paper variant="outlined" sx={{ p: 3 }}>
                <form onSubmit={handleSubmit}>
                    <Grid container spacing={2}>
                        <Grid item xs={12}>
                            <TextField fullWidth label="Name your saving goal" name="goalName" value={form.goalName} onChange={handleChange} required />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth label="Target date" name="targetDate" value={form.targetDate} onChange={handleChange} />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth type="number" label="Target amount (€)" name="amount" value={form.amount} onChange={handleChange} />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth type="number" label="Monthly spare (€)" name="spare" value={form.spare} onChange={handleChange} />
                        </Grid>
                        <Grid item xs={6}>
                            <TextField fullWidth label="Invite member (email)" name="invite" value={form.invite} onChange={handleChange} />
                        </Grid>
                        <Grid item xs={12}>
                            <Button variant="contained" type="submit">Add</Button>
                        </Grid>
                    </Grid>
                </form>
            </Paper>
        </Container>
    )
}

export default AddAccounts;