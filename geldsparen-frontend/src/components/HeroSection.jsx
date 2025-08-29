import React from 'react'
import {Typography, Button} from '@mui/material'
import {Link} from "react-router-dom";
import heroImage from "../assets/hero-img.jpg";


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
                    </div>

                    <div className="hero-right login-btn">
                        <img
                            src={heroImage}
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