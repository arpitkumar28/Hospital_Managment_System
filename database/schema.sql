-- Canonical schema entry point. Run with psql from the project root:
--   psql -v ON_ERROR_STOP=1 -d hospital_management -f database/schema.sql
-- This applies the initial schema to a NEW, EMPTY database only.
\ir migrations/001_initial_schema.sql
\ir migrations/002_authentication_foundation.sql
