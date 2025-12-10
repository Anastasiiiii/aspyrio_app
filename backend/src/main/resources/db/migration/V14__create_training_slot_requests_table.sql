CREATE TABLE IF NOT EXISTS training_slot_requests (
    id BIGSERIAL PRIMARY KEY,
    group_training_slot_id BIGINT NOT NULL REFERENCES group_training_slots(id) ON DELETE CASCADE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    coach_response_at TIMESTAMP,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_training_slot_requests_slot_id ON training_slot_requests(group_training_slot_id);
CREATE INDEX IF NOT EXISTS idx_training_slot_requests_status ON training_slot_requests(status);

