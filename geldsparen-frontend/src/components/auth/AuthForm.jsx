import { useState } from "react";
import { useAuth } from '../../context/AuthContext';
import './AuthForm.css';
import { useNavigate,useLocation} from 'react-router-dom';

export function AuthForm({ mode, onClose, onSwitchMode }) {
    const [errors, setErrors] = useState({});
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
    });
    const { login, register } = useAuth();
    const [isLoading, setIsLoading] = useState(false);
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });

        if (errors[e.target.name]) {
            setErrors({
                ...errors,
                [e.target.name]: ''
            });
        }
    };

    const validateForm = () => {
        const newErrors = {};

        if (mode === "login") {
            if (!formData.username) {
                newErrors.username = 'Username or email is required';
            }
            if (!formData.password) {
                newErrors.password = 'Password is required';
            }
        } else if (mode === "register") {
            if (!formData.username) newErrors.username = 'Username is required';
            if (!formData.email) newErrors.email = 'Email is required';
            if (!formData.password) newErrors.password = 'Password is required';
            if (formData.password !== formData.confirmPassword) {
                newErrors.confirmPassword = 'Passwords do not match';
            }
            if (formData.password.length < 6) {
                newErrors.password = 'Password must be at least 6 characters';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setErrors({});

        if (!validateForm()) {
            setIsLoading(false);
            return;
        }

        try {
            if (mode === "login") {
                console.log("Attempting login...");
                const result = await login({
                    username: formData.username,
                    password: formData.password
                });
                if (result.success) {
                    onClose?.();
                    navigate('/profile');
                } else {
                    setErrors({ submit: result.error });
                }
            } else {
                const result = await register({
                    username: formData.email,
                    email: formData.email,
                    password: formData.password
                });

                if (result.success) {
                    onClose?.();
                    alert(result.message || 'Registration successful! Please login.');
                    onSwitchMode?.();
                } else {
                    setErrors({ submit: result.error });
                }
            }
        } catch (err) {
            setErrors({ submit: err.message || 'An error occurred' });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <div className="auth-card">
                    <div className="auth-header">
                        <h2>{mode === "login" ? "Anmelden" : "Register"}</h2>
                        <p>
                            {mode === "login"
                                ? "Willkommen zurück! Bitte melden Sie sich bei Ihrem Konto an"
                                : "Erstellen Sie ein neues Konto, um loszulegen"
                            }
                        </p>
                    </div>

                    {errors.submit && (
                        <div className="auth-error">
                            <p>{errors.submit}</p>
                        </div>
                    )}

                    <form onSubmit={handleSubmit}>
                        {mode === "register" && (
                            <div className="form-group">
                                <label>E-Mail <span>*</span></label>
                                <input
                                    type="email"
                                    name="email"
                                    value={formData.email}
                                    onChange={handleChange}
                                    placeholder="Geben Sie Ihre E-Mail-Adresse ein"
                                    required
                                />
                                {errors.email && <span className="error-text">{errors.email}</span>}
                            </div>
                        )}

                        <div className="form-group">
                            <label>{mode === "login" ? "Username or Email" : "Username"} <span>*</span></label>
                            <input
                                type="text"
                                name="username"
                                value={formData.username}
                                onChange={handleChange}
                                placeholder={mode === "login" ? "Enter your username or email" : "Enter your username"}
                                required
                            />
                            {errors.username && <span className="error-text">{errors.username}</span>}
                        </div>

                        <div className="form-group">
                            <label>Password <span>*</span></label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Enter your password"
                                required
                            />
                            {errors.password && <span className="error-text">{errors.password}</span>}
                        </div>

                        {mode === "register" && (
                            <div className="form-group">
                                <label>Confirm Password <span>*</span></label>
                                <input
                                    type="password"
                                    name="confirmPassword"
                                    value={formData.confirmPassword}
                                    onChange={handleChange}
                                    placeholder="Confirm your password"
                                    required
                                />
                                {errors.confirmPassword && <span className="error-text">{errors.confirmPassword}</span>}
                            </div>
                        )}

                        <button type="submit" className="auth-submit" disabled={isLoading}>
                            {isLoading ? 'Loading...' : (mode === "login" ? 'Anmelden' : 'Benutzerkonto erstellen')}
                        </button>

                        <div className="auth-toggle">
                            {mode === "login"
                                ? "Sie haben noch kein Konto? "
                                : "Sie haben bereits ein Konto? "}
                            <button
                                type="button"
                                className="auth-link"
                                onClick={onSwitchMode}
                            >
                                {mode === "login" ? "Hier registrieren" : "Hier anmelden"}
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    );
}