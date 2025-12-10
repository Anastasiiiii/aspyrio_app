CREATE TABLE IF NOT EXISTS group_training_bookings (
    id BIGSERIAL PRIMARY KEY,
    group_training_slot_id BIGINT NOT NULL REFERENCES group_training_slots(id) ON DELETE CASCADE,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    booking_status VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW(),
    UNIQUE(group_training_slot_id, user_id)
);

CREATE INDEX IF NOT EXISTS idx_group_training_bookings_slot_id ON group_training_bookings(group_training_slot_id);
CREATE INDEX IF NOT EXISTS idx_group_training_bookings_user_id ON group_training_bookings(user_id);
CREATE INDEX IF NOT EXISTS idx_group_training_bookings_status ON group_training_bookings(booking_status);
CREATE INDEX IF NOT EXISTS idx_group_training_bookings_user_status ON group_training_bookings(user_id, booking_status);

