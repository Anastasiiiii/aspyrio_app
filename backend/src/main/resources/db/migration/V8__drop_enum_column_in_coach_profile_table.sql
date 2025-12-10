ALTER TABLE coach_profiles
    ALTER COLUMN training_format DROP DEFAULT;

ALTER TABLE coach_profiles
    ALTER COLUMN training_format TYPE VARCHAR(50)
        USING training_format::text;

DROP TYPE IF EXISTS training_type;
