import styles from "./styles/Select.module.css";

const Select = ({ placeholder, value, onChange, name, options, disabled = false }) => {
    return (
        <div className={styles.wrapper}>
            <select
                className={styles.select}
                value={value}
                onChange={onChange}
                name={name}
                disabled={disabled}
            >
                <option value="">{placeholder}</option>
                {options.map((option) => (
                    <option key={option.value} value={option.value}>
                        {option.label}
                    </option>
                ))}
            </select>
        </div>
    );
}

export default Select;

