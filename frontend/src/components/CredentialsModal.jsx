import { useState } from "react";
import Icon from '@mdi/react';
import { mdiContentCopy, mdiClose } from '@mdi/js';
import styles from "./styles/CredentialsModal.module.css";

const CredentialsModal = ({ isOpen, onClose, username, password }) => {
    const [copied, setCopied] = useState(false);

    if (!isOpen) return null;

    const handleCopy = () => {
        const credentials = `Username: ${username}\nPassword: ${password}`;
        navigator.clipboard.writeText(credentials).then(() => {
            setCopied(true);
            setTimeout(() => setCopied(false), 2000);
        }).catch(err => {
            console.error('Failed to copy:', err);
        });
    };

    const handleBackdropClick = (e) => {
        if (e.target === e.currentTarget) {
            onClose();
        }
    };

    return (
        <div className={styles.overlay} onClick={handleBackdropClick}>
            <div className={styles.modal}>
                <button className={styles.closeButton} onClick={onClose}>
                    <Icon path={mdiClose} size={1.5} color="var(--color-text-white)" />
                </button>
                <h2 className={styles.title}>Fitness Admin Created</h2>
                <p className={styles.subtitle}>Save these credentials securely:</p>
                
                <div className={styles.credentials}>
                    <div className={styles.credentialRow}>
                        <span className={styles.label}>Username:</span>
                        <span className={styles.value}>{username}</span>
                    </div>
                    <div className={styles.credentialRow}>
                        <span className={styles.label}>Password:</span>
                        <span className={styles.value}>{password}</span>
                    </div>
                </div>

                <button 
                    className={styles.copyButton}
                    onClick={handleCopy}
                >
                    <Icon 
                        path={mdiContentCopy} 
                        size={1.2} 
                        color="var(--color-text-white)" 
                    />
                    <span>{copied ? "Copied!" : "Copy"}</span>
                </button>
            </div>
        </div>
    );
};

export default CredentialsModal;

