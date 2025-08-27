import { Routes, Route } from "react-router-dom";
import './App.css'
import AboutPage from "./components/About.jsx";
import Home from "./components/Home.jsx";
import Header from "./components/Header.jsx";
import Profile from "./pages/Profile.jsx";
import {AuthProvider} from "./context/AuthContext.jsx";
import Login from "./pages/Login.jsx";
import Register from "./pages/Register.jsx";

function App() {
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
                  <Route path="/profile/*" element={<Profile />} />
              </Routes>
          </main>
          <footer>{/* Optional footer */}</footer>
      </AuthProvider>
  )
}

export default App
