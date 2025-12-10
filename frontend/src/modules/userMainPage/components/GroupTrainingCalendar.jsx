import { useState, useEffect, useCallback } from "react";
import { getAvailableGroupTrainingSlots, bookGroupTrainingSlot } from "../../../services/api";
import WeekView from "../../coachMainPage/components/calendar/WeekView";
import BookingModal from "./BookingModal";
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
    const [trainingSlots, setTrainingSlots] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [selectedSlot, setSelectedSlot] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const loadTrainingSlots = useCallback(async () => {
        try {
            setIsLoading(true);
            const weekStart = new Date(currentWeekStart);
            const weekEnd = new Date(weekStart);
            weekEnd.setDate(weekEnd.getDate() + 7);
            
            const slots = await getAvailableGroupTrainingSlots(weekStart, weekEnd);
            
            const transformedSlots = slots.map(slot => ({
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
                availableSpots: slot.availableSpots,
                isBooked: slot.isBooked,
                coachName: slot.coachName
            }));
            
            setTrainingSlots(transformedSlots);
        } catch (error) {
            console.error("Error loading available training slots:", error);
        } finally {
            setIsLoading(false);
        }
    }, [currentWeekStart, refreshTrigger]);

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

    const handleSlotClick = (slot) => {
        setSelectedSlot(slot);
        setIsModalOpen(true);
    };

    const handleDayClick = (date, hour) => {
    };

    const handleBookingSuccess = () => {
        setIsModalOpen(false);
        setSelectedSlot(null);
        setRefreshTrigger(prev => prev + 1);
        window.dispatchEvent(new CustomEvent('bookingUpdated'));
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
                <h1 className={styles.title}>Group Training Calendar</h1>
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
                    onSlotClick={handleSlotClick}
                />
            )}

            {isModalOpen && selectedSlot && (
                <BookingModal
                    isOpen={isModalOpen}
                    onClose={() => {
                        setIsModalOpen(false);
                        setSelectedSlot(null);
                    }}
                    slot={selectedSlot}
                    onBookingSuccess={handleBookingSuccess}
                />
            )}
        </div>
    );
};

export default GroupTrainingCalendar;

