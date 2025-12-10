-- Add user_id column for individual training slots
ALTER TABLE group_training_slots 
ADD COLUMN IF NOT EXISTS user_id BIGINT REFERENCES users(id) ON DELETE CASCADE;

-- Add index for user_id
CREATE INDEX IF NOT EXISTS idx_group_training_slots_user_id ON group_training_slots(user_id) WHERE user_id IS NOT NULL;

-- Update constraint to allow user_id for individual trainings
ALTER TABLE group_training_slots 
DROP CONSTRAINT IF EXISTS check_studio_for_group;

ALTER TABLE group_training_slots 
ADD CONSTRAINT check_studio_for_group CHECK (
    (training_category = 'GROUP' AND studio_id IS NOT NULL AND user_id IS NULL) OR
    (training_category = 'INDIVIDUAL' AND studio_id IS NULL)
);


