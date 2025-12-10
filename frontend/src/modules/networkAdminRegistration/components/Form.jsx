import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import Cookies from "js-cookie";
import Input from "../../../components/Input";
import Checkbox from "../../../components/Checkbox";
import SocialLinks from "../../../components/SocialLinks";
import { registerNetworkAdmin } from "../../../services/api";
import styles from "./styles/Form.module.css";

const Form = () => {
    const navigate = useNavigate();
    const [form, setForm] = useState({
        name: "",
        email: "",
        password: "",
        repeat: "",
        admin: false,
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);

    const handleChange = (e) => {
        const { name, value, type, checked } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value
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

        if (!form.name.trim()) {
            newErrors.name = "Name is required";
        }

        if (!form.email.trim()) {
            newErrors.email = "Email is required";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
            newErrors.email = "Invalid email format";
        }

        if (!form.password) {
            newErrors.password = "Password is required";
        } else if (form.password.length < 6) {
            newErrors.password = "Password must be at least 6 characters";
        }

        if (form.password !== form.repeat) {
            newErrors.repeat = "Passwords do not match";
        }

        if (!form.admin) {
            newErrors.admin = "You must confirm that you are the Network Administrator";
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
            const response = await registerNetworkAdmin({
                username: form.name,
                email: form.email,
                password: form.password
            });

            Cookies.set('token', response.token, { expires: 7 }); 

            navigate('/home');
        } catch (error) {
            setErrors({ submit: error.message || "Registration failed. Please try again." });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Let's Get Started</h1>
            <p className={styles.subtitle}>
                Join our community and transform your fitness journey...
            </p>

            <form onSubmit={handleSubmit}>
                <Input 
                    placeholder="Your name" 
                    name="name"
                    value={form.name}
                    onChange={handleChange}
                />
                {errors.name && <span className={styles.error}>{errors.name}</span>}

                <Input 
                    placeholder="Your email" 
                    type="email"
                    name="email"
                    value={form.email}
                    onChange={handleChange}
                />
                {errors.email && <span className={styles.error}>{errors.email}</span>}

                <Input 
                    placeholder="Create password" 
                    type="password"
                    name="password"
                    value={form.password}
                    onChange={handleChange}
                />
                {errors.password && <span className={styles.error}>{errors.password}</span>}

                <Input 
                    placeholder="Repeat password" 
                    type="password"
                    name="repeat"
                    value={form.repeat}
                    onChange={handleChange}
                />
                {errors.repeat && <span className={styles.error}>{errors.repeat}</span>}

                <Checkbox 
                    label="I am the Network Administrator" 
                    checked={form.admin}
                    onChange={handleChange}
                    name="admin"
                />
                {errors.admin && <span className={styles.error}>{errors.admin}</span>}

                {errors.submit && <span className={styles.error}>{errors.submit}</span>}

                <div className={styles.buttonContainer}>
                    <button 
                        type="submit" 
                        className={styles.signUpButton}
                        disabled={isLoading}
                    >
                        {isLoading ? "Signing Up..." : "Sign Up"}
                    </button>
                </div>
            </form>

            <p className={styles.signin}>
                Already a Member? <Link to="/login">Sign in here</Link>
            </p>

            <SocialLinks />
        </div>
    );
}

export default Form;
