# Hospital Management System project website

The project website is a separate Next.js App Router application inside `web/`.
It presents and documents the Java desktop Hospital Management System. It does
not replace the HMS application, connect to its database, or process patient
data.

## Requirements

- Node.js 20.9 or later
- npm

## Local development

From this directory:

```sh
npm install
npm run dev
```

Open <http://localhost:3000>.

## Validation

```sh
npm run lint
npm run build
npm run start
```

The website uses Next.js, TypeScript, Tailwind CSS and Lucide icons. Pages are
rendered with the Next.js App Router; only the responsive navigation requires
client-side state. Its sitemap uses hosting-provided public URL variables.
Never configure HMS database credentials in the website project.

The HMS itself is Java 17+, Swing, FlatLaf, Maven, a service layer, a DAO layer,
JDBC, HikariCP and PostgreSQL. PostgreSQL connection settings and authentication
code exist, but a live database connection has not yet been verified. Supabase
PostgreSQL is an option, not a connected service.

## Routes

`/`, `/product`, `/features`, `/modules`, `/architecture`, `/screenshots`,
`/security`, `/database`, `/docs`, `/team`, `/download`, and `/contact`.

Implementation status is explicit across the site. In particular, the existing
PostgreSQL connectivity and the draft schema still need live database
verification. No application screenshots, team members, release files, security
certifications, or credentials are invented.

## Vercel

Import the repository in Vercel and set **Root Directory** to `web`. Keep the
framework preset as Next.js. Vercel can use the included `npm run build` and
`npm run start` scripts with no custom build output directory. No Vercel
environment variables are required. Do not add database credentials to the
website project.
