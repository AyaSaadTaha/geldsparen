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

            <AppBar className="header-appbar" position="sticky" color="transparent" elevation={0} sx={{ borderBottom: '1px solid', borderColor: 'divider' }}>
                <Toolbar className="header-toolbar">
                    <Typography variant="h6" className="logo" sx={{ flexGrow: 1 }}>
                    </Typography>
                    <Typography variant="body1">Profile</Typography>
                </Toolbar>
                <div>
                    <Box className="profile-nav" sx={{ display:'flex', gap: 1, py: 1 }}>
                        <NavLink to="overview" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Overview</Button></NavLink>
                        <NavLink to="add-accounts" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Add Accounts</Button></NavLink>
                        <NavLink to="actions" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Actions</Button></NavLink>
                        <NavLink to="dashboards" className={({isActive})=> isActive? 'active':''}><Button className="profile-links" variant="text">Dashboards</Button></NavLink>
                    </Box>
                </div>
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