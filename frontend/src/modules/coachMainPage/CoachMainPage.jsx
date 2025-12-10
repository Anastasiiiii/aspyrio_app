import { useState, useEffect } from "react";
import Sidebar from "./components/Sidebar";
import Profile from "./components/Profile";
import Calendar from "./components/Calendar";
import TrainingRequests from "./components/TrainingRequests";
import useWebSocket from "../../hooks/useWebSocket";
import Cookies from "js-cookie";
import styles from "./styles/CoachMainPage.module.css";

const CoachMainPage = () => {
    const [isSidebarOpen, setIsSidebarOpen] = useState(() => {
        return window.innerWidth > 768;
    });
    const [activeView, setActiveView] = useState("profile");
    const [refreshRequests, setRefreshRequests] = useState(0);
    const [refreshCalendar, setRefreshCalendar] = useState(0);

    const getUsername = () => {
        const token = Cookies.get('token');
        if (!token) {
            console.log('CoachMainPage: No token found');
            return null;
        }
        try {
            const decoded = JSON.parse(atob(token.split('.')[1]));
            const username = decoded.sub || decoded.username;
            console.log('CoachMainPage: Extracted username from token:', username);
            return username;
        } catch (e) {
            console.error('CoachMainPage: Error decoding token:', e);
            return null;
        }
    };

    const username = getUsername();

    const handleTrainingRequest = (data) => {
        console.log('Training request received:', data);
        
        // Show browser notification if permission granted
        if ('Notification' in window && Notification.permission === 'granted') {
            new Notification('New Training Slot Request', {
                body: `You have a new training slot request for ${data.sportName || data.slotDetails?.sportName || 'training'}`,
                icon: '/favicon.ico',
                tag: 'training-request'
            });
        } else if ('Notification' in window && Notification.permission !== 'denied') {
            Notification.requestPermission().then(permission => {
                if (permission === 'granted') {
                    new Notification('New Training Slot Request', {
                        body: `You have a new training slot request for ${data.sportName || data.slotDetails?.sportName || 'training'}`,
                        icon: '/favicon.ico',
                        tag: 'training-request'
                    });
                }
            });
        }
        
        setRefreshRequests(prev => prev + 1);
        
        if (activeView !== 'requests') {
            setActiveView('requests');
        }
    };

    const { isConnected } = useWebSocket(username, handleTrainingRequest);
    
    console.log('CoachMainPage: Username:', username, 'WebSocket connected:', isConnected);


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

    const handleRequestProcessed = () => {
        // Refresh calendar when a request is approved/rejected
        setRefreshCalendar(prev => prev + 1);
    };

    const renderContent = () => {
        switch (activeView) {
            case "profile":
                return <Profile />;
            case "calendar":
                return <Calendar key={refreshCalendar} />;
            case "requests":
                return <TrainingRequests 
                    refreshTrigger={refreshRequests} 
                    onRequestProcessed={handleRequestProcessed}
                />;
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

export default CoachMainPage;

