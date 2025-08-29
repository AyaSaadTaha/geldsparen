import {Paper, Typography} from "@mui/material";
import Header from "./Header.jsx";
import HeroSection from "./HeroSection.jsx";
import "./styles.css"
import Footer from "./Footer.jsx";


export default function Home() {
    return (
        <div className="header-appbar">
            {/* Header  */}
            <Header />
            <HeroSection />
            <Footer />
        </div>
    )
}

