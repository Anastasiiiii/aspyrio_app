import styles from "./TrainingSlotItem.module.css";

const TrainingSlotItem = ({ slot, topPosition }) => {
    const startTime = new Date(slot.startTime);
    const endTime = new Date(slot.endTime);
    
    const formatTime = (date) => {
        return date.toLocaleTimeString('en-US', { 
            hour: '2-digit', 
            minute: '2-digit',
            hour12: false 
        });
    };

    const getDuration = () => {
        const diffMs = endTime - startTime;
        const diffMins = Math.floor(diffMs / 60000);
        if (diffMins < 60) {
            return `${diffMins}m`;
        }
        const hours = Math.floor(diffMins / 60);
        const mins = diffMins % 60;
        return mins > 0 ? `${hours}h ${mins}m` : `${hours}h`;
    };

    const durationMinutes = Math.floor((endTime - startTime) / 60000);
    const height = Math.max(40, (durationMinutes / 60) * 60); // Each hour = 60px

    return (
        <div 
            className={styles.slotItem}
            style={{ 
                height: `${height}px`,
                top: `${topPosition}px`
            }}
            title={`${slot.sport.name} - ${formatTime(startTime)} to ${formatTime(endTime)}`}
        >
            <div className={styles.slotSport}>{slot.sport.name}</div>
            <div className={styles.slotTime}>
                {formatTime(startTime)} - {formatTime(endTime)}
            </div>
            <div className={styles.slotDuration}>{getDuration()}</div>
        </div>
    );
};

export default TrainingSlotItem;

