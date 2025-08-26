import React, {useState} from 'react'
import { AppBar, Toolbar, Typography, Container, Box, Button } from '@mui/material'
import { NavLink, Routes, Route, Navigate } from 'react-router-dom'
import Overview from './Overview'
import AddAccounts from './AddAccounts'
import Actions from './Actions'
import Dashboards from './Dashboards'


function Profile() {
    const [accounts, setAccounts] = useState([
        {
            id: "1",
            goalName: "Reise",
            amount: 1000,
            spare: 300,
            targetDate: "2025-12-31",
            invite: "lidiia@mail.com"
        }
    ]);


    function handleAdd(account) {
        setAccounts(prev => [...prev, account]);
    }

    function handleDelete(id) {
        setAccounts(prev => prev.filter(acc => acc.id !== id));
    }


    return (
        <>
            <AppBar position="sticky" color="transparent" elevation={0} sx={{ borderBottom: '1px solid', borderColor: 'divider' }}>
                <Toolbar>
                    <Typography variant="h6" sx={{ flexGrow: 1 }} className="logo">Company Logo</Typography>
                    <Typography variant="body1">Profile</Typography>
                </Toolbar>
                {/* Tabs navbar section*/}
                <Container maxWidth="lg" sx={{ pb: 1 }}>
                    <Box className="profile-nav" sx={{ display:'flex', gap: 1, py: 1 }}>
                        <NavLink to="overview" className={({isActive})=> isActive? 'active':''}><Button variant="text">Overview</Button></NavLink>
                        <NavLink to="add-accounts" className={({isActive})=> isActive? 'active':''}><Button variant="text">Add Accounts</Button></NavLink>
                        <NavLink to="actions" className={({isActive})=> isActive? 'active':''}><Button variant="text">Actions</Button></NavLink>
                        <NavLink to="dashboards" className={({isActive})=> isActive? 'active':''}><Button variant="text">Dashboards</Button></NavLink>
                    </Box>
                </Container>
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
                <Route path="dashboards" element={<Dashboards />} />
            </Routes>
        </>
    )
}

export default Profile