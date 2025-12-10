import { useState, useEffect, useCallback } from "react";
import Input from "../../../components/Input";
import CredentialsModal from "../../../components/CredentialsModal";
import { createCoach, getCoaches } from "../../../services/api";
import CoachesTable from "./CoachesTable";
import styles from "./styles/CreateCoach.module.css";

const CreateCoach = () => {
    const [form, setForm] = useState({
        username: "",
        email: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [coaches, setCoaches] = useState([]);
    const [isLoadingCoaches, setIsLoadingCoaches] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [coachCredentials, setCoachCredentials] = useState({ username: "", password: "" });

    const loadCoaches = useCallback(async () => {
        try {
            setIsLoadingCoaches(true);
            const coachesList = await getCoaches();
            console.log("Loaded coaches:", coachesList);
            setCoaches(coachesList || []);
        } catch (error) {
            console.error("Error loading coaches:", error);
            setCoaches([]);
            setErrors(prev => ({
                ...prev,
                loadError: "Failed to load coaches. Please refresh the page."
            }));
        } finally {
            setIsLoadingCoaches(false);
        }
    }, []);

    useEffect(() => {
        loadCoaches();
    }, [loadCoaches]);

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
        if (successMessage) {
            setSuccessMessage("");
        }
    };

    const validate = () => {
        const newErrors = {};

        if (!form.username.trim()) {
            newErrors.username = "Username is required";
        }

        if (!form.email.trim()) {
            newErrors.email = "Email is required";
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.email)) {
            newErrors.email = "Invalid email format";
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
        setErrors({});
        try {
            const response = await createCoach({
                username: form.username.trim(),
                email: form.email.trim()
            });

            setCoachCredentials({
                username: response.username,
                password: response.password
            });
            setShowModal(true);

            setForm({ 
                username: "",
                email: ""
            });
            
            await loadCoaches();
        } catch (error) {
            console.error("Error creating coach:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating coach. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <>
            <CredentialsModal
                isOpen={showModal}
                onClose={() => setShowModal(false)}
                username={coachCredentials.username}
                password={coachCredentials.password}
            />
            <div className={styles.container}>
                <h1 className={styles.title}>Create Coach</h1>
                <p className={styles.subtitle}>
                    Add a new coach to your fitness center
                </p>

                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.inputWrapper}>
                        <Input 
                            placeholder="Username" 
                            name="username"
                            value={form.username}
                            onChange={handleChange}
                        />
                        {errors.username && <span className={styles.error}>{errors.username}</span>}
                    </div>

                    <div className={styles.inputWrapper}>
                        <Input 
                            placeholder="Email" 
                            type="email"
                            name="email"
                            value={form.email}
                            onChange={handleChange}
                        />
                        {errors.email && <span className={styles.error}>{errors.email}</span>}
                    </div>

                    {errors.submit && <span className={styles.error}>{errors.submit}</span>}
                    {errors.loadError && <span className={styles.error}>{errors.loadError}</span>}
                    {successMessage && <span className={styles.success}>{successMessage}</span>}

                    <div className={styles.buttonContainer}>
                        <button 
                            type="submit" 
                            className={styles.button}
                            disabled={isLoading}
                        >
                            {isLoading ? "Creating..." : "Create Coach"}
                        </button>
                    </div>
                </form>

                <div className={styles.tableContainer}>
                    <h2 className={styles.tableTitle}>Coaches</h2>
                    <CoachesTable 
                        coaches={coaches} 
                        isLoading={isLoadingCoaches}
                    />
                </div>
            </div>
        </>
    );
};

export default CreateCoach;

