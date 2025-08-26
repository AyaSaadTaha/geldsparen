import React from 'react'
import {Container, Box, Typography, Button, Link as MLink} from '@mui/material'
import {Link} from "react-router-dom";


function HeroSection() {
    return (
        <Container maxWidth="lg">
            <Box className="hero">
                <Typography variant="h3" fontWeight={800} gutterBottom>
                    Title goes here
                </Typography>
                <Typography variant="h6" color="text.secondary">
                    Lorem ipsum dolor sit amet, consectetur adipiscing elit. Suspendisse tincidunt sagittis eros.
                </Typography>
                <Typography variant="h6" color="text.secondary">
                    Quisque quis euismod lorem. Etiam sodales ac felis id interdum.
                </Typography>
                <Box className="cta-stack" sx={{ mt: 2 }}>
                </Box>
                <Button variant="contained" size="large" component={Link} to="/register">Register</Button>
            </Box>
        </Container>
    )
}

export default HeroSection;