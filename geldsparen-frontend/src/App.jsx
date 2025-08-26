import { Routes, Route } from "react-router-dom";
import './App.css'
import AboutPage from "./components/About.jsx";
import Home from "./components/Home.jsx";
import HeroSection from "./components/HeroSection.jsx";
import Header from "./components/Header.jsx";
import Profile from "./pages/Profile.jsx";

function App() {
  return (
      <>
          {/*<Header />*/}
          <main className="container">
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
      </>
  )
}

export default App
