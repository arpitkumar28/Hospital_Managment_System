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
