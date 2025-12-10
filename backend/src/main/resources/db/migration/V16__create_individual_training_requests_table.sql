CREATE TABLE IF NOT EXISTS individual_training_requests (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    coach_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sport_id BIGINT NOT NULL REFERENCES sports(id) ON DELETE CASCADE,
    requested_start_time TIMESTAMP NOT NULL,
    requested_end_time TIMESTAMP NOT NULL,
    training_type VARCHAR(20) NOT NULL, -- 'ONLINE', 'OFFLINE', 'BOTH_ONLINE_OFFLINE'
    message TEXT, -- Optional message from user
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, APPROVED, REJECTED
    coach_response_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT check_end_after_start CHECK (requested_end_time > requested_start_time)
);

CREATE INDEX IF NOT EXISTS idx_individual_training_requests_user_id ON individual_training_requests(user_id);
CREATE INDEX IF NOT EXISTS idx_individual_training_requests_coach_id ON individual_training_requests(coach_id);
CREATE INDEX IF NOT EXISTS idx_individual_training_requests_status ON individual_training_requests(status);
CREATE INDEX IF NOT EXISTS idx_individual_training_requests_created_at ON individual_training_requests(created_at);


