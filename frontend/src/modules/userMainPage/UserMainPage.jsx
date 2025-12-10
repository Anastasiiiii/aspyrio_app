import { useState, useEffect } from "react";
import Sidebar from "./components/Sidebar";
import Profile from "./components/Profile";
import PersonalCalendar from "./components/PersonalCalendar";
import GroupTrainingCalendar from "./components/GroupTrainingCalendar";
import RequestIndividualTraining from "./components/RequestIndividualTraining";
import QRCodeGenerator from "./components/QRCodeGenerator";
import styles from "./styles/UserMainPage.module.css";

const UserMainPage = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(() => {
        return window.innerWidth > 768;
    });
    const [activeView, setActiveView] = useState("profile");

    useEffect(() => {
        const handleResize = () => {
            if (window.innerWidth > 768) {
                setIsSidebarOpen(true);
            } else {
                setIsSidebarOpen(false);
            }
        };

        window.addEventListener('resize', handleResize);
        return () => window.removeEventListener('resize', handleResize);
    }, []);

    const handleToggleSidebar = () => {
        setIsSidebarOpen(prev => !prev);
    };

    const renderContent = () => {
        switch (activeView) {
            case "profile":
                return <Profile />;
            case "personal-calendar":
                return <PersonalCalendar />;
            case "group-calendar":
                return <GroupTrainingCalendar />;
            case "request-individual":
                return <RequestIndividualTraining />;
            case "qr-code":
                return <QRCodeGenerator />;
            default:
                return <Profile />;
        }
    };

    return (
        <div className={styles.wrapper}>
            <Sidebar 
                isOpen={isSidebarOpen}
                onToggle={handleToggleSidebar}
                activeView={activeView}
                onViewChange={setActiveView}
            />
            <main className={`${styles.mainContent} ${isSidebarOpen ? styles.sidebarOpen : ''}`}>
                {renderContent()}
            </main>
        </div>
    );
};

export default UserMainPage;

