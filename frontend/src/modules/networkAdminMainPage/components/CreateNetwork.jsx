import { useState, useEffect } from "react";
import Input from "../../../components/Input";
import { createNetwork, checkNetwork } from "../../../services/api";
import styles from "./styles/CreateNetwork.module.css";

const CreateNetwork = () => {
    const [form, setForm] = useState({
        name: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [hasNetwork, setHasNetwork] = useState(false);
    const [isChecking, setIsChecking] = useState(true);

    useEffect(() => {
        const checkAdminNetwork = async () => {
            try {
                const response = await checkNetwork();
                setHasNetwork(response.hasNetwork);
            } catch (error) {
                console.error("Error checking network:", error);
            } finally {
                setIsChecking(false);
            }
        };

        checkAdminNetwork();
    }, []);

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

        if (!form.name.trim()) {
            newErrors.name = "Network name is required";
        } else if (form.name.trim().length < 3) {
            newErrors.name = "Name must contain at least 3 characters";
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
            await createNetwork({
                name: form.name.trim()
            });

            setSuccessMessage("Network created successfully!");
            setForm({ name: "" });
            setHasNetwork(true);
        } catch (error) {
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating network. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Create New Network</h1>
            <p className={styles.subtitle}>
                Enter the name of the new fitness center network
            </p>

            <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="Network name" 
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                    />
                    {errors.name && <span className={styles.error}>{errors.name}</span>}
                </div>

                {errors.submit && <span className={styles.error}>{errors.submit}</span>}
                {successMessage && <span className={styles.success}>{successMessage}</span>}

                <div className={styles.buttonContainer}>
                    <button 
                        type="submit" 
                        className={styles.button}
                        disabled={isLoading || hasNetwork || isChecking}
                    >
                        {isLoading ? "Creating..." : hasNetwork ? "Network Already Created" : "Create Network"}
                    </button>
                </div>
            </form>
        </div>
    );
};

export default CreateNetwork;

