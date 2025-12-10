import { useState, useEffect, useCallback } from "react";
import { uploadReport, getReports } from "../../../services/api";
import styles from "./styles/UploadReport.module.css";

const UploadReport = () => {
    const [file, setFile] = useState(null);
    const [reports, setReports] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [isLoadingReports, setIsLoadingReports] = useState(true);
    const [error, setError] = useState(null);
    const [success, setSuccess] = useState(null);

    const loadReports = useCallback(async () => {
        try {
            setIsLoadingReports(true);
            const reportsList = await getReports();
            setReports(reportsList || []);
        } catch (error) {
            console.error("Error loading reports:", error);
            setError(error.response?.data?.message || error.message || "Failed to load reports");
        } finally {
            setIsLoadingReports(false);
        }
    }, []);

    useEffect(() => {
        loadReports();
    }, [loadReports]);

    const handleFileChange = (e) => {
        const selectedFile = e.target.files[0];
        if (selectedFile) {
            if (!selectedFile.name.toLowerCase().endsWith('.txt')) {
                setError("Only .txt files are allowed");
                setFile(null);
                return;
            }

            if (selectedFile.size > 10 * 1024 * 1024) {
                setError("File size should be less than 10MB");
                setFile(null);
                return;
            }

            setFile(selectedFile);
            setError(null);
            setSuccess(null);
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        if (!file) {
            setError("Please select a file");
            return;
        }

        setIsLoading(true);
        setError(null);
        setSuccess(null);

        try {
            await uploadReport(file);
            setSuccess("Report uploaded successfully!");
            setFile(null);
            // Reset file input
            const fileInput = document.getElementById('report-file-input');
            if (fileInput) {
                fileInput.value = '';
            }
            // Reload reports list
            await loadReports();
        } catch (error) {
            console.error("Error uploading report:", error);
            setError(error.response?.data?.message || error.message || "Failed to upload report");
        } finally {
            setIsLoading(false);
        }
    };

    const formatDate = (dateString) => {
        const date = new Date(dateString);
        return date.toLocaleString();
    };

    const handleDownload = (fileUrl, fileName) => {
        // Open file in new tab for download
        window.open(fileUrl, '_blank');
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Upload Report</h1>
            <p className={styles.subtitle}>
                Upload .txt report files. Files will be stored securely in S3.
            </p>

            <div className={styles.uploadSection}>
                <form onSubmit={handleSubmit} className={styles.form}>
                    <div className={styles.fileInputWrapper}>
                        <label htmlFor="report-file-input" className={styles.fileInputLabel}>
                            {file ? file.name : "Choose .txt file"}
                        </label>
                        <input
                            type="file"
                            id="report-file-input"
                            accept=".txt"
                            onChange={handleFileChange}
                            className={styles.fileInput}
                        />
                    </div>

                    {error && (
                        <div className={styles.error}>
                            {error}
                        </div>
                    )}

                    {success && (
                        <div className={styles.success}>
                            {success}
                        </div>
                    )}

                    <button 
                        type="submit" 
                        className={styles.submitButton}
                        disabled={isLoading || !file}
                    >
                        {isLoading ? "Uploading..." : "Upload Report"}
                    </button>
                </form>
            </div>

            <div className={styles.reportsSection}>
                <h2 className={styles.sectionTitle}>Uploaded Reports</h2>
                
                {isLoadingReports ? (
                    <div className={styles.loading}>
                        <p>Loading reports...</p>
                    </div>
                ) : reports.length === 0 ? (
                    <div className={styles.empty}>
                        <p>No reports uploaded yet</p>
                    </div>
                ) : (
                    <div className={styles.reportsList}>
                        {reports.map((report) => (
                            <div key={report.id} className={styles.reportItem}>
                                <div className={styles.reportInfo}>
                                    <h3 className={styles.reportName}>{report.fileName || 'Report'}</h3>
                                    <p className={styles.reportDate}>
                                        Uploaded: {formatDate(report.createdAt)}
                                    </p>
                                </div>
                                <button
                                    className={styles.downloadButton}
                                    onClick={() => handleDownload(report.fileUrl, report.fileName)}
                                >
                                    Download
                                </button>
                            </div>
                        ))}
                    </div>
                )}
            </div>
        </div>
    );
};

export default UploadReport;


