import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'
import { BrowserRouter as Router } from "react-router-dom";
import theme from "../theme.js";
import {CssBaseline, ThemeProvider} from "@mui/material";

createRoot(document.getElementById('root')).render(
  <StrictMode>
      <ThemeProvider theme={theme}>
          <CssBaseline />
      <Router>
    <App />
      </Router>
       </ThemeProvider>
  </StrictMode>,
)
