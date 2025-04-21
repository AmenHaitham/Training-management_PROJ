-- Create database if not exists
SELECT 'CREATE DATABASE training'
WHERE NOT EXISTS (SELECT FROM pg_database WHERE datname = 'training');

-- Connect to the database
\c training;

-- Execute schema.sql
\i schema.sql;

-- Execute initial_data.sql
\i initial_data.sql; 