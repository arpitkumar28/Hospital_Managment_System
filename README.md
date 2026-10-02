# Hospital Management System

An existing Java Swing hospital management application for Arya College of
Engineering & I.T., Jaipur. The desktop application uses FlatLaf, Maven and
JDBC. Its current database connection is MySQL; a PostgreSQL migration is in
progress and has not yet been verified against the original database dump.

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
