import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import Cookies from "js-cookie";
import Input from "../../../components/Input";
import SocialLinks from "../../../components/SocialLinks";
import { login } from "../../../services/api";
import styles from "./styles/LoginForm.module.css";

const LoginForm = () => {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        username: "",
        password: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value
        }));
        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validate = () => {
        const newErrors = {};

        if (!form.username.trim()) {
            newErrors.username = "Username is required";
        }

        if (!form.password) {
            newErrors.password = "Password is required";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!validate()) {
            return;
        }

        setIsLoading(true);
        try {
            const response = await login({
                username: form.username,
                password: form.password
            });

            Cookies.set('token', response.token, { expires: 7 }); 

            // Redirect based on user role
            const role = response.role;
            switch (role) {
                case 'NETWORK_ADMIN':
                    navigate('/network-admin');
                    break;
                case 'FITNESS_ADMIN':
                    navigate('/fitness-admin');
                    break;
                case 'COACH':
                    navigate('/coach');
                    break;
                case 'USER':
                    navigate('/user');
                    break;
                default:
                    navigate('/home');
            }
        } catch (error) {
            setErrors({ submit: error.response?.data?.message || error.message || "Login failed. Please check your credentials." });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Welcome Back</h1>
            <p className={styles.subtitle}>
                Sign in to continue your fitness journey...
            </p>

            <form onSubmit={handleSubmit}>
                <Input 
                    placeholder="Username" 
                    name="username"
                    value={form.username}
                    onChange={handleChange}
                />
                {errors.username && <span className={styles.error}>{errors.username}</span>}

                <Input 
                    placeholder="Password" 
                    type="password"
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                />
                {errors.password && <span className={styles.error}>{errors.password}</span>}

                {errors.submit && <span className={styles.error}>{errors.submit}</span>}

                <div className={styles.buttonContainer}>
                    <button 
                        type="submit" 
                        className={styles.loginButton}
                        disabled={isLoading}
                    >
                        {isLoading ? "Signing In..." : "Sign In"}
                    </button>
                </div>
            </form>

            <p className={styles.signup}>
                Don't have an account? <Link to="/">Sign up here</Link>
            </p>

            <SocialLinks />
        </div>
    );
}

export default LoginForm;

