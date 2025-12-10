import { useState, useEffect, useCallback } from "react";
import { getCoachesWithSports, createGroupTrainingSlot, getGroupTrainingSlotsForCalendar } from "../../../services/api";
import WeekView from "../../coachMainPage/components/calendar/WeekView";
import CreateGroupSlotModal from "./calendar/CreateGroupSlotModal";
import styles from "./styles/GroupTrainingCalendar.module.css";

const GroupTrainingCalendar = () => {
    const [currentWeekStart, setCurrentWeekStart] = useState(() => {
        const today = new Date();
        const dayOfWeek = today.getDay();
        const diff = today.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
        const monday = new Date(today.setDate(diff));
        monday.setHours(0, 0, 0, 0);
        return monday;
    });
    const [coaches, setCoaches] = useState([]);
    const [trainingSlots, setTrainingSlots] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedDate, setSelectedDate] = useState(null);
    const [selectedTime, setSelectedTime] = useState(null);

    const loadCoaches = useCallback(async () => {
        try {
            const coachesList = await getCoachesWithSports();
            setCoaches(coachesList);
        } catch (error) {
            console.error("Error loading coaches:", error);
        }
    }, []);

    const loadTrainingSlots = useCallback(async () => {
        try {
            setIsLoading(true);
            const weekStart = new Date(currentWeekStart);
            const weekEnd = new Date(weekStart);
            weekEnd.setDate(weekEnd.getDate() + 7);
            
            const groupSlots = await getGroupTrainingSlotsForCalendar(weekStart, weekEnd);
            
            const transformedSlots = groupSlots.map(slot => ({
                id: slot.id,
                startTime: slot.startTime,
                endTime: slot.endTime,
                sport: {
                    id: slot.sportId,
                    name: slot.sportName
                },
                isGroupSlot: true,
                trainingCategory: slot.trainingCategory,
                trainingType: slot.trainingType,
                studioName: slot.studioName,
                maxParticipants: slot.maxParticipants,
                coachName: slot.coachName
            }));
            
            setTrainingSlots(transformedSlots);
        } catch (error) {
            console.error("Error loading training slots:", error);
        } finally {
            setIsLoading(false);
        }
    }, [currentWeekStart]);

    useEffect(() => {
        loadCoaches();
    }, [loadCoaches]);

    useEffect(() => {
        loadTrainingSlots();
    }, [loadTrainingSlots]);

    const handleDayClick = (date, time) => {
        setSelectedDate(date);
        setSelectedTime(time);
        setIsModalOpen(true);
    };

    const handleCreateSlot = async (slotData) => {
        try {
            await createGroupTrainingSlot(slotData);
            setIsModalOpen(false);
            setSelectedDate(null);
            setSelectedTime(null);
            loadTrainingSlots();
        } catch (error) {
            console.error("Error creating group training slot:", error);
            throw error;
        }
    };

    const handlePreviousWeek = () => {
        const newWeekStart = new Date(currentWeekStart);
        newWeekStart.setDate(newWeekStart.getDate() - 7);
        setCurrentWeekStart(newWeekStart);
    };

    const handleNextWeek = () => {
        const newWeekStart = new Date(currentWeekStart);
        newWeekStart.setDate(newWeekStart.getDate() + 7);
        setCurrentWeekStart(newWeekStart);
    };

    const handleToday = () => {
        const today = new Date();
        const dayOfWeek = today.getDay();
        const diff = today.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1);
        const monday = new Date(today.setDate(diff));
        monday.setHours(0, 0, 0, 0);
        setCurrentWeekStart(monday);
    };

    return (
        <div className={styles.calendar}>
            <div className={styles.header}>
                <h1 className={styles.title}>Group Training Schedule</h1>
                <div className={styles.controls}>
                    <button onClick={handlePreviousWeek} className={styles.navButton}>← Previous</button>
                    <button onClick={handleToday} className={styles.navButton}>Today</button>
                    <button onClick={handleNextWeek} className={styles.navButton}>Next →</button>
                </div>
            </div>
            {isLoading ? (
                <div className={styles.loading}>Loading calendar...</div>
            ) : (
                <WeekView 
                    weekStart={currentWeekStart}
                    trainingSlots={trainingSlots}
                    onDayClick={handleDayClick}
                />
            )}
            {isModalOpen && (
                <CreateGroupSlotModal
                    isOpen={isModalOpen}
                    onClose={() => {
                        setIsModalOpen(false);
                        setSelectedDate(null);
                        setSelectedTime(null);
                    }}
                    selectedDate={selectedDate}
                    selectedTime={selectedTime}
                    coaches={coaches}
                    onCreateSlot={handleCreateSlot}
                />
            )}
        </div>
    );
};

export default GroupTrainingCalendar;

