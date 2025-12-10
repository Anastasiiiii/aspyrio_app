import { useState, useEffect, useCallback } from "react";
import Input from "../../../components/Input";
import Select from "../../../components/Select";
import CredentialsModal from "../../../components/CredentialsModal";
import { createFitnessAdmin, getFitnessAdmins, getFitnessCenters } from "../../../services/api";
import FitnessAdminsTable from "./FitnessAdminsTable";
import styles from "./styles/CreateFitnessAdmin.module.css";

const CreateFitnessAdmin = () => {
    const [form, setForm] = useState({
        username: "",
        email: "",
        fitnessCenterId: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [fitnessCenters, setFitnessCenters] = useState([]);
    const [fitnessAdmins, setFitnessAdmins] = useState([]);
    const [isLoadingCenters, setIsLoadingCenters] = useState(true);
    const [isLoadingAdmins, setIsLoadingAdmins] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [adminCredentials, setAdminCredentials] = useState({ username: "", password: "" });

    const loadFitnessCenters = useCallback(async () => {
        try {
            setIsLoadingCenters(true);
            const centers = await getFitnessCenters();
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

    const loadFitnessAdmins = useCallback(async () => {
        try {
            setIsLoadingAdmins(true);
            const admins = await getFitnessAdmins();
            console.log("Loaded fitness admins:", admins);
            setFitnessAdmins(admins || []);
        } catch (error) {
            console.error("Error loading fitness admins:", error);
            setFitnessAdmins([]);
            setErrors(prev => ({
                ...prev,
                loadError: "Failed to load fitness admins. Please refresh the page."
            }));
        } finally {
            setIsLoadingAdmins(false);
        }
    }, []);

    useEffect(() => {
        loadFitnessCenters();
        loadFitnessAdmins();
    }, [loadFitnessCenters, loadFitnessAdmins]);

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

        if (!form.fitnessCenterId) {
            newErrors.fitnessCenterId = "Please select a fitness center";
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
            const response = await createFitnessAdmin({
                username: form.username.trim(),
                email: form.email.trim(),
                fitnessCenterId: parseInt(form.fitnessCenterId)
            });

            // Show modal with credentials
            setAdminCredentials({
                username: response.username,
                password: response.password
            });
            setShowModal(true);

            setForm({ 
                username: "",
                email: "",
                fitnessCenterId: ""
            });
            
            await loadFitnessAdmins();
        } catch (error) {
            console.error("Error creating fitness admin:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating fitness admin. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    const centerOptions = fitnessCenters.map(center => ({
        value: center.id,
        label: center.name
    }));

    return (
        <>
            <CredentialsModal
                isOpen={showModal}
                onClose={() => setShowModal(false)}
                username={adminCredentials.username}
                password={adminCredentials.password}
            />
            <div className={styles.container}>
                <h1 className={styles.title}>Create Fitness Admin</h1>
                <p className={styles.subtitle}>
                    Add a new fitness admin to manage a specific center
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

                <div className={styles.inputWrapper}>
                    <Select
                        placeholder="Select Fitness Center"
                        name="fitnessCenterId"
                        value={form.fitnessCenterId}
                        onChange={handleChange}
                        options={centerOptions}
                        disabled={isLoadingCenters || fitnessCenters.length === 0}
                    />
                    {errors.fitnessCenterId && <span className={styles.error}>{errors.fitnessCenterId}</span>}
                    {isLoadingCenters && <span className={styles.info}>Loading centers...</span>}
                    {!isLoadingCenters && fitnessCenters.length === 0 && (
                        <span className={styles.warning}>No fitness centers available. Please create a fitness center first.</span>
                    )}
                </div>

                {errors.submit && <span className={styles.error}>{errors.submit}</span>}
                {errors.loadError && <span className={styles.error}>{errors.loadError}</span>}
                {successMessage && <span className={styles.success}>{successMessage}</span>}

                <div className={styles.buttonContainer}>
                    <button 
                        type="submit" 
                        className={styles.button}
                        disabled={isLoading || isLoadingCenters || fitnessCenters.length === 0}
                    >
                        {isLoading ? "Creating..." : "Create Fitness Admin"}
                    </button>
                </div>
            </form>

            <div className={styles.tableContainer}>
                <h2 className={styles.tableTitle}>Fitness Admins</h2>
                <FitnessAdminsTable 
                    admins={fitnessAdmins} 
                    isLoading={isLoadingAdmins}
                />
            </div>
        </div>
        </>
    );
};

export default CreateFitnessAdmin;

