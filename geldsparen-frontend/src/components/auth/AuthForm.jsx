import { useState } from "react";
import { useAuth } from '../../context/AuthContext';
import './AuthForm.css';

export function AuthForm({ mode, onClose, onSwitchMode }) {
    const [errors, setErrors] = useState("");
    const [formData, setFormData] = useState({
        username: '',
        email: '',
        password: '',
        confirmPassword: '',
    });
    const { login } = useAuth();
    const { register } = useAuth();

    const [isLoading, setIsLoading] = useState(false);

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
        if (mode === "login"){
            const newErrors = {};

            if (!formData.email) {
                newErrors.email = 'email is required';
            }
            if (!formData.password) {
                newErrors.password = 'Password is required';
            }

            setErrors(newErrors);
            return Object.keys(newErrors).length === 0;
        }
        else if (mode === "register"){
            const newErrors = {};

            if (!formData.username) newErrors.username = 'Username is required';
            if (!formData.email) newErrors.email = 'Email is required';
            if (!formData.password) newErrors.password = 'Password is required';
            if (formData.password !== formData.confirmPassword) {
                newErrors.confirmPassword = 'Passwords do not match';
            }
            if (formData.password.length < 6) {
                newErrors.password = 'Password must be at least 6 characters';
            }

            setErrors(newErrors);
            return Object.keys(newErrors).length === 0;
        }

    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            if (mode === "login") {
                e.preventDefault();

                if (!validateForm()) return;

                setIsLoading(true);
                const result = await login(formData);

                setIsLoading(false);

                if (result.success) {
                    onClose();
                    // Redirect or show success message
                } else {
                    setErrors({ submit: result.error });
                }
            }

            else {
                e.preventDefault();

                if (!validateForm()) return;

                setIsLoading(true);
                const result = await register({
                    username: formData.username,
                    email: formData.email,
                    password: formData.password
                });

                setIsLoading(false);

                if (result.success) {
                    onClose();
                    alert('Registration successful! Please login.');
                    onSwitchMode();
                } else {
                    setErrors({ submit: result.error });
                }
            }
        } catch (err) {
            setErrors({ submit: err.message });
        }
    };

    return (
        <div className="auth-page">
            <div className="auth-container">
                <div className="auth-card">
                    {/* Header */}
                    <div className="auth-header">
                        <h2>{mode === "login" ? "Anmelden" : "Register"}</h2>
                        <p>
                            {mode === "login"
                                ? "Willkommen zurück! Bitte melden Sie sich bei Ihrem Konto an"
                                : "Erstellen Sie ein neues Konto, um loszulegen"
                            }
                        </p>
                    </div>

                    {/* Error */}
                    {errors.submit && (
                        <div className="auth-error">
                            <p>{errors.submit}</p>
                        </div>
                    )}

                    {/* Form */}
                    <form onSubmit={handleSubmit}>
                        {mode === "register" && (
                            <>
                                <div className="form-group">
                                    <label>Username <span>*</span></label>
                                    <input
                                        type="text"
                                        name="username"
                                        value={formData.username}
                                        onChange={handleChange}
                                        placeholder="Geben Sie Ihren vollständigen Namen ein"
                                        required
                                    />
                                </div>
                            </>
                        )}

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
                        </div>

                        <div className="form-group">
                            <label>Passwort <span>*</span></label>
                            <input
                                type="password"
                                name="password"
                                value={formData.password}
                                onChange={handleChange}
                                placeholder="Geben Sie Ihre Passwort ein"
                                required
                            />
                        </div>

                        {mode === "register" && (
                            <div className="form-group">
                                <label>confirmPassword <span>*</span></label>
                                <input
                                    type="password"
                                    name="confirmPassword"
                                    value={formData.confirmPassword}
                                    onChange={handleChange}
                                    placeholder="Geben Sie Ihr Passwort ein"
                                    required
                                />
                            </div>
                        )}


                        <button type="submit"
                                className="auth-submit"
                                disabled={isLoading}
                        >
                            {isLoading && mode === "login" ? 'Anmelden...' : 'Anmelden'}
                            {isLoading && mode === "register" ? 'Benutzerkonto erstellen...' : 'Benutzerkonto erstellen'}

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

                        {mode === "login" && (
                            <div className="auth-forgot">
                                <button type="button">Passwort vergessen?</button>
                            </div>
                        )}
                    </form>
                </div>
            </div>
        </div>
    );
}