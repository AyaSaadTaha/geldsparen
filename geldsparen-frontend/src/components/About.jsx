import React from 'react'
import { Box, Typography, Paper, Button, Stack } from "@mui/material";
import SavingsIcon from "@mui/icons-material/Savings";
import Footer from "./Footer.jsx";
import Header from "./Header.jsx"; // или любая другая иконка


export default function AboutPage() {
    return (

        <Box
            display="flex"
            flexDirection="column"
            minHeight="100vh"
        >
            <Header hideAbout/>

            <Box flexGrow={1} display="flex">
                <Paper
                    elevation={6}
                    sx={{
                        p: 5,
                        textAlign: "center",
                        background: "linear-gradient(135deg, #f3e8ff, #ecfdf5)",
                        width: "100%"
                    }}
                >
                    <Stack spacing={2} alignItems="center">
                        <SavingsIcon sx={{ fontSize: 48, color: "primary.main" }} />
                        <Typography variant="h4" fontWeight="bold" color="text.primary">
                            Spar Fuchs App 🦊
                        </Typography>
                        <Typography variant="body1" color="text.secondary" maxWidth="600px">
                            Eine smarte App, die dir hilft, persönliche und gemeinsame Sparziele
                            einfach zu verwalten. Erstelle Ziele, verfolge deinen Fortschritt und
                            spare zusammen mit Freunden – alles übersichtlich und motivierend an
                            einem Ort.
                        </Typography>

                        <Box
                            sx={{
                                mt: 2,
                                p: 2,
                                borderRadius: 3,
                                backgroundColor: "rgba(255,255,255,0.8)",
                                maxWidth: 600,
                            }}
                        >
                            <Typography variant="subtitle1" fontWeight="bold" gutterBottom>
                                Über uns
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                                Wir sind Aya und Lidiia, zwei Teilnehmerinnen des Coding Bootcamps.
                                Gemeinsam haben wir die Spar Fuchs App entwickelt – unser
                                gemeinsames Projekt, das Technik und Kreativität verbindet.
                            </Typography>
                        </Box>
                    </Stack>
                </Paper>
            </Box>

            <Footer />
        </Box>
    )
}