import { useState, useEffect } from "react";
import { createTrainingSlot } from "../../../../services/api";
import Select from "../../../../components/Select";
import Input from "../../../../components/Input";
import styles from "./AddTrainingSlotModal.module.css";

const AddTrainingSlotModal = ({ 
    isOpen, 
    onClose, 
    onSlotCreated, 
    sports, 
    selectedDate, 
    selectedTime 
}) => {
    const [form, setForm] = useState({
        sportId: "",
        startDate: "",
        startTime: "",
        endDate: "",
        endTime: "",
    });
    const [errors, setErrors] = useState({});
    const [isLoading, setIsLoading] = useState(false);

    useEffect(() => {
        if (isOpen && selectedDate && selectedTime !== null) {
            const start = new Date(selectedDate);
            start.setHours(selectedTime, 0, 0, 0);
            
            const end = new Date(start);
            end.setHours(end.getHours() + 1);

            setForm({
                sportId: "",
                startDate: start.toISOString().split('T')[0],
                startTime: start.toTimeString().slice(0, 5),
                endDate: end.toISOString().split('T')[0],
                endTime: end.toTimeString().slice(0, 5),
            });
            setErrors({});
        }
    }, [isOpen, selectedDate, selectedTime]);

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

    const validate = () => {
        const newErrors = {};

        if (!form.sportId) {
            newErrors.sportId = "Sport is required";
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
            
            if (start.getHours() < 6) {
                newErrors.startTime = "Start time must be 6:00 or later";
            }
            
            if (end.getHours() < 6) {
                newErrors.endTime = "End time must be 6:00 or later";
            }
            
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
        setErrors({});

        try {
            const startDateTime = new Date(`${form.startDate}T${form.startTime}`);
            const endDateTime = new Date(`${form.endDate}T${form.endTime}`);

            await createTrainingSlot({
                sportId: parseInt(form.sportId),
                startTime: startDateTime.toISOString(),
                endTime: endDateTime.toISOString(),
            });

            onSlotCreated();
        } catch (error) {
            console.error("Error creating training slot:", error);
            setErrors({ 
                submit: error.response?.data?.message || error.message || "Error creating training slot. Please try again." 
            });
        } finally {
            setIsLoading(false);
        }
    };

    if (!isOpen) return null;

    const sportOptions = sports.map(sport => ({
        value: sport.id.toString(),
        label: sport.name
    }));

    return (
        <div className={styles.overlay} onClick={onClose}>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
                <div className={styles.modalHeader}>
                    <h2 className={styles.modalTitle}>Add Training Slot</h2>
                    <button 
                        className={styles.closeButton}
                        onClick={onClose}
                        aria-label="Close"
                    >
                        ×
                    </button>
                </div>

                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.inputWrapper}>
                        <Select
                            placeholder="Select Sport *"
                            name="sportId"
                            value={form.sportId}
                            onChange={handleChange}
                            options={sportOptions}
                        />
                        {errors.sportId && <span className={styles.error}>{errors.sportId}</span>}
                    </div>

                    <div className={styles.timeRow}>
                        <div className={styles.inputWrapper}>
                            <label className={styles.label}>Start Date *</label>
                            <Input 
                                type="date"
                                name="startDate"
                                value={form.startDate}
                                onChange={handleChange}
                            />
                        </div>
                        <div className={styles.inputWrapper}>
                            <label className={styles.label}>Start Time *</label>
                            <Input 
                                type="time"
                                name="startTime"
                                value={form.startTime}
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                    {errors.startTime && <span className={styles.error}>{errors.startTime}</span>}

                    <div className={styles.timeRow}>
                        <div className={styles.inputWrapper}>
                            <label className={styles.label}>End Date *</label>
                            <Input 
                                type="date"
                                name="endDate"
                                value={form.endDate}
                                onChange={handleChange}
                            />
                        </div>
                        <div className={styles.inputWrapper}>
                            <label className={styles.label}>End Time *</label>
                            <Input 
                                type="time"
                                name="endTime"
                                value={form.endTime}
                                onChange={handleChange}
                            />
                        </div>
                    </div>
                    {errors.endTime && <span className={styles.error}>{errors.endTime}</span>}

                    {errors.submit && <span className={styles.error}>{errors.submit}</span>}

                    <div className={styles.buttonContainer}>
                        <button 
                            type="button" 
                            className={styles.buttonSecondary}
                            onClick={onClose}
                            disabled={isLoading}
                        >
                            Cancel
                        </button>
                        <button 
                            type="submit" 
                            className={styles.button}
                            disabled={isLoading}
                        >
                            {isLoading ? "Creating..." : "Create Slot"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default AddTrainingSlotModal;

