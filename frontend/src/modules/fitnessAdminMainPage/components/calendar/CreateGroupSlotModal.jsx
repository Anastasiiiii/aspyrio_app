import { useState, useEffect } from "react";
import Select from "../../../../components/Select";
import Input from "../../../../components/Input";
import { getStudios } from "../../../../services/api";
import styles from "./CreateGroupSlotModal.module.css";

const CreateGroupSlotModal = ({ 
    isOpen, 
    onClose, 
    selectedDate, 
    selectedTime,
    coaches,
    onCreateSlot
}) => {
    const [form, setForm] = useState({
        coachId: "",
        sportId: "",
        studioId: "",
        trainingCategory: "GROUP",
        trainingType: "OFFLINE",
        startDate: "",
        startTime: "",
        endDate: "",
        endTime: "",
        maxParticipants: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);
    const [selectedCoach, setSelectedCoach] = useState(null);
    const [availableSports, setAvailableSports] = useState([]);
    const [studios, setStudios] = useState([]);

    useEffect(() => {
        if (isOpen && selectedDate && selectedTime !== null) {
            const start = new Date(selectedDate);
            start.setHours(selectedTime, 0, 0, 0);
            
            const end = new Date(start);
            end.setHours(end.getHours() + 1);

            setForm({
                coachId: "",
                sportId: "",
                studioId: "",
                trainingCategory: "GROUP",
                trainingType: "OFFLINE",
                startDate: start.toISOString().split('T')[0],
                startTime: start.toTimeString().slice(0, 5),
                endDate: end.toISOString().split('T')[0],
                endTime: end.toTimeString().slice(0, 5),
                maxParticipants: "",
            });
            setSelectedCoach(null);
            setAvailableSports([]);
            setErrors({});
        }
    }, [isOpen, selectedDate, selectedTime]);

    useEffect(() => {
        const loadStudios = async () => {
            try {
                const studiosList = await getStudios();
                setStudios(studiosList || []);
            } catch (error) {
                console.error("Error loading studios:", error);
                setStudios([]);
            }
        };

        if (isOpen) {
            loadStudios();
        }
    }, [isOpen]);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setForm(prev => ({
            ...prev,
            [name]: value
        }));

        if (name === "coachId") {
            const coach = coaches.find(c => c.id.toString() === value);
            setSelectedCoach(coach);
            if (coach) {
                setAvailableSports(coach.sports || []);
                setForm(prev => ({ ...prev, sportId: "" }));
            } else {
                setAvailableSports([]);
            }
        }

        if (errors[name]) {
            setErrors(prev => ({
                ...prev,
                [name]: ''
            }));
        }
    };

    const validate = () => {
        const newErrors = {};

        if (!form.coachId) {
            newErrors.coachId = "Coach is required";
        }

        if (!form.sportId) {
            newErrors.sportId = "Sport is required";
        }

        if (form.trainingCategory === "GROUP" && !form.studioId) {
            newErrors.studioId = "Studio is required for group training";
        }

        if (!form.maxParticipants || parseInt(form.maxParticipants) <= 0) {
            newErrors.maxParticipants = "Max participants must be greater than 0";
        }

        if (!form.startDate || !form.startTime) {
            newErrors.startTime = "Start date and time are required";
        }

        if (!form.endDate || !form.endTime) {
            newErrors.endTime = "End date and time are required";
        }

        if (form.startDate && form.startTime && form.endDate && form.endTime) {
            const start = new Date(`${form.startDate}T${form.startTime}`);
            const end = new Date(`${form.endDate}T${form.endTime}`);
            
            if (end <= start) {
                newErrors.endTime = "End time must be after start time";
            }
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
            const startDateTime = new Date(`${form.startDate}T${form.startTime}`);
            const endDateTime = new Date(`${form.endDate}T${form.endTime}`);

            const slotData = {
                coachId: parseInt(form.coachId),
                sportId: parseInt(form.sportId),
                studioId: form.studioId ? parseInt(form.studioId) : null,
                trainingCategory: form.trainingCategory,
                trainingType: form.trainingType,
                startTime: startDateTime.toISOString(),
                endTime: endDateTime.toISOString(),
                maxParticipants: parseInt(form.maxParticipants),
            };

            await onCreateSlot(slotData);
        } catch (error) {
            setErrors({ submit: error.response?.data?.message || error.message || "Error creating slot" });
        } finally {
            setIsLoading(false);
        }
    };

    if (!isOpen) return null;

    const coachOptions = coaches.map(coach => ({
        value: coach.id.toString(),
        label: `${coach.firstName} ${coach.lastName} (${coach.username})`
    }));

    const sportOptions = availableSports.map(sport => ({
        value: sport.id.toString(),
        label: sport.name
    }));

    const trainingCategoryOptions = [
        { value: "GROUP", label: "Group" },
        { value: "INDIVIDUAL", label: "Individual" }
    ];

    const trainingTypeOptions = [
        { value: "ONLINE", label: "Online" },
        { value: "OFFLINE", label: "Offline" },
        { value: "BOTH_ONLINE_OFFLINE", label: "Both" }
    ];

    return (
        <div className={styles.overlay} onClick={onClose}>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
                <div className={styles.header}>
                    <h2>Create Group Training Slot</h2>
                    <button className={styles.closeButton} onClick={onClose}>×</button>
                </div>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Select
                                placeholder="Select Coach *"
                                name="coachId"
                                value={form.coachId}
                                onChange={handleChange}
                                options={coachOptions}
                            />
                            {errors.coachId && <span className={styles.error}>{errors.coachId}</span>}
                        </div>

                        <div className={styles.inputWrapper}>
                            <Select
                                placeholder="Select Sport *"
                                name="sportId"
                                value={form.sportId}
                                onChange={handleChange}
                                options={sportOptions}
                                disabled={!form.coachId}
                            />
                            {errors.sportId && <span className={styles.error}>{errors.sportId}</span>}
                        </div>
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Select
                                placeholder="Training Category *"
                                name="trainingCategory"
                                value={form.trainingCategory}
                                onChange={handleChange}
                                options={trainingCategoryOptions}
                            />
                        </div>

                        <div className={styles.inputWrapper}>
                            <Select
                                placeholder="Training Type *"
                                name="trainingType"
                                value={form.trainingType}
                                onChange={handleChange}
                                options={trainingTypeOptions}
                            />
                        </div>
                    </div>

                    {form.trainingCategory === "GROUP" && (
                        <div className={styles.inputWrapper}>
                            <Select
                                placeholder="Select Studio *"
                                name="studioId"
                                value={form.studioId}
                                onChange={handleChange}
                                options={studios.map(studio => ({
                                    value: studio.id.toString(),
                                    label: `${studio.name} (Capacity: ${studio.capacity})`
                                }))}
                            />
                            {errors.studioId && <span className={styles.error}>{errors.studioId}</span>}
                        </div>
                    )}

                    <div className={styles.inputWrapper}>
                        <Input
                            placeholder="Max Participants *"
                            name="maxParticipants"
                            type="number"
                            value={form.maxParticipants}
                            onChange={handleChange}
                        />
                        {errors.maxParticipants && <span className={styles.error}>{errors.maxParticipants}</span>}
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Input
                                placeholder="Start Date *"
                                name="startDate"
                                type="date"
                                value={form.startDate}
                                onChange={handleChange}
                            />
                        </div>
                        <div className={styles.inputWrapper}>
                            <Input
                                placeholder="Start Time *"
                                name="startTime"
                                type="time"
                                value={form.startTime}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    <div className={styles.formRow}>
                        <div className={styles.inputWrapper}>
                            <Input
                                placeholder="End Date *"
                                name="endDate"
                                type="date"
                                value={form.endDate}
                                onChange={handleChange}
                            />
                        </div>
                        <div className={styles.inputWrapper}>
                            <Input
                                placeholder="End Time *"
                                name="endTime"
                                type="time"
                                value={form.endTime}
                                onChange={handleChange}
                            />
                        </div>
                    </div>

                    {errors.startTime && <span className={styles.error}>{errors.startTime}</span>}
                    {errors.endTime && <span className={styles.error}>{errors.endTime}</span>}
                    {errors.submit && <span className={styles.error}>{errors.submit}</span>}

                    <div className={styles.buttonContainer}>
                        <button type="button" onClick={onClose} className={styles.cancelButton}>
                            Cancel
                        </button>
                        <button type="submit" className={styles.submitButton} disabled={isLoading}>
                            {isLoading ? "Creating..." : "Create Slot"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default CreateGroupSlotModal;

