import React from 'react'
import { Container, Typography } from '@mui/material'


export default function AboutPage() {
    return (
        <Container maxWidth="md" sx={{ py: 6 }}>
            <Typography variant="h4" fontWeight={800} gutterBottom>
                About SparFuchs
            </Typography>
            <Typography>
                SparFuchs hilft dir, schnell und klar auf ein Sparziel hinzuarbeiten – ohne Finanzknoten im Kopf. 🎯
            </Typography>
        </Container>
    )
}