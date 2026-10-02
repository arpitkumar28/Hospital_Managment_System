import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Database', description: 'Database development information for the Hospital Management System.' };

export default function DatabasePage() {
  return <main id="main"><PageIntro eyebrow="DATABASE" title="Database information will be published as implemented." description="PostgreSQL is not presented as connected or verified in the current application."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><p className="max-w-2xl text-sm leading-7 text-slate-600">Database connectivity and operations will be documented after the Java application has a confirmed PostgreSQL integration.</p></section></main>;
}
