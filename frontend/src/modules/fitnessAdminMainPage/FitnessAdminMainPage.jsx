import { useState, useEffect } from "react";
import Sidebar from "./components/Sidebar";
import CreateCoach from "./components/CreateCoach";
import CreateRegularUser from "./components/CreateRegularUser";
import CreateSport from "./components/CreateSport";
import CreateStudio from "./components/CreateStudio";
import GroupTrainingCalendar from "./components/GroupTrainingCalendar";
import UploadReport from "./components/UploadReport";
import styles from "./styles/FitnessAdminMainPage.module.css";

const FitnessAdminMainPage = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(() => {
        return window.innerWidth > 768;
    });
    const [activeView, setActiveView] = useState("group-training-calendar");

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
            case "group-training-calendar":
                return <GroupTrainingCalendar />;
            case "create-studio":
                return <CreateStudio />;
            case "create-coach":
                return <CreateCoach />;
            case "create-regular-user":
                return <CreateRegularUser />;
            case "create-sport":
                return <CreateSport />;
            case "reports":
                return <UploadReport />;
            default:
                return <GroupTrainingCalendar />;
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

export default FitnessAdminMainPage;

