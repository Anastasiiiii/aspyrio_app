import { useState, useEffect, useCallback } from "react";
import Input from "../../../components/Input";
import { createSport, getSports } from "../../../services/api";
import SportsTable from "./SportsTable";
import styles from "./styles/CreateSport.module.css";

const CreateSport = () => {
    const [form, setForm] = useState({
        name: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [sports, setSports] = useState([]);
    const [isLoadingSports, setIsLoadingSports] = useState(true);

    const loadSports = useCallback(async () => {
        try {
            setIsLoadingSports(true);
            const sportsList = await getSports();
            console.log("Loaded sports:", sportsList);
            setSports(sportsList || []);
        } catch (error) {
            console.error("Error loading sports:", error);
            setSports([]);
            setErrors(prev => ({
                ...prev,
                loadError: "Failed to load sports. Please refresh the page."
            }));
        } finally {
            setIsLoadingSports(false);
        }
    }, []);

    useEffect(() => {
        loadSports();
    }, [loadSports]);

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
            newErrors.name = "Sport name is required";
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
            await createSport({
                name: form.name.trim()
            });

            setSuccessMessage("Sport created successfully!");
            setForm({ 
                name: ""
            });
            
            await loadSports();
        } catch (error) {
            console.error("Error creating sport:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating sport. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Create Sport</h1>
            <p className={styles.subtitle}>
                Add a new sport to the system
            </p>

            <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="Sport Name" 
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                    />
                    {errors.name && <span className={styles.error}>{errors.name}</span>}
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
                        {isLoading ? "Creating..." : "Create Sport"}
                    </button>
                </div>
            </form>

            <div className={styles.tableContainer}>
                <h2 className={styles.tableTitle}>Sports</h2>
                <SportsTable 
                    sports={sports} 
                    isLoading={isLoadingSports}
                />
            </div>
        </div>
    );
};

export default CreateSport;


