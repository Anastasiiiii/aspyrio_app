import { useState, useEffect } from "react";
import { getAllTrainingSlotRequests, approveRejectTrainingSlot, approveRejectIndividualTrainingRequest } from "../../../services/api";
import styles from "./styles/TrainingRequests.module.css";

const TrainingRequests = ({ refreshTrigger, onRequestProcessed }) => {
    const [groupRequests, setGroupRequests] = useState([]);
    const [individualRequests, setIndividualRequests] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [processingId, setProcessingId] = useState(null);
    const [processingType, setProcessingType] = useState(null); // 'group' or 'individual'

    useEffect(() => {
        loadRequests();
    }, [refreshTrigger]);

    const loadRequests = async () => {
        setIsLoading(true);
        setError(null);
        try {
            const data = await getAllTrainingSlotRequests();
            setGroupRequests(data.groupTrainingRequests || []);
            setIndividualRequests(data.individualTrainingRequests || []);
        } catch (err) {
            setError(err.response?.data?.message || err.message || "Error loading requests");
        } finally {
            setIsLoading(false);
        }
    };

    const handleApprove = async (requestId, type) => {
        setProcessingId(requestId);
        setProcessingType(type);
        try {
            if (type === 'individual') {
                await approveRejectIndividualTrainingRequest({
                    requestId: requestId,
                    action: "APPROVE"
                });
            } else {
                await approveRejectTrainingSlot({
                    requestId: requestId,
                    action: "APPROVE"
                });
            }
            await loadRequests();
            if (onRequestProcessed) {
                onRequestProcessed();
            }
        } catch (err) {
            alert(err.response?.data?.message || err.message || "Error approving request");
        } finally {
            setProcessingId(null);
            setProcessingType(null);
        }
    };

    const handleReject = async (requestId, type) => {
        setProcessingId(requestId);
        setProcessingType(type);
        try {
            if (type === 'individual') {
                await approveRejectIndividualTrainingRequest({
                    requestId: requestId,
                    action: "REJECT"
                });
            } else {
                await approveRejectTrainingSlot({
                    requestId: requestId,
                    action: "REJECT"
                });
            }
            await loadRequests(); // Reload to get updated status
            // Notify parent to refresh calendar
            if (onRequestProcessed) {
                onRequestProcessed();
            }
        } catch (err) {
            alert(err.response?.data?.message || err.message || "Error rejecting request");
        } finally {
            setProcessingId(null);
            setProcessingType(null);
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

    const getStatusBadgeClass = (status) => {
        switch (status) {
            case 'PENDING':
                return styles.statusPending;
            case 'APPROVED':
                return styles.statusApproved;
            case 'REJECTED':
                return styles.statusRejected;
            default:
                return styles.statusDefault;
        }
    };

    const renderGroupRequest = (request) => {
        const slot = request.slotDetails;
        const isProcessing = processingId === request.id && processingType === 'group';
        const isPending = request.status === 'PENDING';

        return (
            <div key={`group-${request.id}`} className={styles.requestCard}>
                <div className={styles.requestCardInner}>
                    <div className={styles.requestHeader}>
                        <div className={styles.requestInfo}>
                            <h3>{slot?.sportName || 'Training Slot'} <span className={styles.requestType}>[Group]</span></h3>
                            <span className={`${styles.statusBadge} ${getStatusBadgeClass(request.status)}`}>
                                {request.status}
                            </span>
                        </div>
                        <div className={styles.requestDate}>
                            Created: {formatDateTime(request.createdAt)}
                        </div>
                    </div>

                    <div className={styles.requestDetails}>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>Category:</span>
                            <span className={styles.value}>{slot?.trainingCategory || 'N/A'}</span>
                        </div>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>Type:</span>
                            <span className={styles.value}>{slot?.trainingType || 'N/A'}</span>
                        </div>
                        {slot?.studioName && (
                            <div className={styles.detailRow}>
                                <span className={styles.label}>Studio:</span>
                                <span className={styles.value}>{slot.studioName}</span>
                            </div>
                        )}
                        <div className={styles.detailRow}>
                            <span className={styles.label}>Start Time:</span>
                            <span className={styles.value}>
                                {slot?.startTime ? formatDateTime(slot.startTime) : 'N/A'}
                            </span>
                        </div>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>End Time:</span>
                            <span className={styles.value}>
                                {slot?.endTime ? formatDateTime(slot.endTime) : 'N/A'}
                            </span>
                        </div>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>Max Participants:</span>
                            <span className={styles.value}>{slot?.maxParticipants || 'N/A'}</span>
                        </div>
                    </div>

                    {isPending && (
                        <div className={styles.requestActions}>
                            <button
                                className={styles.rejectButton}
                                onClick={() => handleReject(request.id, 'group')}
                                disabled={isProcessing}
                            >
                                {isProcessing ? "Processing..." : "Reject"}
                            </button>
                            <button
                                className={styles.approveButton}
                                onClick={() => handleApprove(request.id, 'group')}
                                disabled={isProcessing}
                            >
                                {isProcessing ? "Processing..." : "Approve"}
                            </button>
                        </div>
                    )}

                    {request.coachResponseAt && (
                        <div className={styles.responseDate}>
                            Responded: {formatDateTime(request.coachResponseAt)}
                        </div>
                    )}
                </div>
            </div>
        );
    };

    const renderIndividualRequest = (request) => {
        const isProcessing = processingId === request.id && processingType === 'individual';
        const isPending = request.status === 'PENDING';

        return (
            <div key={`individual-${request.id}`} className={styles.requestCard}>
                <div className={styles.requestCardInner}>
                    <div className={styles.requestHeader}>
                        <div className={styles.requestInfo}>
                            <h3>{request.sportName || 'Training Slot'} <span className={styles.requestType}>[Individual]</span></h3>
                            <span className={`${styles.statusBadge} ${getStatusBadgeClass(request.status)}`}>
                                {request.status}
                            </span>
                        </div>
                        <div className={styles.requestDate}>
                            Created: {formatDateTime(request.createdAt)}
                        </div>
                    </div>

                    <div className={styles.requestDetails}>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>User:</span>
                            <span className={styles.value}>{request.userName || 'N/A'}</span>
                        </div>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>Sport:</span>
                            <span className={styles.value}>{request.sportName || 'N/A'}</span>
                        </div>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>Type:</span>
                            <span className={styles.value}>{request.trainingType || 'N/A'}</span>
                        </div>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>Requested Start Time:</span>
                            <span className={styles.value}>
                                {request.requestedStartTime ? formatDateTime(request.requestedStartTime) : 'N/A'}
                            </span>
                        </div>
                        <div className={styles.detailRow}>
                            <span className={styles.label}>Requested End Time:</span>
                            <span className={styles.value}>
                                {request.requestedEndTime ? formatDateTime(request.requestedEndTime) : 'N/A'}
                            </span>
                        </div>
                        {request.message && (
                            <div className={styles.detailRow}>
                                <span className={styles.label}>Message:</span>
                                <span className={styles.value}>{request.message}</span>
                            </div>
                        )}
                    </div>

                    {isPending && (
                        <div className={styles.requestActions}>
                            <button
                                className={styles.rejectButton}
                                onClick={() => handleReject(request.id, 'individual')}
                                disabled={isProcessing}
                            >
                                {isProcessing ? "Processing..." : "Reject"}
                            </button>
                            <button
                                className={styles.approveButton}
                                onClick={() => handleApprove(request.id, 'individual')}
                                disabled={isProcessing}
                            >
                                {isProcessing ? "Processing..." : "Approve"}
                            </button>
                        </div>
                    )}

                    {request.coachResponseAt && (
                        <div className={styles.responseDate}>
                            Responded: {formatDateTime(request.coachResponseAt)}
                        </div>
                    )}
                </div>
            </div>
        );
    };

    if (isLoading) {
        return (
            <div className={styles.container}>
                <div className={styles.loading}>Loading requests...</div>
            </div>
        );
    }

    if (error) {
        return (
            <div className={styles.container}>
                <div className={styles.error}>{error}</div>
                <button onClick={loadRequests} className={styles.retryButton}>
                    Retry
                </button>
            </div>
        );
    }

    const totalRequests = groupRequests.length + individualRequests.length;

    return (
        <div className={styles.container}>
            <div className={styles.header}>
                <h2>Training Slot Requests</h2>
                <button onClick={loadRequests} className={styles.refreshButton}>
                    Refresh
                </button>
            </div>

            {totalRequests === 0 ? (
                <div className={styles.emptyState}>
                    <p>No training slot requests at the moment.</p>
                </div>
            ) : (
                <div className={styles.requestsList}>
                    {groupRequests.map(renderGroupRequest)}
                    {individualRequests.map(renderIndividualRequest)}
                </div>
            )}
        </div>
    );
};

export default TrainingRequests;
