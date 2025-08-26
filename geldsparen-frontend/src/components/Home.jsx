import {Container, Paper, Typography} from "@mui/material";
import Header from "./Header.jsx";
import HeroSection from "./HeroSection.jsx";
import "./styles.css"


export default function Home() {
    return (
        <div className="header-appbar">
            {/* Header  */}
            <Header/>
            <HeroSection />
            <Paper variant="outlined" sx={{ p: 3 }}>
                <Typography color="text.secondary">Footer will be under hero section.</Typography>
            </Paper>
            {/*<Footer />*/}
        </div>
    )
}

