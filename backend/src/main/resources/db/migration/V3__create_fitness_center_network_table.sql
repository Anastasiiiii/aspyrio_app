CREATE TABLE fitness_center_network (
                                        id BIGSERIAL PRIMARY KEY,
                                        name VARCHAR(100) NOT NULL,
                                        network_admin_id BIGINT NOT NULL REFERENCES users(id),
                                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
