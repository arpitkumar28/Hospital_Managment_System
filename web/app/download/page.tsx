import type { Metadata } from 'next';
import Link from 'next/link';
import { ArrowRight, Check, Clock3, Database, Monitor, PackageOpen } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';
import { StatusPill } from '@/components/status-pill';

export const metadata: Metadata = { title: 'Desktop application', description: 'The desktop release is not packaged yet. Review system requirements and project status.' };

const requirements = [
  ['Java runtime', 'Java 17 or later is the project target.'],
  ['Operating system', 'Windows, macOS or Linux with a compatible Java desktop environment; each platform still needs a verified release build.'],
  ['Database', 'Current source expects local MySQL. PostgreSQL/Supabase is the migration target and is not yet connected.'],
  ['Configuration', 'Database connection values need to move fully to environment variables before team distribution.'],
];

export default function DownloadPage() {
  return <main id="main"><PageIntro eyebrow="DESKTOP APPLICATION" title="A desktop release is coming soon." description="The Hospital Management System is a Java Swing desktop application. No tested, packaged installer or release archive exists yet, so there is no download button." />
    <section className="mx-auto max-w-[1120px] px-6 py-12 sm:py-16 lg:px-10"><div className="grid gap-5 lg:grid-cols-[.85fr_1.15fr]">
      <article className="rounded-3xl bg-slate-950 p-6 text-white sm:p-8"><span className="grid size-12 place-items-center rounded-2xl bg-white/10 text-teal-200"><PackageOpen size={22}/></span><StatusPill status="Not verified"/><h2 className="mt-5 text-2xl font-bold tracking-tight">Desktop release coming soon</h2><p className="mt-3 text-xs leading-6 text-slate-300">The app needs a successful Java build, secure PostgreSQL setup, authentication migration and packaged runtime before it is ready for distribution.</p><div className="mt-6 flex items-center gap-2 rounded-xl border border-white/10 bg-white/5 p-3 text-[10px] text-slate-200"><Clock3 size={14} className="text-amber-300"/>No release artifact is currently available.</div><Link href="/docs" className="mt-6 inline-flex items-center gap-2 text-[11px] font-bold text-teal-200">Follow project documentation <ArrowRight size={14}/></Link></article>
      <div className="rounded-3xl border border-slate-200 bg-white p-6 sm:p-8"><div className="flex items-center gap-3"><span className="grid size-10 place-items-center rounded-xl bg-sky-50 text-sky-800"><Monitor size={18}/></span><div><h2 className="text-sm font-bold text-slate-950">System requirements</h2><p className="text-[9px] text-slate-500">Expected requirements; platform verification remains open.</p></div></div><div className="mt-5 divide-y divide-slate-100">{requirements.map(([name,detail])=><div key={name} className="py-4"><div className="flex gap-2"><Check size={13} className="mt-0.5 text-teal-700"/><b className="text-[10px] text-slate-900">{name}</b></div><p className="ml-5 mt-1 text-[10px] leading-5 text-slate-600">{detail}</p></div>)}</div></div>
    </div><p className="mt-5 flex items-start gap-2 rounded-xl bg-amber-50 p-4 text-[10px] leading-5 text-amber-950"><Database size={14} className="mt-0.5 shrink-0"/>Do not connect an unverified build to production data. Use a separate development database and restricted credentials during testing.</p></section>
  </main>;
}
