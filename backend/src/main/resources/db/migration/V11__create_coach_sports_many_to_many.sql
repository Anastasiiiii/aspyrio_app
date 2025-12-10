CREATE TABLE coach_sports (
    coach_profile_id BIGINT NOT NULL REFERENCES coach_profiles(id) ON DELETE CASCADE,
    sport_id BIGINT NOT NULL REFERENCES sports(id) ON DELETE CASCADE,
    PRIMARY KEY (coach_profile_id, sport_id)
);

INSERT INTO coach_sports (coach_profile_id, sport_id)
SELECT id, sport_id
FROM coach_profiles
WHERE sport_id IS NOT NULL;

DROP INDEX IF EXISTS idx_coach_profiles_sport_id;

ALTER TABLE coach_profiles
DROP CONSTRAINT IF EXISTS fk_coach_profile_sport;

ALTER TABLE coach_profiles
DROP COLUMN IF EXISTS sport_id;

CREATE INDEX idx_coach_sports_coach_profile_id ON coach_sports(coach_profile_id);
CREATE INDEX idx_coach_sports_sport_id ON coach_sports(sport_id);


