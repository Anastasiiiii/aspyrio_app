import { useState, useEffect, useCallback } from "react";
import Input from "../../../components/Input";
import { createStudio, getStudios } from "../../../services/api";
import StudiosTable from "./StudiosTable";
import styles from "./styles/CreateStudio.module.css";

const CreateStudio = () => {
    const [form, setForm] = useState({
        name: "",
        capacity: "",
        description: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [studios, setStudios] = useState([]);
    const [isLoadingStudios, setIsLoadingStudios] = useState(true);

    const loadStudios = useCallback(async () => {
        try {
            setIsLoadingStudios(true);
            const studiosList = await getStudios();
            setStudios(studiosList || []);
        } catch (error) {
            console.error("Error loading studios:", error);
            setStudios([]);
            setErrors(prev => ({
                ...prev,
                loadError: "Failed to load studios. Please refresh the page."
            }));
        } finally {
            setIsLoadingStudios(false);
        }
    }, []);

    useEffect(() => {
        loadStudios();
    }, [loadStudios]);

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
            newErrors.name = "Studio name is required";
        }

        if (!form.capacity || parseInt(form.capacity) <= 0) {
            newErrors.capacity = "Capacity must be greater than 0";
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
            await createStudio({
                name: form.name.trim(),
                capacity: parseInt(form.capacity),
                description: form.description.trim() || null
            });

            setSuccessMessage("Studio created successfully!");
            setForm({
                name: "",
                capacity: "",
                description: "",
            });
            await loadStudios();
        } catch (error) {
            console.error("Error creating studio:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating studio. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Create Studio</h1>
            <p className={styles.subtitle}>
                Add a new studio to your fitness center
            </p>

            <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="Studio Name *" 
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                    />
                    {errors.name && <span className={styles.error}>{errors.name}</span>}
                </div>

                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="Capacity *" 
                        name="capacity"
                        type="number"
                        value={form.capacity}
                        onChange={handleChange}
                        min="1"
                    />
                    {errors.capacity && <span className={styles.error}>{errors.capacity}</span>}
                </div>

                <div className={styles.inputWrapper}>
                    <label className={styles.label}>Description</label>
                    <textarea
                        className={styles.textarea}
                        placeholder="Studio description (optional)..."
                        name="description"
                        value={form.description}
                        onChange={handleChange}
                        rows={4}
                    />
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
                        {isLoading ? "Creating..." : "Create Studio"}
                    </button>
                </div>
            </form>

            <div className={styles.tableSection}>
                <h2 className={styles.tableTitle}>Existing Studios</h2>
                {isLoadingStudios ? (
                    <p className={styles.loading}>Loading studios...</p>
                ) : (
                    <StudiosTable studios={studios} onStudioCreated={loadStudios} />
                )}
            </div>
        </div>
    );
};

export default CreateStudio;


