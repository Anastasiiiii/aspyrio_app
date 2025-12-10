import { useState, useEffect, useCallback } from "react";
import Input from "../../../components/Input";
import { getUserProfile, updateUserProfile, uploadUserPhoto } from "../../../services/api";
import styles from "./styles/Profile.module.css";

const Profile = () => {
    const [profile, setProfile] = useState(null);
    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        birthDate: "",
        city: "",
        country: "",
        weight: "",
        height: "",
        goal: "",
        targetWeight: "",
        imageUrl: "",
    });
    const [photo, setPhoto] = useState(null);
    const [photoPreview, setPhotoPreview] = useState(null);
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [isLoadingProfile, setIsLoadingProfile] = useState(true);
    const [isEditMode, setIsEditMode] = useState(false);

    const loadProfile = useCallback(async () => {
        try {
            setIsLoadingProfile(true);
            const loadedProfile = await getUserProfile();
            setProfile(loadedProfile);
            
            if (loadedProfile) {
                setForm({
                    firstName: loadedProfile.firstName || "",
                    lastName: loadedProfile.lastName || "",
                    birthDate: loadedProfile.birthDate || "",
                    city: loadedProfile.city || "",
                    country: loadedProfile.country || "",
                    weight: loadedProfile.weight?.toString() || "",
                    height: loadedProfile.height?.toString() || "",
                    goal: loadedProfile.goal || "",
                    targetWeight: loadedProfile.targetWeight?.toString() || "",
                    imageUrl: loadedProfile.imageUrl || "",
                });
                if (loadedProfile.imageUrl) {
                    setPhotoPreview(loadedProfile.imageUrl);
                }
            }
        } catch (error) {
            console.error("Error loading profile:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Failed to load profile" 
            });
        } finally {
            setIsLoadingProfile(false);
        }
    }, []);

    useEffect(() => {
        loadProfile();
    }, [loadProfile]);

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

    const handlePhotoChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            if (!file.type.startsWith('image/')) {
                setErrors(prev => ({
                    ...prev,
                    photo: "Please select an image file"
                }));
                return;
            }

            if (file.size > 10 * 1024 * 1024) {
                setErrors(prev => ({
                    ...prev,
                    photo: "Image size should be less than 10MB"
                }));
                return;
            }

            setPhoto(file);
            setErrors(prev => ({
                ...prev,
                photo: ''
            }));

            const reader = new FileReader();
            reader.onloadend = () => {
                setPhotoPreview(reader.result);
            };
            reader.readAsDataURL(file);
        }
    };

    const validate = () => {
        const newErrors = {};

        if (!form.firstName.trim()) {
            newErrors.firstName = "First name is required";
        }

        if (!form.lastName.trim()) {
            newErrors.lastName = "Last name is required";
        }

        if (form.weight && isNaN(parseFloat(form.weight))) {
            newErrors.weight = "Weight must be a valid number";
        }

        if (form.height && isNaN(parseFloat(form.height))) {
            newErrors.height = "Height must be a valid number";
        }

        if (form.targetWeight && isNaN(parseFloat(form.targetWeight))) {
            newErrors.targetWeight = "Target weight must be a valid number";
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleCreate = async (e) => {
        e.preventDefault();
        
        if (!validate()) {
            return;
        }

        setIsLoading(true);
        setErrors({});

        try {
            if (photo) {
                await uploadUserPhoto(photo);
            }

            const createData = {
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
            };
            
            if (form.birthDate) {
                createData.birthDate = form.birthDate;
            }
            if (form.city && form.city.trim()) {
                createData.city = form.city.trim();
            }
            if (form.country && form.country.trim()) {
                createData.country = form.country.trim();
            }
            if (form.weight && form.weight.trim()) {
                createData.weight = parseFloat(form.weight);
            }
            if (form.height && form.height.trim()) {
                createData.height = parseFloat(form.height);
            }
            if (form.goal && form.goal.trim()) {
                createData.goal = form.goal.trim();
            }
            if (form.targetWeight && form.targetWeight.trim()) {
                createData.targetWeight = parseFloat(form.targetWeight);
            }
            
            const updatedProfile = await updateUserProfile(createData);
            setProfile(updatedProfile);
            setPhoto(null);
            
            await loadProfile();
        } catch (error) {
            console.error("Error creating profile:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating profile. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    const handleUpdate = async (e) => {
        e.preventDefault();
        
        if (!validate()) {
            return;
        }

        setIsLoading(true);
        setErrors({});

        try {
            if (photo) {
                const profileWithPhoto = await uploadUserPhoto(photo);
                setPhotoPreview(profileWithPhoto.imageUrl);
                setPhoto(null);
            }

            const updateData = {
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
            };
            
            if (form.birthDate) {
                updateData.birthDate = form.birthDate;
            }
            if (form.city && form.city.trim()) {
                updateData.city = form.city.trim();
            }
            if (form.country && form.country.trim()) {
                updateData.country = form.country.trim();
            }
            if (form.weight && form.weight.trim()) {
                updateData.weight = parseFloat(form.weight);
            }
            if (form.height && form.height.trim()) {
                updateData.height = parseFloat(form.height);
            }
            if (form.goal && form.goal.trim()) {
                updateData.goal = form.goal.trim();
            }
            if (form.targetWeight && form.targetWeight.trim()) {
                updateData.targetWeight = parseFloat(form.targetWeight);
            }
            
            const updatedProfile = await updateUserProfile(updateData);
            setProfile(updatedProfile);
            setIsEditMode(false);
            
            await loadProfile();
        } catch (error) {
            console.error("Error updating profile:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error updating profile. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    if (isLoadingProfile) {
        return (
            <div className={styles.container}>
                <div className={styles.loading}>
                    <p>Loading profile...</p>
                </div>
            </div>
        );
    }

    if (!profile) {
        return (
            <div className={styles.container}>
                <h1 className={styles.title}>Profile</h1>
                <p className={styles.subtitle}>
                    Create your profile to get started
                </p>
                <div className={styles.message}>
                    <p>You don't have a profile yet. Please fill in the form below to create one.</p>
                </div>

                <form onSubmit={handleCreate} className={styles.form}>
                    <div className={styles.photoSection}>
                        <label className={styles.photoLabel}>Profile Photo</label>
                        <div className={styles.photoContainer}>
                            {photoPreview && (
                                <img 
                                    src={photoPreview} 
                                    alt="Profile preview" 
                                    className={styles.photoPreview}
                                />
                            )}
                            <div className={styles.photoInputWrapper}>
                                <input
                                    type="file"
                                    accept="image/*"
                                    onChange={handlePhotoChange}
                                    className={styles.fileInput}
                                    id="photo-upload"
                                />
                                <label htmlFor="photo-upload" className={styles.fileInputLabel}>
                                    {photoPreview ? "Change Photo" : "Upload Photo"}
                                </label>
                            </div>
                        </div>
                        {errors.photo && <span className={styles.error}>{errors.photo}</span>}
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="First Name *" 
                                name="firstName"
                                value={form.firstName}
                                onChange={handleChange}
                            />
                            {errors.firstName && <span className={styles.error}>{errors.firstName}</span>}
                        </div>

                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="Last Name *" 
                                name="lastName"
                                value={form.lastName}
                                onChange={handleChange}
                            />
                            {errors.lastName && <span className={styles.error}>{errors.lastName}</span>}
                        </div>
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="Birth Date" 
                                type="date"
                                name="birthDate"
                                value={form.birthDate}
                                onChange={handleChange}
                            />
                        </div>

                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="City" 
                                name="city"
                                value={form.city}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className={styles.inputWrapper}>
                        <Input 
                            placeholder="Country" 
                            name="country"
                            value={form.country}
                            onChange={handleChange}
                        />
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="Weight (kg)" 
                                type="number"
                                step="0.01"
                                name="weight"
                                value={form.weight}
                                onChange={handleChange}
                            />
                            {errors.weight && <span className={styles.error}>{errors.weight}</span>}
                        </div>

                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="Height (cm)" 
                                type="number"
                                step="0.01"
                                name="height"
                                value={form.height}
                                onChange={handleChange}
                            />
                            {errors.height && <span className={styles.error}>{errors.height}</span>}
                        </div>
                    </div>

                    <div className={styles.inputWrapper}>
                        <Input 
                            placeholder="Target Weight (kg)" 
                            type="number"
                            step="0.01"
                            name="targetWeight"
                            value={form.targetWeight}
                            onChange={handleChange}
                        />
                        {errors.targetWeight && <span className={styles.error}>{errors.targetWeight}</span>}
                    </div>

                    <div className={styles.inputWrapper}>
                        <label className={styles.label}>Fitness Goal</label>
                        <textarea
                            className={styles.textarea}
                            placeholder="Describe your fitness goals..."
                            name="goal"
                            value={form.goal}
                            onChange={handleChange}
                            rows={5}
                        />
                    </div>

                    {errors.submit && <span className={styles.error}>{errors.submit}</span>}

                    <div className={styles.buttonContainer}>
                        <button 
                            type="submit" 
                            className={styles.submitButton}
                            disabled={isLoading}
                        >
                            {isLoading ? "Creating..." : "Create Profile"}
                        </button>
                    </div>
                </form>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1 className={styles.title}>Profile</h1>
                {!isEditMode && (
                    <button 
                        className={styles.editButton}
                        onClick={() => setIsEditMode(true)}
                    >
                        Edit Profile
                    </button>
                )}
            </div>

            {isEditMode ? (
                <form onSubmit={handleUpdate} className={styles.form}>
                    <div className={styles.photoSection}>
                        <label className={styles.photoLabel}>Profile Photo</label>
                        <div className={styles.photoContainer}>
                            {photoPreview && (
                                <img 
                                    src={photoPreview} 
                                    alt="Profile preview" 
                                    className={styles.photoPreview}
                                />
                            )}
                            <div className={styles.photoInputWrapper}>
                                <input
                                    type="file"
                                    accept="image/*"
                                    onChange={handlePhotoChange}
                                    className={styles.fileInput}
                                    id="photo-upload-edit"
                                />
                                <label htmlFor="photo-upload-edit" className={styles.fileInputLabel}>
                                    {photoPreview ? "Change Photo" : "Upload Photo"}
                                </label>
                            </div>
                        </div>
                        {errors.photo && <span className={styles.error}>{errors.photo}</span>}
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="First Name *" 
                                name="firstName"
                                value={form.firstName}
                                onChange={handleChange}
                            />
                            {errors.firstName && <span className={styles.error}>{errors.firstName}</span>}
                        </div>

                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="Last Name *" 
                                name="lastName"
                                value={form.lastName}
                                onChange={handleChange}
                            />
                            {errors.lastName && <span className={styles.error}>{errors.lastName}</span>}
                        </div>
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="Birth Date" 
                                type="date"
                                name="birthDate"
                                value={form.birthDate}
                                onChange={handleChange}
                            />
                        </div>

                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="City" 
                                name="city"
                                value={form.city}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className={styles.inputWrapper}>
                        <Input 
                            placeholder="Country" 
                            name="country"
                            value={form.country}
                            onChange={handleChange}
                        />
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="Weight (kg)" 
                                type="number"
                                step="0.01"
                                name="weight"
                                value={form.weight}
                                onChange={handleChange}
                            />
                            {errors.weight && <span className={styles.error}>{errors.weight}</span>}
                        </div>

                        <div className={styles.inputWrapper}>
                            <Input 
                                placeholder="Height (cm)" 
                                type="number"
                                step="0.01"
                                name="height"
                                value={form.height}
                                onChange={handleChange}
                            />
                            {errors.height && <span className={styles.error}>{errors.height}</span>}
                        </div>
                    </div>

                    <div className={styles.inputWrapper}>
                        <Input 
                            placeholder="Target Weight (kg)" 
                            type="number"
                            step="0.01"
                            name="targetWeight"
                            value={form.targetWeight}
                            onChange={handleChange}
                        />
                        {errors.targetWeight && <span className={styles.error}>{errors.targetWeight}</span>}
                    </div>

                    <div className={styles.inputWrapper}>
                        <label className={styles.label}>Fitness Goal</label>
                        <textarea
                            className={styles.textarea}
                            placeholder="Describe your fitness goals..."
                            name="goal"
                            value={form.goal}
                            onChange={handleChange}
                            rows={5}
                        />
                    </div>

                    {errors.submit && <span className={styles.error}>{errors.submit}</span>}

                    <div className={styles.buttonContainer}>
                        <button 
                            type="button" 
                            className={styles.cancelButton}
                            onClick={() => {
                                setIsEditMode(false);
                                loadProfile();
                            }}
                            disabled={isLoading}
                        >
                            Cancel
                        </button>
                        <button 
                            type="submit" 
                            className={styles.submitButton}
                            disabled={isLoading}
                        >
                            {isLoading ? "Updating..." : "Update Profile"}
                        </button>
                    </div>
                </form>
            ) : (
                <div className={styles.profileView}>
                    {profile.imageUrl && (
                        <div className={styles.imageContainer}>
                            <img 
                                src={profile.imageUrl} 
                                alt="Profile" 
                                className={styles.profileImage}
                            />
                        </div>
                    )}
                    
                    <div className={styles.profileInfo}>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Name:</span>
                            <span className={styles.value}>
                                {profile.firstName} {profile.lastName}
                            </span>
                        </div>

                        {profile.birthDate && (
                            <div className={styles.infoRow}>
                                <span className={styles.label}>Birth Date:</span>
                                <span className={styles.value}>
                                    {new Date(profile.birthDate).toLocaleDateString()}
                                </span>
                            </div>
                        )}

                        {(profile.city || profile.country) && (
                            <div className={styles.infoRow}>
                                <span className={styles.label}>Location:</span>
                                <span className={styles.value}>
                                    {[profile.city, profile.country].filter(Boolean).join(", ")}
                                </span>
                            </div>
                        )}

                        {(profile.weight || profile.height) && (
                            <div className={styles.infoRow}>
                                <span className={styles.label}>Physical Info:</span>
                                <span className={styles.value}>
                                    {profile.weight && `${profile.weight} kg`}
                                    {profile.weight && profile.height && " / "}
                                    {profile.height && `${profile.height} cm`}
                                </span>
                            </div>
                        )}

                        {profile.targetWeight && (
                            <div className={styles.infoRow}>
                                <span className={styles.label}>Target Weight:</span>
                                <span className={styles.value}>
                                    {profile.targetWeight} kg
                                </span>
                            </div>
                        )}

                        {profile.goal && (
                            <div className={styles.infoRow}>
                                <span className={styles.label}>Fitness Goal:</span>
                                <span className={styles.value}>{profile.goal}</span>
                            </div>
                        )}
                    </div>
                </div>
            )}
        </div>
    );
};

export default Profile;
