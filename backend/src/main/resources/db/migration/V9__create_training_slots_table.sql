CREATE TABLE training_slots (
    id BIGSERIAL PRIMARY KEY,
    coach_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sport_id BIGINT NOT NULL REFERENCES sports(id) ON DELETE CASCADE,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT check_end_after_start CHECK (end_time > start_time)
);

CREATE INDEX idx_training_slots_coach_id ON training_slots(coach_id);
CREATE INDEX idx_training_slots_sport_id ON training_slots(sport_id);
CREATE INDEX idx_training_slots_start_time ON training_slots(start_time);


