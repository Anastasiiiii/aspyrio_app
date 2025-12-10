import styles from "./styles/FitnessAdminsTable.module.css";

const FitnessAdminsTable = ({ admins, isLoading }) => {
    if (isLoading) {
        return (
            <div className={styles.loading}>
                <p>Loading fitness admins...</p>
            </div>
        );
    }

    if (admins.length === 0) {
        return (
            <div className={styles.empty}>
                <p>No fitness admins created yet.</p>
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
                    {admins.map((admin) => (
                        <tr key={admin.id}>
                            <td>{admin.username}</td>
                            <td>{admin.email}</td>
                            <td>{admin.center ? admin.center.name : "-"}</td>
                            <td>
                                {admin.createdAt 
                                    ? new Date(admin.createdAt).toLocaleDateString()
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

export default FitnessAdminsTable;

