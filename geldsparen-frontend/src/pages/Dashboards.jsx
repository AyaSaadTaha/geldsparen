import React, {useEffect, useState} from 'react'
import SavingsDiagram from "../components/SavingsDiagram.jsx";
import SpendingsDiagram from "../components/SpendingsDiagram.jsx";
import axios from "axios";


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
            <p>Your dashboards overview.</p>
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

