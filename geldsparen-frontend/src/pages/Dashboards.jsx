import React, { useEffect, useState } from 'react';
import { ResponsiveContainer, PieChart, Pie, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, Legend, Cell, RadialBarChart, RadialBar } from 'recharts';
import axios from 'axios';
import SpendingsDiagram from "../components/SpendingsDiagram.jsx";

const COLORS = ['#8b5cf7', '#22c55e', '#f43f5e', '#3b82f6', '#f59e0b', '#10b981', '#ef4444'];

const CustomTooltip = ({ active, payload }) => {
    if (active && payload && payload.length) {
        const data = payload[0].payload;
        return (
            <div className="bg-white p-4 shadow-lg rounded-lg border border-gray-200">
                <p className="font-bold text-gray-700">{data.name}</p>
                <p className="text-sm text-gray-600">Betrag: <span className="font-semibold">{data.value || data.current || data.progress} €</span></p>
                {(data.target) && <p className="text-sm text-gray-600">Ziel: <span className="font-semibold">{data.target} €</span></p>}
            </div>
        );
    }
    return null;
};

const CustomLegend = ({ payload }) => {
    return (
        <ul className="flex flex-wrap justify-center p-4">
            {payload.map((entry, index) => (
                <li key={`item-${index}`} className="flex items-center mx-2 my-1">
                    <div className="w-3 h-3 rounded-full mr-2" style={{ backgroundColor: entry.color || COLORS[index % COLORS.length] }}></div>
                    <span className="text-sm text-gray-600">{entry.value || entry.name}</span>
                </li>
            ))}
        </ul>
    );
};

// Mock-Daten zur Veranschaulichung, wenn die API nicht erreichbar ist
const MOCK_DATA = {
    spendingPattern: [
        { name: 'Lebensmittel', value: 300, color: '#f87171' },
        { name: 'Unterhaltung', value: 150, color: '#fde047' },
        { name: 'Miete', value: 1200, color: '#34d399' },
        { name: 'Transport', value: 200, color: '#60a5fa' },
        { name: 'Shopping', value: 450, color: '#a855f7' },
        { name: 'Versorgung', value: 250, color: '#fb923c' },
    ],
    savingGoals: [
        { name: 'Autofonds', currentAmount: 3000, targetAmount: 10000 },
        { name: 'Urlaub', currentAmount: 1500, targetAmount: 2000 },
        { name: 'Notfall', currentAmount: 5000, targetAmount: 5000 },
        { name: 'Neuer Laptop', currentAmount: 750, targetAmount: 1200 },
    ],
};

function Dashboards() {
    // Zustände für die Daten und den Ladezustand
    const [spendingPattern, setSpendingPattern] = useState(null);
    const [savingGoals, setSavingGoals] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchFinancialData = async () => {
            try {
                setLoading(true);
                const token = localStorage.getItem("token");

                const response = await axios.get("http://localhost:8080/api/spending-patterns", {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setSpendingPattern(response.data);

                // Versuch, Daten über die API abzurufen
                const goalsRes = await axios.get("http://localhost:8080/api/saving-goals", {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setSavingGoals(goalsRes.data);

            } catch (error) {
                console.error("Fehler beim Abrufen der Finanzdaten:", error);
                // Fallback zu Mock-Daten bei einem Fehler
                setSpendingPattern(MOCK_DATA.spendingPattern);
                setSavingGoals(MOCK_DATA.savingGoals);
            } finally {
                setLoading(false);
            }
        };

        fetchFinancialData();
    }, []);

    // Daten für Radial-Diagramm (Sparziele)
    const progressData = savingGoals.map((goal, idx) => ({
        name: goal.name,
        progress: goal.targetAmount
            ? Math.round((goal.currentAmount / goal.targetAmount) * 100)
            : 0,
        fill: COLORS[idx % COLORS.length],
    }));

    // Daten für Balkendiagramm (Ersparnisse vs. Ziel)
    const barData = savingGoals.map((goal, idx) => ({
        name: goal.name,
        current: goal.currentAmount,
        target: goal.targetAmount,
        fill: COLORS[idx % COLORS.length],
    }));

    return (
        <div className="min-h-screen bg-gray-100 p-8 font-sans antialiased text-gray-800">
            <script src="https://cdn.tailwindcss.com"></script>
            <div className="container mx-auto max-w-7xl">
                <h1 className="text-4xl md:text-5xl font-extrabold text-center text-gray-900 mb-2">
                    <span className="bg-clip-text text-transparent bg-gradient-to-r from-purple-500 to-pink-500">Finanzielles Dashboard</span>
                </h1>
                <p className="text-center text-gray-500 mb-10">Ein schneller Überblick über Ihre Finanzen</p>

                {loading ? (
                    <div className="flex justify-center items-center h-64">
                        <div className="w-12 h-12 border-4 border-purple-500 border-dashed rounded-full animate-spin"></div>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

                        {/* Ausgabenübersicht */}
                        {spendingPattern && (
                            <div className="lg:col-span-2 bg-white rounded-3xl shadow-xl p-6 transition-all duration-300 hover:shadow-2xl">
                                <SpendingsDiagram spendingPattern={spendingPattern} />
                            </div>
                        )}

                        {/* Fortschritt der Sparziele - Radial-Diagramm */}
                        {savingGoals.length > 0 && (
                            <div className="lg:col-span-1 bg-white rounded-3xl shadow-xl p-6 transition-all duration-300 hover:shadow-2xl">
                                <h2 className="text-xl md:text-2xl font-bold mb-4 text-gray-800">Fortschritt der Sparziele</h2>
                                <ResponsiveContainer width="100%" height={300}>
                                    <RadialBarChart
                                        innerRadius="10%"
                                        outerRadius="100%"
                                        data={progressData}
                                        startAngle={90}
                                        endAngle={-270}
                                    >
                                        <RadialBar
                                            minAngle={15}
                                            background
                                            clockWise
                                            dataKey="progress"
                                            cornerRadius={10}
                                            label={{ position: 'insideStart', fill: '#000', fontSize: 12, formatter: (value) => `${value}%` }}
                                        />
                                        <Tooltip content={<CustomTooltip />} />
                                        <Legend iconSize={10} layout="vertical" verticalAlign="middle" align="right" />
                                    </RadialBarChart>
                                </ResponsiveContainer>
                            </div>
                        )}

                        {/* Ersparnisse vs. Ziel - Balkendiagramm */}
                        {savingGoals.length > 0 && (
                            <div className="lg:col-span-3 bg-white rounded-3xl shadow-xl p-6 transition-all duration-300 hover:shadow-2xl">
                                <h2 className="text-xl md:text-2xl font-bold mb-4 text-gray-800">Ersparnisse vs. Ziel</h2>
                                <ResponsiveContainer width="100%" height={300}>
                                    <BarChart data={barData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
                                        <CartesianGrid strokeDasharray="3 3" />
                                        <XAxis dataKey="name" />
                                        <YAxis />
                                        <Tooltip formatter={(value, name) => [`${value} €`, name === 'current' ? 'Aktuelle Ersparnisse' : 'Ziel']} />
                                        <Legend />
                                        <Bar dataKey="current" fill="#a855f7" name="Aktuelle Ersparnisse" barSize={30} radius={[10, 10, 0, 0]} />
                                        <Bar dataKey="target" fill="#22c55e" name="Ziel" barSize={30} radius={[10, 10, 0, 0]} />
                                    </BarChart>
                                </ResponsiveContainer>
                            </div>
                        )}

                    </div>
                )}
            </div>
        </div>
    );
}

export default Dashboards;
