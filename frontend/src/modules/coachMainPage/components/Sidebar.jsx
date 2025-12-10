import { useNavigate } from "react-router-dom";
import Cookies from "js-cookie";
import styles from "./styles/Sidebar.module.css";
import Icon from '@mdi/react';
import { mdiLogout, mdiAccount, mdiCalendar, mdiBell } from '@mdi/js';

const Sidebar = ({ isOpen, onToggle, activeView, onViewChange }) => {
    const navigate = useNavigate();

    const handleLogout = () => {
        Cookies.remove('token');
        navigate('/login');
    };

    const getColor = (variableName) => {
        return getComputedStyle(document.documentElement)
            .getPropertyValue(variableName).trim();
    };

    const menuItems = [
        { id: "profile", label: "Profile", icon: mdiAccount },
        { id: "calendar", label: "Calendar", icon: mdiCalendar },
        { id: "requests", label: "Requests", icon: mdiBell },
    ];

    return (
        <>
            {isOpen && (
                <div 
                    className={styles.overlay} 
                    onClick={onToggle}
                />
            )}

            <button 
                className={styles.burgerButton}
                onClick={onToggle}
                aria-label="Toggle menu"
            >
                <span className={`${styles.burgerLine} ${isOpen ? styles.open : ''}`}></span>
                <span className={`${styles.burgerLine} ${isOpen ? styles.open : ''}`}></span>
                <span className={`${styles.burgerLine} ${isOpen ? styles.open : ''}`}></span>
            </button>

            <aside className={`${styles.sidebar} ${isOpen ? styles.open : ''}`}>
                <div className={styles.sidebarHeader}>
                    <h2 className={styles.sidebarTitle}>Menu</h2>
                </div>
                
                <nav className={styles.nav}>
                    <ul className={styles.menuList}>
                        {menuItems.map((item) => (
                            <li key={item.id} className={styles.menuItem}>
                                <button
                                    className={`${styles.menuButton} ${activeView === item.id ? styles.active : ''}`}
                                    onClick={() => {
                                        onViewChange(item.id);
                                        if (window.innerWidth <= 768) {
                                            onToggle();
                                        }
                                    }}
                                >
                                    <span className={styles.menuIcon}>
                                        <Icon 
                                            path={item.icon} 
                                            size={1.2} 
                                            color={getColor('--color-text-white')}
                                        />
                                    </span>
                                    <span className={styles.menuLabel}>{item.label}</span>
                                </button>
                            </li>
                        ))}
                    </ul>
                    <div className={styles.logoutContainer}>
                        <button
                            className={styles.logoutButton}
                            onClick={handleLogout}
                        >
                            <span className={styles.menuIcon}>
                                <Icon
                                    path={mdiLogout}
                                    size={1.2}
                                    color={getColor('--color-text-white')}
                                />
                            </span>
                            <span className={styles.menuLabel}>Logout</span>
                        </button>
                    </div>
                </nav>
            </aside>
        </>
    );
};

export default Sidebar;

