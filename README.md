# Hospital Management System

Java 17 Swing hospital management application using FlatLaf, Maven, JDBC, and
PostgreSQL. The desktop app reads its database settings from process
environment variables, with `.env.local` as a project-root local fallback.
Values in the process environment take precedence. `.env.local` is ignored by
Git and must never be committed.

## Desktop setup and launch

From the project root, create a local configuration file and edit the password
placeholder using the credentials for your PostgreSQL role:

```sh
cp .env.example .env.local
chmod 600 .env.local
```

The supported variable names are `HOSPITAL_DB_HOST`, `HOSPITAL_DB_PORT`,
`HOSPITAL_DB_NAME`, `HOSPITAL_DB_USER`, `HOSPITAL_DB_PASSWORD`, and
`HOSPITAL_DB_SSLMODE`. The example targets `hospital_management_dev` and uses
`hms_app_dev` as the role name; replace the password placeholder with the
locally configured role password. The app also accepts values supplied by the
terminal environment, which override corresponding entries in `.env.local`.

Launch from the repository root in the VS Code terminal:

```sh
mvn exec:java
```

If the configuration file is stored elsewhere, point the JVM at it:

```sh
mvn -Dhospital.env.file=/absolute/path/to/.env.local exec:java
```

Do not paste credentials into source files, Maven arguments, command history,
or the frontend project. Missing or malformed configuration is reported before
the login screen opens.

## Database safety

The desktop application does not create, reset, or migrate the database on
startup. `database/schema.sql` applies the initial schema to a **new, empty**
database only. Do not run it against an existing development database. Inspect
an existing database and back it up before planning any migration.

PostgreSQL integration tests are disabled unless `HOSPITAL_TEST_DB_NAME` names
a dedicated database ending in `_test`; they also verify the connected
database name before inserting fixtures. Never point integration tests at
`hospital_management_dev`.

Run the automated suite with:

```sh
mvn test
```

## Project website

The separate `web/` directory contains the Next.js, TypeScript and Tailwind
project website. It documents the desktop system and its status; it does not
run the hospital application or connect to patient data.

Run it locally:

```sh
cd web
npm install
npm run dev
```

Then open <http://localhost:3000>. See [web/README.md](web/README.md) for
validation and Vercel setup.
