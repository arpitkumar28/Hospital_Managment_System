# Hospital Management System project website

The project website is a separate Next.js App Router application inside `web/`.
It presents and documents the Java Swing desktop project. It does not replace
the desktop hospital application, connect to PostgreSQL, or process patient
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

The project uses Next.js, TypeScript, Tailwind CSS and Lucide icons. Pages are
rendered with the Next.js App Router; only the responsive navigation requires
client-side state. No `.env.example` is needed because the public website has no
environment variables or private server-side configuration.

## Routes

`/`, `/features`, `/modules`, `/architecture`, `/screenshots`, `/security`,
`/database`, `/docs`, `/team`, `/download`, and `/contact`.

Implementation status is explicit across the site. In particular, the existing
Java baseline uses local MySQL; PostgreSQL/Supabase is the migration target, not
a verified connection. No screenshot, team member, release file, security
certification, or credential is invented.

## Vercel

Import the repository in Vercel and set **Root Directory** to `web`. Keep the
framework preset as Next.js. Vercel can use the included `npm run build` and
`npm run start` scripts with no custom build output directory. No Vercel
environment variables are required. Do not add database credentials to the
website project.
