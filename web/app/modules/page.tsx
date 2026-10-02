import type { Metadata } from 'next';
import Link from 'next/link';
import { ArrowRight } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';
import { StatusPill } from '@/components/status-pill';
import { hospitalModules } from '@/lib/site-data';

export const metadata: Metadata = { title: 'Hospital modules', description: 'Explore patient, doctor, appointment, admission, room, bed and billing workflows in the desktop system.' };

export default function ModulesPage() {
  return <main id="main">
    <PageIntro eyebrow="HOSPITAL MODULES" title="Hospital operations, module by module." description="Explore the patient, clinical, administrative and financial workflows within the desktop system. Module descriptions distinguish available behavior from capabilities that remain under development." />
    <section className="mx-auto max-w-[1280px] px-6 py-12 sm:py-16 lg:px-10">
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">{hospitalModules.map((module, index) => { const Icon = module.icon; return <article key={module.title} className="rounded-2xl border border-slate-200 bg-white p-5 shadow-[0_5px_20px_rgba(16,44,66,.035)] sm:p-6"><div className="flex items-center justify-between gap-2"><span className="grid size-11 place-items-center rounded-xl bg-sky-50 text-teal-800"><Icon size={20} aria-hidden="true" /></span><StatusPill status={module.status}/></div><p className="mt-5 text-[9px] font-bold tracking-[.15em] text-slate-400">MODULE 0{index+1}</p><h2 className="mt-1 text-base font-bold tracking-tight text-slate-950">{module.title}</h2><p className="mt-2 min-h-10 text-xs leading-5 text-slate-600">{module.description}</p><ul className="mt-4 grid gap-2 border-t border-slate-100 pt-4">{module.items.map((item) => <li key={item} className="flex gap-2 text-[11px] leading-5 text-slate-600"><span className="mt-1.5 size-1 shrink-0 rounded-full bg-teal-500"/>{item}</li>)}</ul></article>; })}</div>
      <div className="mt-8 flex flex-wrap items-center justify-between gap-4 rounded-2xl bg-slate-950 p-6 text-white sm:p-7"><div><p className="text-[9px] font-bold uppercase tracking-[.15em] text-teal-300">DESKTOP APPLICATION</p><h2 className="mt-2 text-lg font-bold">Purpose-built for hospital operations.</h2><p className="mt-1 max-w-2xl text-xs leading-6 text-slate-300">The product application runs on Java Swing. This public website provides product information and does not connect to patient records.</p></div><Link href="/architecture" className="inline-flex shrink-0 items-center gap-2 rounded-lg bg-white px-4 py-3 text-[11px] font-bold text-slate-900">Architecture <ArrowRight size={14}/></Link></div>
    </section>
  </main>;
}
