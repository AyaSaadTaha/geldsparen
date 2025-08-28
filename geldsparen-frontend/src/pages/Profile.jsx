import React, {useState} from 'react'
import {AppBar, Toolbar, Typography, Container, Box, Button, Link as MLink} from '@mui/material'
import {NavLink, Routes, Route, Navigate, Link} from 'react-router-dom'
import Overview from './Overview'
import AddAccounts from './AddAccounts'
import Actions from './Actions'
import Dashboards from './Dashboards'


function Profile() {
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
        setAccounts(prev => prev.filter(acc => acc.id !== id));
    }


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
                            <NavLink to="add-accounts" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Add Accounts</Button></NavLink>
                            <NavLink to="actions" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Actions</Button></NavLink>
                            <NavLink to="dashboards" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Dashboards</Button></NavLink>
                            <NavLink to="current-account" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">CurrentAccountPage</Button></NavLink>
                            <NavLink to="saving-goals" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">SavingGoalPage</Button></NavLink>
                            <NavLink to="spending-patterns" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">SpendingPatternPage</Button></NavLink>
                            <NavLink to="/monthly-payments/:goalId" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">MonthlyPaymentsPage</Button></NavLink>
                        </Box>
                    </div>
                    <Typography variant="body1">Profile</Typography>
                </Toolbar>
            </AppBar>

            {/* Routes*/}
            <Routes>
                <Route path="/" element={<Navigate to="overview" replace />} />
                <Route path="overview" element={
                    <Overview accounts={accounts} onDelete={handleDelete} />
                } />
                <Route path="add-accounts" element={
                    <AddAccounts onAdd={handleAdd}/>
                } />
                <Route path="actions" element={<Actions />} />
                <Route path="dashboards" element={<Dashboards accounts={accounts}/>} />
            </Routes>
        </>
    )
}

export default Profile