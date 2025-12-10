CREATE TABLE sports (
                        id SERIAL PRIMARY KEY,
                        name VARCHAR(100) NOT NULL UNIQUE,
                        created_at TIMESTAMP DEFAULT NOW(),
                        updated_at TIMESTAMP DEFAULT NOW()
);

INSERT INTO sports (name) VALUES
                              ('Fitness'),
                              ('Boxing'),
                              ('Yoga'),
                              ('Crossfit'),
                              ('Stretching'),
                              ('Pilates'),
                              ('Gymnastics');
