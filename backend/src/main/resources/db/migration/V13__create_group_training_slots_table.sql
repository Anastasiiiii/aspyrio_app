CREATE TABLE IF NOT EXISTS group_training_slots (
    id BIGSERIAL PRIMARY KEY,
    coach_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    sport_id BIGINT NOT NULL REFERENCES sports(id) ON DELETE CASCADE,
    studio_id BIGINT REFERENCES studios(id) ON DELETE CASCADE,
    training_category VARCHAR(20) NOT NULL DEFAULT 'GROUP',
    training_type VARCHAR(20) NOT NULL,
    created_by_id BIGINT NOT NULL REFERENCES users(id),
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,
    max_participants INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT check_end_after_start CHECK (end_time > start_time),
    CONSTRAINT check_max_participants CHECK (max_participants > 0),
    CONSTRAINT check_studio_for_group CHECK (
        (training_category = 'GROUP' AND studio_id IS NOT NULL) OR
        (training_category = 'INDIVIDUAL' AND studio_id IS NULL)
    )
);

CREATE INDEX IF NOT EXISTS idx_group_training_slots_coach_id ON group_training_slots(coach_id);
CREATE INDEX IF NOT EXISTS idx_group_training_slots_status ON group_training_slots(status);
CREATE INDEX IF NOT EXISTS idx_group_training_slots_start_time ON group_training_slots(start_time);
CREATE INDEX IF NOT EXISTS idx_group_training_slots_training_type ON group_training_slots(training_type);
CREATE INDEX IF NOT EXISTS idx_group_training_slots_studio_time ON group_training_slots(studio_id, start_time, status) WHERE studio_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_group_training_slots_coach_category_time ON group_training_slots(coach_id, training_category, start_time);

