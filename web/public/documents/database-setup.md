# PostgreSQL migration foundation

`migrations/001_initial_schema.sql` is a PostgreSQL starting schema for a **new,
empty** target database. It preserves the named business tables, retains
`invoices`, uses identity columns and `NUMERIC(12,2)` for monetary fields, and
uses restrictive foreign keys so accidental deletes do not cascade through
clinical or billing records. `schema.sql` is a `psql` entry point for applying
that migration.

The MySQL dump referenced by the project instructions is not present in this
checkout or its nearby project directories. The schema facts supplied in the
instructions omit exact lengths, nullability, indexes, foreign-key delete
rules, appointment/doctor status domains, and the full `invoices` definition.
Accordingly, this SQL is a reviewable foundation, **not an approved or verified
production migration**. Compare every column and constraint against the actual
dump and validate source data before using it.

## Safety

- The migration has no `DROP`, `TRUNCATE`, or overwrite operation.
- Run it only against a separately created, empty PostgreSQL development
  database after a verified backup of the original MySQL database exists.
- Keep the MySQL database unchanged as the rollback/reference source.
- `users` contains `password_hash`, never a legacy `password` field. A migrated
  identity must remain `PENDING` until a new password is set through a secure
  BCrypt provisioning flow. Never copy old plaintext passwords.
- No live database was contacted and no credentials are included here.

## Applying to an empty local development database

Create the database and least-privilege application role manually using your
database administrator's approved process, then apply:

```sh
psql -v ON_ERROR_STOP=1 -d hospital_management -f database/schema.sql
```

Do not run this against an existing database. The migration is intentionally
one-time DDL and will fail if its tables already exist. `seed.sql` contains no
demo account or password; the initial-admin workflow is not implemented yet.

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
