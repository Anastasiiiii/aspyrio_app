import styles from "./styles/FitnessCentersTable.module.css";

const FitnessCentersTable = ({ centers, isLoading }) => {
    if (isLoading) {
        return (
            <div className={styles.loading}>
                <p>Loading fitness centers...</p>
            </div>
        );
    }

    if (centers.length === 0) {
        return (
            <div className={styles.empty}>
                <p>No fitness centers created yet.</p>
            </div>
        );
    }

    return (
        <div className={styles.tableWrapper}>
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Address</th>
                        <th>City</th>
                        <th>Country</th>
                        <th>Postal Code</th>
                        <th>Created At</th>
                    </tr>
                </thead>
                <tbody>
                    {centers.map((center) => (
                        <tr key={center.id}>
                            <td>{center.name}</td>
                            <td>{center.address}</td>
                            <td>{center.city}</td>
                            <td>{center.country}</td>
                            <td>{center.postalCode || "-"}</td>
                            <td>
                                {center.createdAt 
                                    ? new Date(center.createdAt).toLocaleDateString()
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

export default FitnessCentersTable;


