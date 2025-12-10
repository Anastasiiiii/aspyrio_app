import { useState, useEffect, useCallback } from "react";
import Input from "../../../components/Input";
import CredentialsModal from "../../../components/CredentialsModal";
import { createRegularUser, getRegularUsers } from "../../../services/api";
import RegularUsersTable from "./RegularUsersTable";
import styles from "./styles/CreateRegularUser.module.css";

const CreateRegularUser = () => {
    const [form, setForm] = useState({
        username: "",
        email: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [successMessage, setSuccessMessage] = useState("");
    const [regularUsers, setRegularUsers] = useState([]);
    const [isLoadingUsers, setIsLoadingUsers] = useState(true);
    const [showModal, setShowModal] = useState(false);
    const [userCredentials, setUserCredentials] = useState({ username: "", password: "" });

    const loadRegularUsers = useCallback(async () => {
        try {
            setIsLoadingUsers(true);
            const usersList = await getRegularUsers();
            console.log("Loaded regular users:", usersList);
            setRegularUsers(usersList || []);
        } catch (error) {
            console.error("Error loading regular users:", error);
            setRegularUsers([]);
            setErrors(prev => ({
                ...prev,
                loadError: "Failed to load regular users. Please refresh the page."
            }));
        } finally {
            setIsLoadingUsers(false);
        }
    }, []);

    useEffect(() => {
        loadRegularUsers();
    }, [loadRegularUsers]);

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
            const response = await createRegularUser({
                username: form.username.trim(),
                email: form.email.trim()
            });

            setUserCredentials({
                username: response.username,
                password: response.password
            });
            setShowModal(true);

            setForm({ 
                username: "",
                email: ""
            });
            
            await loadRegularUsers();
        } catch (error) {
            console.error("Error creating regular user:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating regular user. Please try again." 
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
                username={userCredentials.username}
                password={userCredentials.password}
            />
            <div className={styles.container}>
                <h1 className={styles.title}>Create Regular User</h1>
                <p className={styles.subtitle}>
                    Add a new regular user to your fitness center
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
                            {isLoading ? "Creating..." : "Create User"}
                        </button>
                    </div>
                </form>

                <div className={styles.tableContainer}>
                    <h2 className={styles.tableTitle}>Regular Users</h2>
                    <RegularUsersTable 
                        users={regularUsers} 
                        isLoading={isLoadingUsers}
                    />
                </div>
            </div>
        </>
    );
};

export default CreateRegularUser;

