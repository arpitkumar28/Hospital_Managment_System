# PostgreSQL migration foundation

`migrations/001_initial_schema.sql` is a PostgreSQL starting schema for a **new,
empty** target database. `migrations/002_authentication_foundation.sql` adds the
authentication indexes and tables idempotently and can be applied again safely.
It preserves the named business tables, retains
`invoices`, uses identity columns and `NUMERIC(12,2)` for monetary fields, and
uses restrictive foreign keys so accidental deletes do not cascade through
clinical or billing records. `schema.sql` is a `psql` entry point that applies
both migrations.

The MySQL dump referenced by the project instructions is not present in this
checkout or its nearby project directories. The schema facts supplied in the
instructions omit exact lengths, nullability, indexes, foreign-key delete
rules, appointment/doctor status domains, and the full `invoices` definition.
Accordingly, this SQL is a reviewable foundation, **not an approved or verified
production migration**. Compare every column and constraint against the actual
dump and validate source data before using it.

## Safety

- The migrations have no `DROP`, `TRUNCATE`, or overwrite operation.
- Run it only against a separately created, empty PostgreSQL development
  database after a verified backup of the original MySQL database exists.
- Keep the MySQL database unchanged as the rollback/reference source.
- `users` contains `password_hash`, never a legacy `password` field. A migrated
  identity must remain `PENDING` until a new password is set through a secure
  BCrypt provisioning flow. Never copy old plaintext passwords.
- Local development validation was performed only against the separate
  `hospital_management_dev` database. No production or Supabase database was
  contacted, and no credentials are included in tracked files.

## Local macOS development database

The local development target is `hospital_management_dev` on `localhost:5432`,
owned by the non-superuser application role `hms_app_dev`. Its password is kept
in the ignored, owner-readable `.env.local`; never commit that file. The Java
application reads process environment variables and does not load dotenv files
itself. Copy `.env.example` to `.env.local`, set a private role password, then
export the values before launching the app:

```sh
set -a
source .env.local
set +a
```

On macOS with Homebrew PostgreSQL 15, start an initialized cluster temporarily
with:

```sh
pg_ctl -D "$(brew --prefix)/var/postgresql@15" -l /tmp/hms-postgresql-dev.log start
```

Stop it with `pg_ctl -D "$(brew --prefix)/var/postgresql@15" stop`. Inspect the
cluster and database list before starting an existing data directory. Do not
run the initial schema against a populated or reference database.

## Applying to a new empty development database

Create the database and least-privilege application role manually using your
database administrator's approved process, then apply the schema using the
application role's explicit connection settings:

```sh
psql -h localhost -p 5432 -U hms_app_dev -d hospital_management_dev \
  -v ON_ERROR_STOP=1 -f database/schema.sql
```

Run this only against a separately created, empty development database; the base
migration is intentionally one-time DDL and will fail if its business tables
already exist. The authentication migration is additive and repeatable.

## Repairing an existing development database

Do not run `database/schema.sql` against an existing database: it starts with
the one-time initial schema. The checked-in files are not tracked in a migration
history table, so determine the applied state from PostgreSQL's catalog. Connect
to the confirmed development database with `.env.local` loaded, verify the
database name, inspect the relevant objects, and take a backup first:

```sh
set -a
source .env.local
set +a
psql -h "$HOSPITAL_DB_HOST" -p "$HOSPITAL_DB_PORT" -U "$HOSPITAL_DB_USER" \
  -d "$HOSPITAL_DB_NAME" -v ON_ERROR_STOP=1 \
  -c 'SELECT current_database();' \
  -c "SELECT to_regclass('public.patients') AS patients, to_regclass('public.patient_credentials') AS patient_credentials, to_regclass('public.audit_logs') AS audit_logs;" \
  -c "SELECT table_name FROM information_schema.tables WHERE table_schema='public' AND table_name IN ('patients','patient_credentials','audit_logs') ORDER BY table_name;"
```

After confirming the target is the intended development database and a backup
exists, apply only the additive authentication migrations that are missing. In
the known case where the existing application schema already has `patients`,
but the patient credentials table is absent, run:

```sh
psql -h "$HOSPITAL_DB_HOST" -p "$HOSPITAL_DB_PORT" -U "$HOSPITAL_DB_USER" \
  -d "$HOSPITAL_DB_NAME" -v ON_ERROR_STOP=1 \
  -f database/migrations/002_authentication_foundation.sql \
  -f database/migrations/003_patient_authentication.sql
```

Both files use additive/idempotent DDL. Migration 003 needs the existing
`patients` table and adds no patient rows. Do not run migration 001 on an
existing database. If either authentication migration encounters schema drift,
stop and inspect the error rather than editing/dropping existing objects. These
commands do not create a migration-history record; capture which numbered files
were applied in the development environment's deployment notes. Startup never
runs migrations.

## Provisioning a development patient login

Patient credential enrollment is not exposed in the UI. First create the
patient through the authorized staff Patients screen, then use a trusted
development-only Java helper or local provisioning utility that calls
`PasswordUtil.hashPassword(char[])` and inserts the resulting hash into
`patient_credentials` for that existing `patient_id`, with a unique username,
matching email, and `status='ACTIVE'`. The `patient_id` must be selected from
the database; never guess or hardcode one. Keep the helper local/untracked and
remove it after use. Do not insert plaintext passwords or hand-written hashes.
The table's foreign key and unique indexes enforce the patient relationship and
unique case-insensitive login identifiers. Patient accounts remain separate
from `users` staff identities.
`seed.sql` contains no demo account or password. To create the first
administrator, supply `HOSPITAL_INITIAL_ADMIN_USERNAME`,
`HOSPITAL_INITIAL_ADMIN_EMAIL`, and `HOSPITAL_INITIAL_ADMIN_PASSWORD` in the
process environment before starting the Java application. Startup creates an
administrator only when none exists, using a PostgreSQL advisory lock and a
BCrypt hash. Unset those one-time variables after setup. The password must
contain at least 8 characters including upper/lowercase, a number, and a
symbol. Self-registration creates only `RECEPTIONIST` accounts in `PENDING`
status; it never grants administrator privileges.

Database connection settings use `HOSPITAL_DB_HOST`, `HOSPITAL_DB_PORT`,
`HOSPITAL_DB_NAME`, `HOSPITAL_DB_USER`, `HOSPITAL_DB_PASSWORD`, and
`HOSPITAL_DB_SSLMODE`. Database username and password are required; defaults
are `localhost`, `5432`, `hospital_management`, and PostgreSQL SSL mode `prefer`.

## Required before data migration

1. Obtain the authoritative MySQL dump without exposing it in Git.
2. Verify a fresh backup and record source row counts.
3. Confirm every table's complete columns, data types, defaults, indexes,
   constraints, and delete behavior against this schema.
4. Resolve legacy users by carrying username/role only; require new passwords.
5. Migrate into a separate PostgreSQL database, validate row counts, IDs,
   relationships, identity sequences, monetary totals, and bed/admission state.
6. Keep the source untouched until application acceptance and rollback planning
   are complete.
