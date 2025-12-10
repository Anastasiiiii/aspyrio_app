import styles from "./styles/StudiosTable.module.css";

const StudiosTable = ({ studios, onStudioCreated }) => {
    if (!studios || studios.length === 0) {
        return (
            <div className={styles.emptyState}>
                <p>No studios found. Create your first studio above.</p>
            </div>
        );
    }

    return (
        <div className={styles.tableContainer}>
            <table className={styles.table}>
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Capacity</th>
                        <th>Description</th>
                        <th>Created At</th>
                    </tr>
                </thead>
                <tbody>
                    {studios.map((studio) => (
                        <tr key={studio.id}>
                            <td>{studio.name}</td>
                            <td>{studio.capacity}</td>
                            <td>{studio.description || "—"}</td>
                            <td>
                                {studio.createdAt 
                                    ? new Date(studio.createdAt).toLocaleDateString()
                                    : "—"}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default StudiosTable;


