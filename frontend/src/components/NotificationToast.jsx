import { useEffect } from "react";
import styles from "./styles/NotificationToast.module.css";

const NotificationToast = ({ message, type = "info", onClose, duration = 5000 }) => {
    useEffect(() => {
        if (duration > 0) {
            const timer = setTimeout(() => {
                onClose();
            }, duration);
            return () => clearTimeout(timer);
        }
    }, [duration, onClose]);

    return (
        <div className={`${styles.toast} ${styles[type]}`}>
            <div className={styles.content}>
                <span className={styles.message}>{message}</span>
                <button className={styles.closeButton} onClick={onClose}>×</button>
            </div>
        </div>
    );
};

export default NotificationToast;


