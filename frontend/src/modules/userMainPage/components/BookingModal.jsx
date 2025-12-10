import { useState } from "react";
import { bookGroupTrainingSlot } from "../../../services/api";
import styles from "./styles/BookingModal.module.css";

const BookingModal = ({ isOpen, onClose, slot, onBookingSuccess }) => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    if (!isOpen || !slot) return null;

    const formatDateTime = (dateTimeString) => {
        const date = new Date(dateTimeString);
        return date.toLocaleString('uk-UA', {
            day: '2-digit',
            month: '2-digit',
            year: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
    };

    const handleBook = async () => {
        if (slot.isBooked) {
            return;
        }

        setIsLoading(true);
        setError(null);
        try {
            await bookGroupTrainingSlot(slot.id);
            onBookingSuccess();
        } catch (err) {
            setError(err.response?.data?.message || err.message || "Error booking training slot");
        } finally {
            setIsLoading(false);
        }
    };

    return (
        <div className={styles.overlay} onClick={onClose}>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()}>
                <div className={styles.header}>
                    <h2>Training Slot Details</h2>
                    <button className={styles.closeButton} onClick={onClose}>×</button>
                </div>
                <div className={styles.content}>
                    <div className={styles.infoSection}>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Sport:</span>
                            <span className={styles.value}>{slot.sport?.name || 'N/A'}</span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Coach:</span>
                            <span className={styles.value}>{slot.coachName || 'N/A'}</span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Category:</span>
                            <span className={styles.value}>{slot.trainingCategory || 'N/A'}</span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Type:</span>
                            <span className={styles.value}>{slot.trainingType || 'N/A'}</span>
                        </div>
                        {slot.studioName && (
                            <div className={styles.infoRow}>
                                <span className={styles.label}>Studio:</span>
                                <span className={styles.value}>{slot.studioName}</span>
                            </div>
                        )}
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Start Time:</span>
                            <span className={styles.value}>
                                {slot.startTime ? formatDateTime(slot.startTime) : 'N/A'}
                            </span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>End Time:</span>
                            <span className={styles.value}>
                                {slot.endTime ? formatDateTime(slot.endTime) : 'N/A'}
                            </span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Available Spots:</span>
                            <span className={styles.value}>
                                {slot.availableSpots !== undefined ? slot.availableSpots : slot.maxParticipants || 'N/A'} / {slot.maxParticipants || 'N/A'}
                            </span>
                        </div>
                    </div>
                    {error && <div className={styles.error}>{error}</div>}
                    {slot.isBooked ? (
                        <div className={styles.bookedMessage}>
                            You have already booked this training slot.
                        </div>
                    ) : (
                        <div className={styles.buttonContainer}>
                            <button
                                className={styles.cancelButton}
                                onClick={onClose}
                                disabled={isLoading}
                            >
                                Cancel
                            </button>
                            <button
                                className={styles.bookButton}
                                onClick={handleBook}
                                disabled={isLoading || slot.availableSpots === 0}
                            >
                                {isLoading ? "Booking..." : "Book Training"}
                            </button>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default BookingModal;


