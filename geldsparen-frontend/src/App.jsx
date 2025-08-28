import { Routes, Route ,Navigate} from "react-router-dom";
import './App.css'
import AboutPage from "./components/About.jsx";
import Home from "./components/Home.jsx";
import Header from "./components/Header.jsx";
import Profile from "./pages/Profile.jsx";
import {AuthProvider} from "./context/AuthContext.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";
import {useEffect,useState} from "react";
import CurrentAccountPage from "./pages/CurrentAccountPage.jsx";
import SavingGoalPage from "./pages/SavingGoalPage.jsx";
import MonthlyPaymentsPage from "./pages/MonthlyPaymentsPage.jsx";
import SpendingPatternPage from "./pages/SpendingPatternPage.jsx";

function App() {
    const [isAuthenticated, setIsAuthenticated] = useState(false);
    const [user, setUser] = useState(null);

    useEffect(() => {
        const token = localStorage.getItem('token');
        const userData = localStorage.getItem('user');
        if (token && userData) {
            setIsAuthenticated(true);
            setUser(JSON.parse(userData));
        }
    }, []);


  return (
      <AuthProvider>
          <main className="container z-0">
              <Routes>
                  {/* Home: show hero + home content */}
                  <Route
                      path="/"
                      element={
                              <Home />
                      }
                  />

                  {/* About for the home navbar */}
                  <Route path="/about" element={<AboutPage />} />
                  {/* Profile with pages: overview, add accounts, actions, dashboards */}
                  <Route path="/*" element={<Profile />} />


              </Routes>
          </main>
          <footer>{/* Optional footer */}</footer>
      </AuthProvider>
  )
}

export default App
