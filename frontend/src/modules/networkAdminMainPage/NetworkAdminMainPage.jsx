import { useState, useEffect } from "react";
import Sidebar from "./components/Sidebar";
import CreateNetwork from "./components/CreateNetwork";
import CreateFitnessCenter from "./components/CreateFitnessCenter";
import CreateFitnessAdmin from "./components/CreateFitnessAdmin";
import ViewReports from "./components/ViewReports";
import styles from "./styles/NetworkAdminMainPage.module.css";

const NetworkAdminMainPage = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(() => {
        return window.innerWidth > 768;
    });
    const [activeView, setActiveView] = useState("create-network");

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
            case "create-network":
                return <CreateNetwork />;
            case "create-fitness-center":
                return <CreateFitnessCenter />;
            case "create-fitness-admin":
                return <CreateFitnessAdmin />;
            case "reports":
                return <ViewReports />;
            default:
                return <CreateNetwork />;
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

export default NetworkAdminMainPage;
