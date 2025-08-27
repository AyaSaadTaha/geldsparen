import React from 'react'
import { AppBar, Toolbar, Typography, Button, Box, Link as MLink } from '@mui/material'
import { Link } from 'react-router-dom'
import "./styles.css"

function Header() {
    return (
        <AppBar className="header-appbar" position="sticky" color="transparent" elevation={0} sx={{ borderBottom: '1px solid', borderColor: 'divider' }}>
            <Toolbar className="header-toolbar">
                <Typography variant="h6" className="logo" sx={{ flexGrow: 1 }}>
                </Typography>
                <Box sx={{ display: { xs: 'none', md: 'flex' }, gap: 2, mr: 2 }} className="nav-right">
                    <MLink component={Link} to="/about" underline="hover" color="inherit">About</MLink>
                </Box>
                <Button variant="outlined" component={Link} to="/login" className="login-btn">Log In</Button>
            </Toolbar>
        </AppBar>
    )
}

export default Header