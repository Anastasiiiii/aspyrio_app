CREATE TABLE IF NOT EXISTS studios (
    id BIGSERIAL PRIMARY KEY,
    fitness_center_id BIGINT NOT NULL REFERENCES fitness_center(id) ON DELETE CASCADE,
    name VARCHAR(100) NOT NULL,
    capacity INTEGER NOT NULL,
    description TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_studios_fitness_center_id ON studios(fitness_center_id);

