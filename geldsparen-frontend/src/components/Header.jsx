import React, {useState} from 'react'
import {AppBar, Toolbar, Typography, Button, Box, Link as MLink, Modal} from '@mui/material'
import { Link } from 'react-router-dom'
import "./styles.css"
import {useAuth} from "../context/AuthContext.jsx";
import {AuthModal} from "./auth/AuthModal.jsx";


function Header() {
    const { user, logout, loading } = useAuth();
    const [authModalOpen, setAuthModalOpen] = useState(false);
    const [authMode, setAuthMode] = useState("login");


    const handleLogout = () => {
        logout();
    };

    const handleAuthClick = (mode) => {
        setAuthMode(mode);
        setAuthModalOpen(true);
    };
    const handleSwitchAuthMode = () => {
        setAuthMode(authMode === "login" ? "register" : "login");
    };

    if (loading) {
        return (
            <header className="bg-white shadow-sm border-b">
                <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
                    <div className="flex justify-between items-center h-16">
                        <div className="flex items-center">
                            <span className="text-xl font-bold text-gray-900">Spar Fuchs</span>
                        </div>
                        <div className="animate-pulse bg-gray-200 h-8 w-24 rounded"></div>
                    </div>
                </div>
            </header>
        );
    }


    return (
        <>
            <AppBar
                className="header-appbar"
                position="sticky"
                color="transparent"
                elevation={0}
                sx={{
                    borderBottom: '1px solid',
                    borderColor: 'divider',
                    zIndex: 100
                }}
            >
                <Toolbar className="header-toolbar">
                    <Typography variant="h6" className="logo" sx={{ flexGrow: 1 }}>
                    </Typography>
                    <Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 2, mr: 2 }} className="nav-right">
                        <MLink component={Link} to="/about" underline="hover" color="inherit">About</MLink>
                    </Box>
                    {/*<Button variant="outlined" component={Link} to="/login" className="login-btn">Log In</Button>*/}
                    {user ? (
                        <>
                            <span className="text-gray-700">Welcome, {user.username}</span>
                            <Button variant="outlined"  onClick={handleLogout} className="login-btn">Logout</Button>

                        </>
                    ) : (
                        <>
                            <Button variant="outlined" onClick={() => handleAuthClick("login")} className="login-btn btn-secondary"> Log In</Button>
                            <Button variant="outlined" onClick={() => handleAuthClick("register")} className="login-btn btn-primary"> Register</Button>
                        </>
                    )}

                </Toolbar>
            </AppBar>
            {/* Auth Modals */}
            <AuthModal
                mode={authMode}
                open={authModalOpen}
                onClose={() => setAuthModalOpen(false)}
                onSwitchMode={handleSwitchAuthMode}
             />
        </>

    )
}

export default Header