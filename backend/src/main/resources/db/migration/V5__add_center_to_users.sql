ALTER TABLE users
ADD COLUMN center_id BIGINT;

ALTER TABLE users
ADD CONSTRAINT fk_users_center
FOREIGN KEY (center_id)
REFERENCES fitness_center(id)
ON DELETE SET NULL;
