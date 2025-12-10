import styles from "./styles/Checkbox.module.css";

const Checkbox = ({ label, checked, onChange, name }) => {
    return (
        <label className={styles.label}>
            <input 
                type="checkbox" 
                className={styles.checkbox}
                checked={checked}
                onChange={onChange}
                name={name}
            />
            {label}
        </label>
    );
}

export default Checkbox;
