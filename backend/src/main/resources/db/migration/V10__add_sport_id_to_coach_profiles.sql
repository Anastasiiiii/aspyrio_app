-- Додаємо колонку sport_id
ALTER TABLE coach_profiles
ADD COLUMN sport_id INTEGER;

-- Встановлюємо для існуючих записів перший спорт (якщо він існує)
UPDATE coach_profiles
SET sport_id = (SELECT id FROM sports ORDER BY id LIMIT 1)
WHERE sport_id IS NULL
  AND EXISTS (SELECT 1 FROM sports LIMIT 1);

-- Додаємо FOREIGN KEY constraint
ALTER TABLE coach_profiles
ADD CONSTRAINT fk_coach_profile_sport
    FOREIGN KEY (sport_id) REFERENCES sports(id)
    ON DELETE SET NULL;

-- Додаємо індекс для швидшого пошуку
CREATE INDEX idx_coach_profiles_sport_id ON coach_profiles(sport_id);

