import { useState, useEffect, useCallback } from "react";
import Input from "../../../components/Input";
import { createFitnessCenter, getFitnessCenters } from "../../../services/api";
import FitnessCentersTable from "./FitnessCentersTable";
import styles from "./styles/CreateFitnessCenter.module.css";

const CreateFitnessCenter = () => {
    const [form, setForm] = useState({
        name: "",
        address: "",
        city: "",
        country: "",
        postalCode: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [fitnessCenters, setFitnessCenters] = useState([]);
    const [isLoadingCenters, setIsLoadingCenters] = useState(true);

    const loadFitnessCenters = useCallback(async () => {
        try {
            setIsLoadingCenters(true);
            const centers = await getFitnessCenters();
            console.log("Loaded fitness centers:", centers);
            setFitnessCenters(centers || []);
        } catch (error) {
            console.error("Error loading fitness centers:", error);
            setFitnessCenters([]);
            setErrors(prev => ({
                ...prev,
                loadError: "Failed to load fitness centers. Please refresh the page."
            }));
        } finally {
            setIsLoadingCenters(false);
        }
    }, []);

    useEffect(() => {
        loadFitnessCenters();
    }, [loadFitnessCenters]);

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
            newErrors.name = "Name is required";
        }

        if (!form.address.trim()) {
            newErrors.address = "Address is required";
        }

        if (!form.city.trim()) {
            newErrors.city = "City is required";
        }

        if (!form.country.trim()) {
            newErrors.country = "Country is required";
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
            await createFitnessCenter({
                name: form.name.trim(),
                address: form.address.trim(),
                city: form.city.trim(),
                country: form.country.trim(),
                postalCode: form.postalCode.trim() || ""
            });

            setSuccessMessage("Fitness center created successfully!");
            setForm({ 
                name: "",
                address: "",
                city: "",
                country: "",
                postalCode: ""
            });
            
            await loadFitnessCenters();
        } catch (error) {
            console.error("Error creating fitness center:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating fitness center. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Create Fitness Center</h1>
            <p className={styles.subtitle}>
                Add a new fitness center to your network
            </p>

            <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="Name" 
                        name="name"
                        value={form.name}
                        onChange={handleChange}
                    />
                    {errors.name && <span className={styles.error}>{errors.name}</span>}
                </div>

                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="Address" 
                        name="address"
                        value={form.address}
                        onChange={handleChange}
                    />
                    {errors.address && <span className={styles.error}>{errors.address}</span>}
                </div>

                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="City" 
                        name="city"
                        value={form.city}
                        onChange={handleChange}
                    />
                    {errors.city && <span className={styles.error}>{errors.city}</span>}
                </div>

                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="Country" 
                        name="country"
                        value={form.country}
                        onChange={handleChange}
                    />
                    {errors.country && <span className={styles.error}>{errors.country}</span>}
                </div>

                <div className={styles.inputWrapper}>
                    <Input 
                        placeholder="Postal Code (optional)" 
                        name="postalCode"
                        value={form.postalCode}
                        onChange={handleChange}
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
                        {isLoading ? "Creating..." : "Create Fitness Center"}
                    </button>
                </div>
            </form>

            <div className={styles.tableContainer}>
                <h2 className={styles.tableTitle}>Fitness Centers</h2>
                <FitnessCentersTable 
                    centers={fitnessCenters} 
                    isLoading={isLoadingCenters}
                />
            </div>
        </div>
    );
};

export default CreateFitnessCenter;
