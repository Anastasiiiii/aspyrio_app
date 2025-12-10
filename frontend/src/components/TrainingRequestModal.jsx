import { useState, useEffect } from "react";
import { approveRejectTrainingSlot } from "../services/api";
import styles from "./styles/TrainingRequestModal.module.css";

const TrainingRequestModal = ({ isOpen, onClose, slotRequest, onResponse }) => {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {
        if (isOpen && slotRequest) {
            // Focus the modal when it opens
            const modal = document.querySelector('[data-training-request-modal]');
            if (modal) {
                modal.focus();
            }
        }
    }, [isOpen, slotRequest]);

    if (!isOpen || !slotRequest) return null;

    const getRequestId = () => {
        return slotRequest.id || slotRequest.slotDetails?.requestId || slotRequest.slotDetails?.id;
    };

    const handleApprove = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const requestId = getRequestId();
            if (!requestId) {
                throw new Error("Request ID not found");
            }
            await approveRejectTrainingSlot({
                requestId: requestId,
                action: "APPROVE"
            });
            onResponse();
            onClose();
        } catch (err) {
            setError(err.response?.data?.message || err.message || "Error approving request");
        } finally {
            setIsLoading(false);
        }
    };

    const handleReject = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const requestId = getRequestId();
            if (!requestId) {
                throw new Error("Request ID not found");
            }
            await approveRejectTrainingSlot({
                requestId: requestId,
                action: "REJECT"
            });
            onResponse();
            onClose();
        } catch (err) {
            setError(err.response?.data?.message || err.message || "Error rejecting request");
        } finally {
            setIsLoading(false);
        }
    };

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

    return (
        <div className={styles.overlay} onClick={onClose} data-training-request-modal>
            <div className={styles.modal} onClick={(e) => e.stopPropagation()} tabIndex={-1}>
                <div className={styles.header}>
                    <h2>New Training Slot Request</h2>
                    <button className={styles.closeButton} onClick={onClose}>×</button>
                </div>
                <div className={styles.content}>
                    <div className={styles.infoSection}>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Sport:</span>
                            <span className={styles.value}>{slotRequest.slotDetails?.sportName || "N/A"}</span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Category:</span>
                            <span className={styles.value}>{slotRequest.slotDetails?.trainingCategory || "N/A"}</span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Type:</span>
                            <span className={styles.value}>{slotRequest.slotDetails?.trainingType || "N/A"}</span>
                        </div>
                        {slotRequest.slotDetails?.studioName && (
                            <div className={styles.infoRow}>
                                <span className={styles.label}>Studio:</span>
                                <span className={styles.value}>{slotRequest.slotDetails.studioName}</span>
                            </div>
                        )}
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Start Time:</span>
                            <span className={styles.value}>
                                {slotRequest.slotDetails?.startTime 
                                    ? formatDateTime(slotRequest.slotDetails.startTime)
                                    : "N/A"}
                            </span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>End Time:</span>
                            <span className={styles.value}>
                                {slotRequest.slotDetails?.endTime 
                                    ? formatDateTime(slotRequest.slotDetails.endTime)
                                    : "N/A"}
                            </span>
                        </div>
                        <div className={styles.infoRow}>
                            <span className={styles.label}>Max Participants:</span>
                            <span className={styles.value}>{slotRequest.slotDetails?.maxParticipants || "N/A"}</span>
                        </div>
                    </div>
                    {error && <div className={styles.error}>{error}</div>}
                    <div className={styles.buttonContainer}>
                        <button
                            className={styles.rejectButton}
                            onClick={handleReject}
                            disabled={isLoading}
                        >
                            {isLoading ? "Processing..." : "Reject"}
                        </button>
                        <button
                            className={styles.approveButton}
                            onClick={handleApprove}
                            disabled={isLoading}
                        >
                            {isLoading ? "Processing..." : "Approve"}
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default TrainingRequestModal;

