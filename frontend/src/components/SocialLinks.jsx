import { useState } from "react";
import styles from "./styles/SocialLinks.module.css";
import Icon from '@mdi/react';
import { mdiInstagram, mdiFacebook, mdiEmailOutline } from '@mdi/js';

const SocialLinks = () => {
    const [hoveredIcon, setHoveredIcon] = useState(null);

    const iconColor = (iconName) => hoveredIcon === iconName ? "#07D6B6" : "#fff";

    return (
        <div className={styles.wrapper}>
            <Icon 
                className={styles.icon} 
                path={mdiInstagram} 
                size={1} 
                color={iconColor("instagram")}
                onMouseEnter={() => setHoveredIcon("instagram")}
                onMouseLeave={() => setHoveredIcon(null)}
            />
            <Icon 
                className={styles.icon} 
                path={mdiFacebook} 
                size={1} 
                color={iconColor("facebook")}
                onMouseEnter={() => setHoveredIcon("facebook")}
                onMouseLeave={() => setHoveredIcon(null)}
            />
            <Icon 
                className={styles.icon} 
                path={mdiEmailOutline} 
                size={1} 
                color={iconColor("email")}
                onMouseEnter={() => setHoveredIcon("email")}
                onMouseLeave={() => setHoveredIcon(null)}
            />
        </div>
    );
}

export default SocialLinks;
