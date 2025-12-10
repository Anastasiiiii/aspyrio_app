import styles from "./styles/CoachesTable.module.css";

const CoachesTable = ({ coaches, isLoading }) => {
    if (isLoading) {
        return (
            <div className={styles.loading}>
                <p>Loading coaches...</p>
            </div>
        );
    }

    if (coaches.length === 0) {
        return (
            <div className={styles.empty}>
                <p>No coaches created yet.</p>
            </div>
        );
    }

    return (
        <div className={styles.tableWrapper}>
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>Username</th>
                        <th>Email</th>
                        <th>Fitness Center</th>
                        <th>Created At</th>
                    </tr>
                </thead>
                <tbody>
                    {coaches.map((coach) => (
                        <tr key={coach.id}>
                            <td>{coach.username}</td>
                            <td>{coach.email}</td>
                            <td>{coach.center ? coach.center.name : "-"}</td>
                            <td>
                                {coach.createdAt 
                                    ? new Date(coach.createdAt).toLocaleDateString()
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

export default CoachesTable;

