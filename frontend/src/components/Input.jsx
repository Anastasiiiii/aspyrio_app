import styles from "./styles/Input.module.css";

const Input = ({ placeholder, type = "text", value, onChange, name }) => {
    return (
        <div className={styles.wrapper}>
            <input
                type={type}
                placeholder={placeholder}
                className={styles.input}
                value={value}
                onChange={onChange}
                name={name}
            />
        </div>
    );
}

export default Input;
