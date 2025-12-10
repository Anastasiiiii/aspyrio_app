import DayColumn from "./DayColumn";
import styles from "./WeekView.module.css";

const WeekView = ({ weekStart, trainingSlots, onDayClick }) => {
    const days = [];
    const dayNames = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];
    
    for (let i = 0; i < 7; i++) {
        const date = new Date(weekStart);
        date.setDate(date.getDate() + i);
        days.push(date);
    }

    const getSlotsForDay = (date) => {
        return trainingSlots.filter(slot => {
            const slotDate = new Date(slot.startTime);
            return slotDate.toDateString() === date.toDateString();
        });
    };

    return (
        <div className={styles.weekView}>
            <div className={styles.timeColumn}>
                <div className={styles.timeHeader}></div>
                <div className={styles.timeSlots}>
                    {Array.from({ length: 18 }, (_, i) => {
                        const hour = i + 6; // Start from 6:00
                        return (
                            <div key={hour} className={styles.timeSlot}>
                                {hour.toString().padStart(2, '0')}:00
                            </div>
                        );
                    })}
                </div>
            </div>
            <div className={styles.daysContainer}>
                {days.map((day, index) => (
                    <DayColumn
                        key={day.toISOString()}
                        day={day}
                        dayName={dayNames[index]}
                        slots={getSlotsForDay(day)}
                        onDayClick={onDayClick}
                    />
                ))}
            </div>
        </div>
    );
};

export default WeekView;

