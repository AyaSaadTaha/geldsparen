import React, {useEffect, useState} from 'react'
import {AppBar, Toolbar, Typography, Container, Box, Button, Link as MLink, MenuItem, Menu} from '@mui/material'
import {NavLink, Routes, Route, Navigate, Link} from 'react-router-dom'
import Overview from './Overview'
import AddAccounts from './AddAccounts'
import Actions from './Actions'
import Dashboards from './Dashboards'
import CurrentAccountPage from "./CurrentAccountPage.jsx";
import SavingGoalPage from "./SavingGoalPage.jsx";
import SpendingPatternPage from "./SpendingPatternPage.jsx";
import MonthlyPaymentsPage from "./MonthlyPaymentsPage.jsx";
import "../components/styles.css"
import ArrowDropDownIcon from "@mui/icons-material/ArrowDropDown";
import { useNavigate } from "react-router-dom";
import {useAuth} from "../context/AuthContext.jsx";
import axios from "axios";
import Footer from "../components/Footer.jsx";



function Profile() {
    // for dropdown menu navbar
    const [anchorEl, setAnchorEl] = React.useState(null);
    const open = Boolean(anchorEl);
    const handleOpen = (e) => setAnchorEl(e.currentTarget);
    const handleClose = () => setAnchorEl(null);
    const navigate = useNavigate();
    const go = (path) => { handleClose(); navigate(path); };
    const { user } = useAuth();
    const [currentAccount, setCurrentAccount] = useState(null);
    const [savingGoals, setSavingGoals] = useState([]);
    const [message, setMessage] = useState('');

    const [accounts, setAccounts] = useState([
        {
            id: "1",
            goalName: "Reise nach Ägypten",
            amount: 1000,
            spare: [300, 600, 200, 600, 200, 400],
            targetDate: "2025-12-31",
            invite: "lidiia@mail.com"
        },
        {
            id: 1,
            goalName: "Neues Auto",
            amount: 5000,
            spare: [100, 200, 200],
            targetDate: "2025-09-31",
            members: 2,
            currentSaved: 2300,
            progress: 46,
        },
        {
            id: 2,
            goalName: "Geburtstag Party",
            amount: 3000,
            spare: [500, 600, 800, 700],
            targetDate: "2025-12-15",
            invite: 2,
            currentSaved: 1200,
            progress: 40,
        },
    ]);

    function handleAdd(account) {
        setAccounts(prev => [...prev, account]);
    }

    function handleDelete(id) {
        setSavingGoals(prev => prev.filter(acc => acc.id !== id));
    }

    /*function handleAccountSaved(account) {
        setCurrentAccount(account);
    }*/


    const fetchSavingGoals = async () => {
        try {
            const token = localStorage.getItem('token');
            const response = await axios.get('http://localhost:8080/api/saving-goals', {
                headers: { Authorization: `Bearer ${token}` }
            });
            // Add a check to ensure the response data is an array before setting the state
            if (Array.isArray(response.data)) {
                setSavingGoals(response.data);
                alert(response.data);
            } else {
                // If the response is not an array, log the error and set to an empty array
                console.error("API response for saving goals is not an array:", response.data);
                setSavingGoals([]);
            }
        } catch (error) {
            console.error(error);
            setMessage('Failed to fetch saving goals');
            setSavingGoals([]); // Set to empty array on error to prevent breaking the UI
        }
    };

    useEffect(() => {
        fetchSavingGoals();
    }, []);

    useEffect(() => {
        const fetchAccount = async () => {
            const token = localStorage.getItem("token");
            try {
                const res = await fetch("http://localhost:8080/api/current-accounts", {
                    headers: {
                        "Authorization": `Bearer ${token}`
                    }
                });

                if (res.status === 404) {
                    // if no account, empty array
                    setCurrentAccount(null);
                    return;
                }

                if (!res.ok) {
                    console.error("Failed to load current account");
                    return;
                }

                const data = await res.json();
                console.log("👉 Data received from the backend:", data); // { salary, payday, iban }
                // array
                setCurrentAccount([{
                    id: "current",               // id for key
                    goalName: "Current Account", // if needed, title
                    salary: data.salary,
                    payday: data.payday,
                    iban: data.iban
                }]);
            } catch (e) {
                console.error("Network error:", e);
            }
        };

        fetchAccount();
    }, []);



    return (
        <>
            <AppBar className="header-appbar" position="static" color="transparent" elevation={0} sx={{ borderBottom: '1px solid', borderColor: 'divider', display:'flex', }}>
                <Toolbar className="header-toolbar">
                    <Typography variant="h6" className="logo" sx={{ flexGrow: 1 }}>
                    </Typography>
                    <div>
                        <Box className="profile-nav" sx={{
                            position: 'absolute',
                            left: '50%',
                            transform: 'translateX(-50%)',
                            display: 'flex',
                            gap: 1,
                            py: 1,
                        }}>
                            <NavLink to="overview" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Overview</Button></NavLink>
                            <Button
                                className="profile-links"
                                variant="text"
                                endIcon={<ArrowDropDownIcon />}
                                onClick={handleOpen}
                                aria-controls={open ? "add-account-menu" : undefined}
                                aria-haspopup="true"
                                aria-expanded={open ? "true" : undefined}
                            >
                                Add Account
                            </Button>
                            <Menu
                                id="add-account-menu"
                                anchorEl={anchorEl}
                                open={open}
                                onClose={handleClose}
                                anchorOrigin={{ vertical: "bottom", horizontal: "center" }}
                                transformOrigin={{ vertical: "top", horizontal: "center" }}
                                sx={{ zIndex: (t) => t.zIndex.appBar + 1 }}
                            >
                                <MenuItem onClick={() => go("/current-account")}>
                                   Current account
                                </MenuItem>
                                <MenuItem onClick={() => go("/saving-goals")}>
                                    Saving account
                                </MenuItem>
                            </Menu>

                            <NavLink to="dashboards" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Dashboards</Button></NavLink>
                            <NavLink to="spending-patterns" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Spending Page</Button></NavLink>
                            <NavLink to="monthly-payments/:goalId" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Monthly Payments</Button></NavLink>
                        </Box>
                    </div>
                    {/* user e-mail */}
                    <Typography variant="body1">
                        <strong><em>{user?.email}</em></strong>
                    </Typography>
                    <Typography variant="body1" marginLeft={2}>
                        Log Out
                    </Typography>
                </Toolbar>
            </AppBar>

            {/* Routes*/}
            <Routes>
                <Route path="/" element={<Navigate to="overview" replace />} />
                <Route path="overview" element={
                    <Overview accounts={savingGoals} onDelete={handleDelete} currentAccount={currentAccount} />
                } />
                <Route path="add-accounts" element={
                    <AddAccounts onAdd={handleAdd}/>
                } />
                <Route path="actions" element={<Actions />} />
                <Route path="dashboards" element={<Dashboards accounts={accounts}/>} />
                <Route path="current-account" element={<CurrentAccountPage currentAccount={currentAccount}/>} />
                <Route path="saving-goals" element={<SavingGoalPage/>} />
                <Route path="spending-patterns" element={<SpendingPatternPage/>} />
                <Route path="monthly-payments/:goalId" element={<MonthlyPaymentsPage/>} />
            </Routes>
        </>
    )
}

export default Profile