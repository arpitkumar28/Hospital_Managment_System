import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Architecture', description: 'The Java desktop architecture of the Hospital Management System.' };

const applicationStack = ['Java 17+', 'Java Swing', 'FlatLaf', 'Maven', 'Service layer', 'DAO layer', 'JDBC', 'HikariCP', 'PostgreSQL'];

export default function ArchitecturePage() {
  return <main id="main"><PageIntro eyebrow="APPLICATION ARCHITECTURE" title="A Java desktop hospital management system." description="The HMS is built as a Java Swing desktop application with distinct service, DAO and JDBC data-access layers."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><h2 className="text-base font-semibold text-slate-900">Application stack</h2><ul className="mt-4 grid gap-x-8 sm:grid-cols-2 lg:grid-cols-3">{applicationStack.map((item)=><li key={item} className="border-t border-slate-200 py-5 text-sm font-medium text-slate-700">{item}</li>)}</ul><div className="mt-4 rounded-xl border border-amber-200 bg-amber-50 p-5"><h2 className="text-sm font-semibold text-amber-950">Database status</h2><p className="mt-2 max-w-3xl text-sm leading-6 text-amber-900">PostgreSQL connection configuration and pooling are implemented. A live database connection and the draft schema have not yet been verified against a running PostgreSQL instance.</p></div><p className="mt-6 max-w-3xl text-sm leading-6 text-slate-600">The public product website is a separate project for product information. Its web framework and hosting are not part of the HMS application architecture.</p></section></main>;
}
