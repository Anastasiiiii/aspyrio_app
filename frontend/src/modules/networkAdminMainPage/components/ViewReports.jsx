import { useState, useEffect, useCallback } from "react";
import { getReportsForNetworkAdmin, summarizeReport } from "../../../services/api";
import styles from "./styles/ViewReports.module.css";

const ViewReports = () => {
    const [reports, setReports] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    const [summaries, setSummaries] = useState({});
    const [isSummarizing, setIsSummarizing] = useState({});

    const loadReports = useCallback(async () => {
        try {
            setIsLoading(true);
            setError(null);
            const reportsList = await getReportsForNetworkAdmin();
            setReports(reportsList || []);
        } catch (error) {
            console.error("Error loading reports:", error);
            setError(error.response?.data?.message || error.message || "Failed to load reports");
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        loadReports();
    }, [loadReports]);

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleString();
    };

    const handleDownload = (fileUrl, fileName) => {
        // Open file in new tab for download
        window.open(fileUrl, '_blank');
    };

    const handleSummarize = async (reportId) => {
        if (summaries[reportId]) {
            // If summary already exists, just show it
            return;
        }

        setIsSummarizing(prev => ({ ...prev, [reportId]: true }));
        setError(null);

        try {
            const response = await summarizeReport(reportId);
            setSummaries(prev => ({ ...prev, [reportId]: response.summary }));
        } catch (error) {
            console.error("Error summarizing report:", error);
            setError(error.response?.data?.message || error.message || "Failed to summarize report");
        } finally {
            setIsSummarizing(prev => ({ ...prev, [reportId]: false }));
        }
    };

    const handleCopySummary = async (reportId) => {
        const summary = summaries[reportId];
        if (summary) {
            try {
                await navigator.clipboard.writeText(summary);
                // Show temporary success message
                const button = document.getElementById(`copy-btn-${reportId}`);
                if (button) {
                    const originalText = button.textContent;
                    button.textContent = "Copied!";
                    setTimeout(() => {
                        button.textContent = originalText;
                    }, 2000);
                }
            } catch (error) {
                console.error("Failed to copy:", error);
                setError("Failed to copy to clipboard");
            }
        }
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Reports</h1>
            <p className={styles.subtitle}>
                View all reports from fitness centers in your network.
            </p>

            {error && (
                <div className={styles.error}>
                    {error}
                </div>
            )}

            {isLoading ? (
                <div className={styles.loading}>
                    <p>Loading reports...</p>
                </div>
            ) : reports.length === 0 ? (
                <div className={styles.empty}>
                    <p>No reports available</p>
                </div>
            ) : (
                <div className={styles.reportsList}>
                    {reports.map((report) => (
                        <div key={report.id} className={styles.reportItem}>
                            <div className={styles.reportInfo}>
                                <h3 className={styles.reportName}>{report.fileName || 'Report'}</h3>
                                <div className={styles.reportDetails}>
                                    <div className={styles.detailRow}>
                                        <span className={styles.detailLabel}>From:</span>
                                        <span className={styles.detailValue}>
                                            {report.userName || report.userEmail || `User #${report.userId}`}
                                        </span>
                                    </div>
                                    <div className={styles.detailRow}>
                                        <span className={styles.detailLabel}>Fitness Center:</span>
                                        <span className={styles.detailValue}>
                                            {report.fitnessCenterName || `Center #${report.fitnessCenterId}`}
                                        </span>
                                    </div>
                                    <div className={styles.detailRow}>
                                        <span className={styles.detailLabel}>Uploaded:</span>
                                        <span className={styles.detailValue}>
                                            {formatDate(report.createdAt)}
                                        </span>
                                    </div>
                                </div>
                            </div>
                            <div className={styles.buttonGroup}>
                                <button
                                    className={styles.downloadButton}
                                    onClick={() => handleDownload(report.fileUrl, report.fileName)}
                                >
                                    Download
                                </button>
                                <button
                                    className={styles.summarizeButton}
                                    onClick={() => handleSummarize(report.id)}
                                    disabled={isSummarizing[report.id]}
                                >
                                    {isSummarizing[report.id] ? "Summarizing..." : "Summarize"}
                                </button>
                            </div>
                            {summaries[report.id] && (
                                <div className={styles.summaryContainer}>
                                    <div className={styles.summaryHeader}>
                                        <h4 className={styles.summaryTitle}>Summary</h4>
                                        <button
                                            id={`copy-btn-${report.id}`}
                                            className={styles.copyButton}
                                            onClick={() => handleCopySummary(report.id)}
                                        >
                                            Copy
                                        </button>
                                    </div>
                                    <div className={styles.summaryText}>
                                        {summaries[report.id]}
                                    </div>
                                </div>
                            )}
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default ViewReports;

