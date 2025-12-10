import React from "react";
import ImageSide from "../../components/ImageSide.jsx";
import Form from "./components/Form";
import styles from "./styles/NetworkAdminRegistration.module.css";

const NetworkAdminRegistration = () => {
    return (
        <div className={styles.wrapper}>
            <ImageSide />
            <Form />
        </div>
    )
}

export default NetworkAdminRegistration;