DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name='users' AND column_name='username'
    ) THEN
ALTER TABLE users ADD COLUMN username VARCHAR(50) NOT NULL UNIQUE;
END IF;
END
$$;
