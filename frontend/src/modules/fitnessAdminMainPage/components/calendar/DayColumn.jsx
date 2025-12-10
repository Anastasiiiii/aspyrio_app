import TrainingSlotItem from "./TrainingSlotItem";
import styles from "./DayColumn.module.css";

const DayColumn = ({ day, dayName, slots, onDayClick }) => {
    const isToday = day.toDateString() === new Date().toDateString();
    const dayNumber = day.getDate();

    const hours = Array.from({ length: 18 }, (_, i) => i + 6);

    const getSlotsForHour = (hour) => {
        return slots.filter(slot => {
            const slotStart = new Date(slot.startTime);
            return slotStart.getHours() === hour;
        });
    };

    const handleHourClick = (hour) => {
        const clickedDate = new Date(day);
        clickedDate.setHours(hour, 0, 0, 0);
        onDayClick(clickedDate, hour);
    };

    return (
        <div className={styles.dayColumn}>
            <div className={`${styles.dayHeader} ${isToday ? styles.today : ''}`}>
                <div className={styles.dayName}>{dayName}</div>
                <div className={styles.dayNumber}>{dayNumber}</div>
            </div>
            <div className={styles.hoursContainer}>
                {hours.map(hour => (
                    <div 
                        key={hour} 
                        className={styles.hourCell}
                        onClick={() => handleHourClick(hour)}
                    />
                ))}
                {slots.map(slot => {
                    const slotStart = new Date(slot.startTime);
                    const slotHour = slotStart.getHours();
                    const slotMinutes = slotStart.getMinutes();
                    const topPosition = ((slotHour - 6) * 60) + (slotMinutes / 60) * 60;
                    
                    if (slotHour < 6) {
                        return null;
                    }
                    
                    return (
                        <TrainingSlotItem
                            key={slot.id}
                            slot={slot}
                            topPosition={topPosition}
                        />
                    );
                })}
            </div>
        </div>
    );
};

export default DayColumn;

