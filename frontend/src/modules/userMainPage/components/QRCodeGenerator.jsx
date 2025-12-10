import { useState, useEffect, useRef } from "react";
import { generatePass } from "../../../services/api";
import QRCode from "qrcode";
import styles from "./styles/QRCodeGenerator.module.css";

const QRCodeGenerator = () => {
    const [qrCodeDataUrl, setQrCodeDataUrl] = useState(null);
    const [token, setToken] = useState(null);
    const [expiresAt, setExpiresAt] = useState(null);
    const [timeRemaining, setTimeRemaining] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    const intervalRef = useRef(null);

    const generateQRCode = async () => {
        setIsLoading(true);
        setError(null);
        
        try {
            const pass = await generatePass();
            setToken(pass.token);
            setExpiresAt(new Date(pass.expiresAt));
            
            const qrDataUrl = await QRCode.toDataURL(pass.token, {
                width: 300,
                margin: 2,
                color: {
                    dark: '#000000',
                    light: '#FFFFFF'
                }
            });
            setQrCodeDataUrl(qrDataUrl);
        } catch (err) {
            console.error("Error generating pass:", err);
            setError(err.response?.data?.message || err.message || "Failed to generate QR code");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        if (expiresAt) {
            const updateTimeRemaining = () => {
                const now = new Date();
                const remaining = Math.max(0, Math.floor((expiresAt - now) / 1000));
                setTimeRemaining(remaining);
                
                if (remaining === 0) {
                    setQrCodeDataUrl(null);
                    setToken(null);
                    setExpiresAt(null);
                    if (intervalRef.current) {
                        clearInterval(intervalRef.current);
                        intervalRef.current = null;
                    }
                }
            };

            updateTimeRemaining();
            intervalRef.current = setInterval(updateTimeRemaining, 1000);

            return () => {
                if (intervalRef.current) {
                    clearInterval(intervalRef.current);
                }
            };
        }
    }, [expiresAt]);

    useEffect(() => {
        // Auto-generate QR code on mount
        generateQRCode();
    }, []);

    const formatTime = (seconds) => {
        return `${seconds}s`;
    };

    return (
        <div className={styles.container}>
            <h1 className={styles.title}>QR Code Pass</h1>
            <p className={styles.subtitle}>
                Generate a QR code to access the fitness center. The code is valid for 30 seconds.
            </p>

            {error && (
                <div className={styles.error}>
                    {error}
                </div>
            )}

            <div className={styles.qrContainer}>
                {isLoading ? (
                    <div className={styles.loading}>
                        <p>Generating QR code...</p>
                    </div>
                ) : qrCodeDataUrl ? (
                    <>
                        <div className={styles.qrCodeWrapper}>
                            <img 
                                src={qrCodeDataUrl} 
                                alt="QR Code" 
                                className={styles.qrCode}
                            />
                        </div>
                        {timeRemaining > 0 && (
                            <div className={styles.timer}>
                                <span className={styles.timerLabel}>Time remaining:</span>
                                <span className={styles.timerValue}>{formatTime(timeRemaining)}</span>
                            </div>
                        )}
                        {timeRemaining === 0 && (
                            <div className={styles.expired}>
                                QR code has expired
                            </div>
                        )}
                    </>
                ) : (
                    <div className={styles.empty}>
                        <p>Click the button below to generate a QR code</p>
                    </div>
                )}
            </div>

            <div className={styles.buttonContainer}>
                <button 
                    className={styles.generateButton}
                    onClick={generateQRCode}
                    disabled={isLoading}
                >
                    {isLoading ? "Generating..." : "Generate New QR Code"}
                </button>
            </div>
        </div>
    );
};

export default QRCodeGenerator;

