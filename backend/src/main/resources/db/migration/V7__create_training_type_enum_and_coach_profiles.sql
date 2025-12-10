CREATE TYPE training_type AS ENUM ('ONLINE', 'OFFLINE', 'BOTH_ONLINE_OFFLINE');

CREATE TABLE coach_profiles (
                                id SERIAL PRIMARY KEY,
                                user_id BIGINT NOT NULL UNIQUE,

                                first_name VARCHAR(100) NOT NULL,
                                last_name VARCHAR(100) NOT NULL,
                                birth_date DATE,
                                city VARCHAR(100),

                                training_format training_type NOT NULL,

                                description TEXT,
                                achievements TEXT,
                                photo_url VARCHAR(300),

                                created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                                updated_at TIMESTAMP NOT NULL DEFAULT NOW(),

                                CONSTRAINT fk_coach_user
                                    FOREIGN KEY (user_id) REFERENCES users(id)
                                        ON DELETE CASCADE
);
