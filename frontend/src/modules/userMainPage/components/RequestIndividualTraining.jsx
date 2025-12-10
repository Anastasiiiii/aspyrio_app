import { useState, useEffect } from "react";
import { getCoachesForUser, createIndividualTrainingRequest } from "../../../services/api";
import Input from "../../../components/Input";
import Select from "../../../components/Select";
import styles from "./styles/RequestIndividualTraining.module.css";

const RequestIndividualTraining = () => {
    const [coaches, setCoaches] = useState([]);
    const [selectedCoach, setSelectedCoach] = useState("");
    const [sports, setSports] = useState([]);
    const [selectedSport, setSelectedSport] = useState("");
    const [startDate, setStartDate] = useState("");
    const [startTime, setStartTime] = useState("");
    const [endDate, setEndDate] = useState("");
    const [endTime, setEndTime] = useState("");
    const [trainingType, setTrainingType] = useState("ONLINE");
    const [message, setMessage] = useState("");
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(false);

    useEffect(() => {
        loadCoaches();
    }, []);

    useEffect(() => {
        if (selectedCoach) {
            const coach = coaches.find(c => c.id.toString() === selectedCoach);
            if (coach && coach.sports) {
                setSports(coach.sports);
                setSelectedSport("");
            } else {
                setSports([]);
            }
        } else {
            setSports([]);
        }
    }, [selectedCoach, coaches]);

    const loadCoaches = async () => {
        try {
            const coachesList = await getCoachesForUser();
            setCoaches(coachesList);
        } catch (error) {
            console.error("Error loading coaches:", error);
            setError("Failed to load coaches: " + (error.response?.data?.message || error.message || "Unknown error"));
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        setError(null);
        setSuccess(false);

        try {
            const startDateTime = new Date(`${startDate}T${startTime}`);
            const endDateTime = new Date(`${endDate}T${endTime}`);

            if (startDateTime >= endDateTime) {
                throw new Error("End time must be after start time");
            }

            if (startDateTime < new Date()) {
                throw new Error("Start time cannot be in the past");
            }

            await createIndividualTrainingRequest({
                coachId: parseInt(selectedCoach),
                sportId: parseInt(selectedSport),
                requestedStartTime: startDateTime.toISOString(),
                requestedEndTime: endDateTime.toISOString(),
                trainingType: trainingType,
                message: message || null
            });

            setSuccess(true);
            // Reset form
            setSelectedCoach("");
            setSelectedSport("");
            setStartDate("");
            setStartTime("");
            setEndDate("");
            setEndTime("");
            setTrainingType("ONLINE");
            setMessage("");
            
            setTimeout(() => setSuccess(false), 3000);
        } catch (err) {
            setError(err.response?.data?.message || err.message || "Error creating request");
        } finally {
            setIsLoading(false);
        }
    };

    const coachOptions = coaches.map(coach => ({
        value: coach.id.toString(),
        label: coach.username
    }));

    const sportOptions = sports.map(sport => ({
        value: sport.id.toString(),
        label: sport.name
    }));

    const trainingTypeOptions = [
        { value: "ONLINE", label: "Online" },
        { value: "OFFLINE", label: "Offline" },
        { value: "BOTH_ONLINE_OFFLINE", label: "Both Online/Offline" }
    ];

    return (
        <div className={styles.container}>
            <h2 className={styles.title}>Request Individual Training</h2>
            
            <form onSubmit={handleSubmit} className={styles.form}>
                <div className={styles.formGroup}>
                    <label className={styles.label}>Coach *</label>
                    <Select
                        value={selectedCoach}
                        onChange={(e) => setSelectedCoach(e.target.value)}
                        options={coachOptions}
                        placeholder="Select a coach"
                        required
                    />
                </div>

                <div className={styles.formGroup}>
                    <label className={styles.label}>Sport *</label>
                    <Select
                        value={selectedSport}
                        onChange={(e) => setSelectedSport(e.target.value)}
                        options={sportOptions}
                        placeholder={selectedCoach ? "Select a sport" : "Select a coach first"}
                        required
                        disabled={!selectedCoach || sports.length === 0}
                    />
                </div>

                <div className={styles.formRow}>
                    <div className={styles.formGroup}>
                        <label className={styles.label}>Start Date *</label>
                        <Input
                            type="date"
                            value={startDate}
                            onChange={(e) => setStartDate(e.target.value)}
                            required
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label className={styles.label}>Start Time *</label>
                        <Input
                            type="time"
                            value={startTime}
                            onChange={(e) => setStartTime(e.target.value)}
                            required
                        />
                    </div>
                </div>

                <div className={styles.formRow}>
                    <div className={styles.formGroup}>
                        <label className={styles.label}>End Date *</label>
                        <Input
                            type="date"
                            value={endDate}
                            onChange={(e) => setEndDate(e.target.value)}
                            required
                        />
                    </div>
                    <div className={styles.formGroup}>
                        <label className={styles.label}>End Time *</label>
                        <Input
                            type="time"
                            value={endTime}
                            onChange={(e) => setEndTime(e.target.value)}
                            required
                        />
                    </div>
                </div>

                <div className={styles.formGroup}>
                    <label className={styles.label}>Training Type *</label>
                    <Select
                        value={trainingType}
                        onChange={(e) => setTrainingType(e.target.value)}
                        options={trainingTypeOptions}
                        required
                    />
                </div>

                <div className={styles.formGroup}>
                    <label className={styles.label}>Message (Optional)</label>
                    <textarea
                        className={styles.textarea}
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        rows={4}
                        placeholder="Add any additional information..."
                    />
                </div>

                {error && <div className={styles.error}>{error}</div>}
                {success && <div className={styles.success}>Request sent successfully!</div>}

                <button
                    type="submit"
                    className={styles.submitButton}
                    disabled={isLoading}
                >
                    {isLoading ? "Sending..." : "Send Request"}
                </button>
            </form>
        </div>
    );
};

export default RequestIndividualTraining;

