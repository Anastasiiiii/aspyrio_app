import styles from "./styles/RegularUsersTable.module.css";

const RegularUsersTable = ({ users, isLoading }) => {
    if (isLoading) {
        return (
            <div className={styles.loading}>
                <p>Loading regular users...</p>
            </div>
        );
    }

    if (users.length === 0) {
        return (
            <div className={styles.empty}>
                <p>No regular users created yet.</p>
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
                    {users.map((user) => (
                        <tr key={user.id}>
                            <td>{user.username}</td>
                            <td>{user.email}</td>
                            <td>{user.center ? user.center.name : "-"}</td>
                            <td>
                                {user.createdAt 
                                    ? new Date(user.createdAt).toLocaleDateString()
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

export default RegularUsersTable;

