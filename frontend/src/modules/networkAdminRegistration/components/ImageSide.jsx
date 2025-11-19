import registerFormImage from "../../../assets/images/registerFormImage.jpg";
import styles from "./styles/ImageSide.module.css";

const ImageSide = () => {
    return (
        <div className={styles.container}>
            <img
                src={registerFormImage}
                alt="fitness"
                className={styles.image}
            />
        </div>
    );
}

export default ImageSide;
