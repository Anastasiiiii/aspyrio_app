import { useState, useEffect, useCallback } from "react";
import { getTrainingSlots, getGroupTrainingSlotsForCalendar, getSports } from "../../../services/api";
import WeekView from "./calendar/WeekView";
import AddTrainingSlotModal from "./calendar/AddTrainingSlotModal";
import styles from "./styles/Calendar.module.css";

const Calendar = () => {
    const [currentWeekStart, setCurrentWeekStart] = useState(() => {
        const today = new Date();
        const dayOfWeek = today.getDay();
        const diff = today.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1); // Monday
        const monday = new Date(today.setDate(diff));
        monday.setHours(0, 0, 0, 0);
        return monday;
    });
    const [trainingSlots, setTrainingSlots] = useState([]);
    const [sports, setSports] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [selectedDate, setSelectedDate] = useState(null);
    const [selectedTime, setSelectedTime] = useState(null);

    const loadSports = useCallback(async () => {
        try {
            const sportsList = await getSports();
            setSports(sportsList);
        } catch (error) {
            console.error("Error loading sports:", error);
        }
    }, []);

    const loadTrainingSlots = useCallback(async () => {
        try {
            setIsLoading(true);
            const weekStart = new Date(currentWeekStart);
            const weekEnd = new Date(weekStart);
            weekEnd.setDate(weekEnd.getDate() + 7);
            
            // Load both individual and group training slots
            const [individualSlots, groupSlots] = await Promise.all([
                getTrainingSlots(weekStart, weekEnd),
                getGroupTrainingSlotsForCalendar(weekStart, weekEnd)
            ]);
            
            // Transform group slots to match individual slot format
            const transformedGroupSlots = groupSlots.map(slot => ({
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
                maxParticipants: slot.maxParticipants
            }));
            
            setTrainingSlots([...individualSlots, ...transformedGroupSlots]);
        } catch (error) {
            console.error("Error loading training slots:", error);
        } finally {
            setIsLoading(false);
        }
    }, [currentWeekStart]);

    useEffect(() => {
        loadSports();
    }, [loadSports]);

    useEffect(() => {
        loadTrainingSlots();
    }, [loadTrainingSlots]);

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

    const handleSlotCreated = () => {
        loadTrainingSlots();
        setIsModalOpen(false);
    };

    const handleDayClick = (date, time) => {
        setSelectedDate(date);
        setSelectedTime(time);
        setIsModalOpen(true);
    };

    const formatWeekRange = () => {
        const weekEnd = new Date(currentWeekStart);
        weekEnd.setDate(weekEnd.getDate() + 6);
        
        const startMonth = currentWeekStart.toLocaleDateString('en-US', { month: 'short' });
        const startDay = currentWeekStart.getDate();
        const endMonth = weekEnd.toLocaleDateString('en-US', { month: 'short' });
        const endDay = weekEnd.getDate();
        const year = currentWeekStart.getFullYear();
        
        if (startMonth === endMonth) {
            return `${startMonth} ${startDay} - ${endDay}, ${year}`;
        } else {
            return `${startMonth} ${startDay} - ${endMonth} ${endDay}, ${year}`;
        }
    };

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h1 className={styles.title}>Calendar</h1>
                <div className={styles.controls}>
                    <button 
                        className={styles.navButton}
                        onClick={handlePreviousWeek}
                        aria-label="Previous week"
                    >
                        ←
                    </button>
                    <button 
                        className={styles.todayButton}
                        onClick={handleToday}
                    >
                        Today
                    </button>
                    <button 
                        className={styles.navButton}
                        onClick={handleNextWeek}
                        aria-label="Next week"
                    >
                        →
                    </button>
                    <span className={styles.weekRange}>{formatWeekRange()}</span>
                </div>
            </div>

            {isLoading ? (
                <div className={styles.loading}>
                    <p>Loading calendar...</p>
                </div>
            ) : (
                <WeekView
                    weekStart={currentWeekStart}
                    trainingSlots={trainingSlots}
                    onDayClick={handleDayClick}
                />
            )}

            {isModalOpen && (
                <AddTrainingSlotModal
                    isOpen={isModalOpen}
                    onClose={() => setIsModalOpen(false)}
                    onSlotCreated={handleSlotCreated}
                    sports={sports}
                    selectedDate={selectedDate}
                    selectedTime={selectedTime}
                />
            )}
        </div>
    );
};

export default Calendar;


