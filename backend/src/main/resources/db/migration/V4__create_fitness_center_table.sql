CREATE TABLE fitness_center (
                                id BIGSERIAL PRIMARY KEY,
                                name VARCHAR(255) NOT NULL,
                                address VARCHAR(255) NOT NULL,
                                city VARCHAR(100) NOT NULL,
                                country VARCHAR(100) NOT NULL,
                                post_code VARCHAR(20) NOT NULL,
                                date_created TIMESTAMP NOT NULL DEFAULT NOW(),

                                network_id BIGINT NOT NULL,

                                CONSTRAINT fk_fitness_center_network
                                    FOREIGN KEY (network_id)
                                        REFERENCES fitness_center_network (id)
                                        ON DELETE CASCADE
);
