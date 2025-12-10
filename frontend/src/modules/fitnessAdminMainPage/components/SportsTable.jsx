import styles from "./styles/SportsTable.module.css";

const SportsTable = ({ sports, isLoading }) => {
    if (isLoading) {
        return (
            <div className={styles.loading}>
                <p>Loading sports...</p>
            </div>
        );
    }

    if (sports.length === 0) {
        return (
            <div className={styles.empty}>
                <p>No sports created yet.</p>
            </div>
        );
    }

    return (
        <div className={styles.tableWrapper}>
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Created At</th>
                    </tr>
                </thead>
                <tbody>
                    {sports.map((sport) => (
                        <tr key={sport.id}>
                            <td>{sport.name}</td>
                            <td>
                                {sport.createdAt 
                                    ? new Date(sport.createdAt).toLocaleDateString()
                                    : "-"
                                }
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default SportsTable;


