CREATE TABLE IF NOT EXISTS passes (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token VARCHAR(255) NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_passes_user_id ON passes(user_id);
CREATE INDEX IF NOT EXISTS idx_passes_token ON passes(token);
CREATE INDEX IF NOT EXISTS idx_passes_expires_at ON passes(expires_at);
CREATE INDEX IF NOT EXISTS idx_passes_used ON passes(used);
CREATE INDEX IF NOT EXISTS idx_passes_user_expires ON passes(user_id, expires_at, used);


