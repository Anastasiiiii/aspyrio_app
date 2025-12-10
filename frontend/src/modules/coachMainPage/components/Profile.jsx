import { useState, useEffect, useCallback } from "react";
import Input from "../../../components/Input";
import Select from "../../../components/Select";
import { getCoachProfile, updateCoachProfile, uploadCoachPhoto, getSports } from "../../../services/api";
import styles from "./styles/Profile.module.css";

const Profile = () => {
    const [profile, setProfile] = useState(null);
    const [form, setForm] = useState({
        firstName: "",
        lastName: "",
        birthDate: "",
        city: "",
        trainingFormat: "",
        description: "",
        achievements: "",
        sportIds: [],
    });
    const [sports, setSports] = useState([]);
    const [photo, setPhoto] = useState(null);
    const [photoPreview, setPhotoPreview] = useState(null);
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [isLoadingProfile, setIsLoadingProfile] = useState(true);
    const [isEditMode, setIsEditMode] = useState(false);

    const trainingFormatOptions = [
        { value: "ONLINE", label: "Online" },
        { value: "OFFLINE", label: "Offline" },
        { value: "BOTH_ONLINE_OFFLINE", label: "Both Online & Offline" },
    ];

    const formatTrainingFormat = (format) => {
        const option = trainingFormatOptions.find(opt => opt.value === format);
        return option ? option.label : format;
    };

    const loadSports = useCallback(async () => {
        try {
            const sportsList = await getSports();
            setSports(sportsList || []);
        } catch (error) {
            console.error("Error loading sports:", error);
        }
    }, []);

    const loadProfile = useCallback(async () => {
        try {
            setIsLoadingProfile(true);
            const loadedProfile = await getCoachProfile();
            
            if (loadedProfile) {
                setProfile(loadedProfile);
                setForm({
                    firstName: loadedProfile.firstName || "",
                    lastName: loadedProfile.lastName || "",
                    birthDate: loadedProfile.birthDate ? loadedProfile.birthDate.split('T')[0] : "",
                    city: loadedProfile.city || "",
                    trainingFormat: loadedProfile.trainingFormat || "",
                    description: loadedProfile.description || "",
                    achievements: loadedProfile.achievements || "",
                    sportIds: loadedProfile.sports?.map(s => s.id.toString()) || [],
                });
                
                if (loadedProfile.photoUrl) {
                    setPhotoPreview(loadedProfile.photoUrl);
                }
            }
        } catch (error) {
            console.error("Error loading profile:", error);
            setProfile(null);
        } finally {
            setIsLoadingProfile(false);
        }
    }, []);

    useEffect(() => {
        loadSports();
        loadProfile();
    }, [loadProfile, loadSports]);

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

    const handleSportChange = (e) => {
        const selectedOptions = Array.from(e.target.selectedOptions, option => option.value);
        setForm(prev => ({
            ...prev,
            sportIds: selectedOptions
        }));
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

        if (!form.trainingFormat) {
            newErrors.trainingFormat = "Training format is required";
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
                await uploadCoachPhoto(photo);
            }

            const createData = {
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                trainingFormat: form.trainingFormat,
            };
            
            if (form.birthDate) {
                createData.birthDate = form.birthDate;
            }
            if (form.city && form.city.trim()) {
                createData.city = form.city.trim();
            }
            if (form.description && form.description.trim()) {
                createData.description = form.description.trim();
            }
            if (form.achievements && form.achievements.trim()) {
                createData.achievements = form.achievements.trim();
            }
            if (form.sportIds && form.sportIds.length > 0) {
                createData.sports = form.sportIds.map(id => ({ id: parseInt(id) }));
            }
            
            const updatedProfile = await updateCoachProfile(createData);

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
                const profileWithPhoto = await uploadCoachPhoto(photo);
                setPhotoPreview(profileWithPhoto.photoUrl);
                setPhoto(null);
            }

            const updateData = {
                firstName: form.firstName.trim(),
                lastName: form.lastName.trim(),
                trainingFormat: form.trainingFormat,
            };
            
            if (form.birthDate) {
                updateData.birthDate = form.birthDate;
            }
            if (form.city && form.city.trim()) {
                updateData.city = form.city.trim();
            }
            if (form.description && form.description.trim()) {
                updateData.description = form.description.trim();
            }
            if (form.achievements && form.achievements.trim()) {
                updateData.achievements = form.achievements.trim();
            }
            if (form.sportIds && form.sportIds.length > 0) {
                updateData.sports = form.sportIds.map(id => ({ id: parseInt(id) }));
            } else {
                updateData.sports = [];
            }
            
            const updatedProfile = await updateCoachProfile(updateData);

            setProfile(updatedProfile);
            setIsEditMode(false);
            
            await loadProfile();
        } catch (error) {
            console.error("Error updating profile:", error);
            console.error("Error response:", error.response);
            const errorMessage = error.response?.data?.message 
                || error.response?.data 
                || error.message 
                || "Error updating profile. Please try again.";
            setErrors({ 
                submit: errorMessage
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
                    Create your coach profile to get started
            </p>
                <div className={styles.message}>
                    <p>You don't have a profile yet. Please fill in the form below to create one.</p>
                </div>

                <form onSubmit={handleCreate} className={styles.form}>
                {/* Photo Upload Section */}
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
                        <Select
                            placeholder="Select Training Format *"
                            name="trainingFormat"
                            value={form.trainingFormat}
                            onChange={handleChange}
                            options={trainingFormatOptions}
                        />
                        {errors.trainingFormat && <span className={styles.error}>{errors.trainingFormat}</span>}
                    </div>

                    <div className={styles.inputWrapper}>
                        <label className={styles.label}>Sports *</label>
                        <select
                            className={styles.select}
                            name="sportIds"
                            multiple
                            value={form.sportIds}
                            onChange={handleSportChange}
                            style={{ minHeight: '100px', padding: '8px' }}
                        >
                            {sports.map(sport => (
                                <option key={sport.id} value={sport.id.toString()}>
                                    {sport.name}
                                </option>
                            ))}
                        </select>
                        <p className={styles.hint}>Hold Ctrl/Cmd to select multiple sports</p>
                        {errors.sportIds && <span className={styles.error}>{errors.sportIds}</span>}
                    </div>

                    {/* Text Areas */}
                    <div className={styles.inputWrapper}>
                        <label className={styles.label}>Description</label>
                        <textarea
                            className={styles.textarea}
                            placeholder="Tell us about yourself..."
                            name="description"
                            value={form.description}
                            onChange={handleChange}
                            rows={5}
                        />
                    </div>

                    <div className={styles.inputWrapper}>
                        <label className={styles.label}>Achievements</label>
                        <textarea
                            className={styles.textarea}
                            placeholder="Your achievements and certifications..."
                            name="achievements"
                            value={form.achievements}
                            onChange={handleChange}
                            rows={5}
                        />
                    </div>

                    {errors.submit && <span className={styles.error}>{errors.submit}</span>}

                    <div className={styles.buttonContainer}>
                        <button 
                            type="submit" 
                            className={styles.button}
                            disabled={isLoading}
                        >
                            {isLoading ? "Creating..." : "Create Profile"}
                        </button>
                    </div>
                </form>
            </div>
        );
    }

    if (isEditMode) {
        return (
            <div className={styles.container}>
                <h1 className={styles.title}>Edit Profile</h1>
                <p className={styles.subtitle}>
                    Update your coach profile information
                </p>

                <form onSubmit={handleUpdate} className={styles.form}>
                    {/* Photo Upload Section */}
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

                    {/* Personal Information */}
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

                <div className={styles.formRow}>
                    <div className={styles.inputWrapper}>
                        <Select
                            placeholder="Select Training Format *"
                            name="trainingFormat"
                            value={form.trainingFormat}
                            onChange={handleChange}
                            options={trainingFormatOptions}
                        />
                        {errors.trainingFormat && <span className={styles.error}>{errors.trainingFormat}</span>}
                    </div>

                    <div className={styles.inputWrapper}>
                        <label className={styles.label}>Sports *</label>
                        <select
                            className={styles.select}
                            name="sportIds"
                            multiple
                            value={form.sportIds}
                            onChange={handleSportChange}
                            style={{ minHeight: '100px', padding: '8px' }}
                        >
                            {sports.map(sport => (
                                <option key={sport.id} value={sport.id.toString()}>
                                    {sport.name}
                                </option>
                            ))}
                        </select>
                        <p className={styles.hint}>Hold Ctrl/Cmd to select multiple sports</p>
                        {errors.sportIds && <span className={styles.error}>{errors.sportIds}</span>}
                    </div>

                {/* Text Areas */}
                <div className={styles.inputWrapper}>
                    <label className={styles.label}>Description</label>
                    <textarea
                        className={styles.textarea}
                        placeholder="Tell us about yourself..."
                        name="description"
                        value={form.description}
                        onChange={handleChange}
                        rows={5}
                    />
                </div>

                <div className={styles.inputWrapper}>
                    <label className={styles.label}>Achievements</label>
                    <textarea
                        className={styles.textarea}
                        placeholder="Your achievements and certifications..."
                        name="achievements"
                        value={form.achievements}
                        onChange={handleChange}
                        rows={5}
                    />
                    </div>

                    {errors.submit && <span className={styles.error}>{errors.submit}</span>}

                    <div className={styles.buttonContainer}>
                        <button 
                            type="button" 
                            className={styles.buttonSecondary}
                            onClick={() => {
                                setIsEditMode(false);
                                loadProfile(); // Reload to reset form
                            }}
                            disabled={isLoading}
                        >
                            Cancel
                        </button>
                        <button 
                            type="submit" 
                            className={styles.button}
                            disabled={isLoading}
                        >
                            {isLoading ? "Saving..." : "Save Changes"}
                        </button>
                    </div>
                </div>
                </form>
            </div>
        );
    }

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Profile</h1>
            <p className={styles.subtitle}>
                Your coach profile information
            </p>

            <div className={styles.profileView}>
                {/* Photo */}
                {profile.photoUrl && (
                    <div className={styles.photoSection}>
                        <img 
                            src={profile.photoUrl} 
                            alt="Profile" 
                            className={styles.profilePhoto}
                        />
                    </div>
                )}

                {/* Personal Information */}
                <div className={styles.infoSection}>
                    <div className={styles.infoRow}>
                        <span className={styles.infoLabel}>First Name:</span>
                        <span className={styles.infoValue}>{profile.firstName || "Not set"}</span>
                    </div>
                    <div className={styles.infoRow}>
                        <span className={styles.infoLabel}>Last Name:</span>
                        <span className={styles.infoValue}>{profile.lastName || "Not set"}</span>
                    </div>
                    {profile.birthDate && (
                        <div className={styles.infoRow}>
                            <span className={styles.infoLabel}>Birth Date:</span>
                            <span className={styles.infoValue}>
                                {new Date(profile.birthDate).toLocaleDateString()}
                            </span>
                        </div>
                    )}
                    {profile.city && (
                        <div className={styles.infoRow}>
                            <span className={styles.infoLabel}>City:</span>
                            <span className={styles.infoValue}>{profile.city}</span>
                        </div>
                    )}
                    {profile.trainingFormat && (
                        <div className={styles.infoRow}>
                            <span className={styles.infoLabel}>Training Format:</span>
                            <span className={styles.infoValue}>
                                {formatTrainingFormat(profile.trainingFormat)}
                            </span>
                        </div>
                    )}
                    {profile.sports && profile.sports.length > 0 && (
                        <div className={styles.infoRow}>
                            <span className={styles.infoLabel}>Sports:</span>
                            <span className={styles.infoValue}>
                                {profile.sports.map(sport => sport.name).join(", ")}
                            </span>
                        </div>
                    )}
                    {profile.description && (
                        <div className={styles.infoRow}>
                            <span className={styles.infoLabel}>Description:</span>
                            <p className={styles.infoText}>{profile.description}</p>
                        </div>
                    )}
                    {profile.achievements && (
                        <div className={styles.infoRow}>
                            <span className={styles.infoLabel}>Achievements:</span>
                            <p className={styles.infoText}>{profile.achievements}</p>
                        </div>
                    )}
                </div>

                <div className={styles.buttonContainer}>
                    <button 
                        className={styles.button}
                        onClick={() => setIsEditMode(true)}
                    >
                        Edit Profile
                    </button>
                </div>
            </div>
        </div>
    );
};

export default Profile;
