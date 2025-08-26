import React from 'react'
import {Container, Box, Typography, Button, Link as MLink} from '@mui/material'
import {Link} from "react-router-dom";
import heroImg from "../assets/hero-img.svg";


function HeroSection() {
    return (
        <div className="hero-wrap">
            <div>
                <div className="hero-grid">
                    <div className="hero-left">
                        <Typography variant="h3" className="hero-title" gutterBottom>
                            Spar Fuchs
                        </Typography>
                        <Typography variant="h4" className="hero-subtext">
                            Zusammen sparen, schneller ans Ziel.
                        </Typography>
                        <Typography variant="h6" className="hero-subtext">
                            Ein smarter Begleiter beim Sparen – mit cleveren Tools und intuitiven Funktionen behalten Sie Ihre Finanzen im Griff und erreichen Ihre Sparziele, Cent für Cent.
                        </Typography>

                        <div className="cta-stack">
                            <Button variant="contained" size="large" component={Link} to="/register">Register</Button>
                        </div>
                    </div>

                    <div className="hero-right login-btn">
                        <img
                            src={heroImg}
                            alt="Sparen leicht gemacht"
                            className="hero-image"
                            loading="eager"
                        />
                    </div>
                </div>
            </div>
        </div>
    )
}

export default HeroSection;