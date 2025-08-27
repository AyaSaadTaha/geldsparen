import React, { useMemo } from "react";
import { Card, CardContent, CardHeader, Typography } from "@mui/material";
import {
    ResponsiveContainer, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip
} from "recharts";

const SavingsDiagram = ({accounts = []}) => {
    // savings account
    const savings = useMemo(
        () => (accounts || []).filter(a => (a.type || "saving") === "saving"),
        [accounts]
    );

    // one column per account
    const acc = accounts[0]; // first saving account
    const data = (acc?.spare || []).map((value, idx) => ({
        name: `M${idx + 1}`,
        value,
    }));

    // total sum for month (spare)
    const monthlyTotal = data.reduce((s, d) => s + d.value, 0);
    return (
        <div>
            <Card variant="outlined" sx={{ borderRadius: 3 }}>
                <CardHeader
                    title={<Typography variant="h6" align="center" fontWeight={800}>Savings</Typography>}
                    subheader={
                        <Typography align="center">
                            Monthly total: {new Intl.NumberFormat("de-DE").format(monthlyTotal)} €
                        </Typography>
                    }
                    sx={{ pb: 0 }}
                />
                <CardContent>
                    <Typography variant="h6" align="center" gutterBottom>
                        {acc ? acc.goalName : "Savings"}
                    </Typography>
                    <div style={{ width: "100%", height: 320 }}>
                        <ResponsiveContainer>
                            <BarChart data={data} barSize={38}>
                                {/* gradient for columns */}
                                <defs>
                                    <linearGradient id="gradOrRed" x1="0" y1="0" x2="1" y2="1">
                                        <stop offset="0%" stopColor="#FF8C00" />
                                        <stop offset="100%" stopColor="#FF1E1E" />
                                    </linearGradient>
                                </defs>
                                <CartesianGrid strokeDasharray="3 3" stroke="#E0E0E0" />
                                <XAxis dataKey="name" tick={{ fontSize: 12 }} axisLine={false} tickLine={false}  />
                                <YAxis tick={{ fontSize: 12 }}
                                       axisLine={false}
                                       tickLine={false}
                                     />
                                <Tooltip formatter={(v) => `${new Intl.NumberFormat("de-DE").format(v)} €`} />
                                <Bar dataKey="value"
                                     fill="url(#gradOrRed)"
                                     radius={[10, 10, 0, 0]}
                                     animationDuration={600} />
                            </BarChart>
                        </ResponsiveContainer>
                    </div>
                    <Typography variant="body2" color="text.secondary" sx={{ mt: 1 }}>
                        Each column = monthly <code>spare</code> amount for the goal.
                    </Typography>
                </CardContent>
            </Card>
        </div>
    );
};

export default SavingsDiagram;