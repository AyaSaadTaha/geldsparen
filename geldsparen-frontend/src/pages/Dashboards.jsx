import React, {useEffect, useState} from 'react'
import SavingsDiagram from "../components/SavingsDiagram.jsx";
import SpendingsDiagram from "../components/SpendingsDiagram.jsx";
import axios from "axios";
import {Typography} from "@mui/material";


function Dashboards({accounts}){
    const [spendingPattern, setSpendingPattern] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchSpendingPattern = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem("token");
                const response = await axios.get("http://localhost:8080/api/spending-patterns", {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setSpendingPattern(response.data);
            } catch (error) {
                console.error("Fehler beim Abrufen des SpendingPatterns:", error);
            } finally {
                setLoading(false);
            }
        };

        fetchSpendingPattern();
    }, []);

    return (
        <div>
            <Typography
                component="h1"
                variant="h4"
                sx={{
                    m: 0,
                    mb: 2,
                    mt: 2,
                    fontFamily: '"Roboto","Helvetica","Arial",sans-serif',
                    fontWeight: 800,
                    fontSize: '2.125rem',
                    lineHeight: 1.235,
                    letterSpacing: '0.00735em',
                    color: '#8b5cf7',
                    textAlign: 'center',
                }}
            >
                Ihre Dashboard-Übersicht
            </Typography>
            <div style={{ marginTop: 16 }}>
                {loading && <p>Loading spending data...</p>}
                {!loading && spendingPattern && (
                    <SpendingsDiagram spendingPattern={spendingPattern} />
                )}
                <SavingsDiagram accounts={accounts} />
            </div>
        </div>
    );
}

export default Dashboards;

