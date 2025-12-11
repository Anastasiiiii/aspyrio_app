CREATE TABLE IF NOT EXISTS user_profile (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    birth_date DATE,
    city VARCHAR(255),
    country VARCHAR(255),
    weight DECIMAL(5,2),
    height DECIMAL(5,2),
    goal VARCHAR(500),
    target_weight DECIMAL(5,2),
    image_url VARCHAR(500),
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

ALTER TABLE user_profile
    ADD CONSTRAINT fk_user_profile_user
        FOREIGN KEY (user_id)
            REFERENCES users(id)
            ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_user_profile_user_id ON user_profile(user_id);
