import React from "react";
import ImageSide from "../../components/ImageSide.jsx";
import LoginForm from "./components/LoginForm";
import styles from "./styles/LoginPage.module.css";

const LoginPage = () => {
    return (
        <div className={styles.wrapper}>
            <ImageSide />
            <LoginForm />
        </div>
    )
}

export default LoginPage;

