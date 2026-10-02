import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Database', description: 'PostgreSQL database configuration and verification status for the Hospital Management System.' };

export default function DatabasePage() {
  return <main id="main"><PageIntro eyebrow="DATABASE" title="PostgreSQL is the HMS database target." description="The Java application has environment-based PostgreSQL JDBC configuration and HikariCP pooling. The live connection has not yet been verified."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><div className="max-w-3xl rounded-xl border border-amber-200 bg-amber-50 p-5"><h2 className="text-sm font-semibold text-amber-950">Verification status</h2><p className="mt-2 text-sm leading-6 text-amber-900">A draft PostgreSQL schema is present in the repository. No PostgreSQL server was available for connection or migration testing. Supabase PostgreSQL is a supported target option, but no Supabase instance is connected.</p></div></section></main>;
}
