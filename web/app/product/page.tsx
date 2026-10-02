import type { Metadata } from 'next';
import Link from 'next/link';
import { ArrowRight, BedDouble, CalendarDays, ClipboardList, CreditCard, Stethoscope, Users, Wallet } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';
import { hospitalModules } from '@/lib/site-data';

export const metadata: Metadata = { title: 'Product', description: 'Explore the Hospital Management System desktop platform and its connected hospital operations workflows.' };

const icons = [Users, Stethoscope, CalendarDays, ClipboardList, BedDouble, CreditCard, Wallet];

export default function ProductPage() {
  return <main id="main"><PageIntro eyebrow="THE PLATFORM" title="One system for essential hospital operations." description="A desktop application that brings patient, clinical, administrative and financial workflows into a connected operational workspace." />
    <section className="mx-auto max-w-[1280px] px-5 py-12 sm:px-8 sm:py-16 lg:px-12"><div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">{hospitalModules.slice(0,7).map((module,index)=>{const Icon=icons[index]??Users;return <article key={module.title} className="border-t border-slate-200 py-5"><span className="grid size-10 place-items-center rounded-xl bg-sky-50 text-teal-800"><Icon size={19}/></span><h2 className="mt-4 text-sm font-bold text-slate-950">{module.title}</h2><p className="mt-2 text-xs leading-6 text-slate-600">{module.description}</p></article>;})}</div><div className="mt-8 flex flex-wrap gap-3"><Link href="/features" className="inline-flex items-center gap-2 rounded-lg bg-teal-700 px-4 py-3 text-xs font-bold text-white">Explore features <ArrowRight size={14}/></Link><Link href="/architecture" className="inline-flex items-center gap-2 rounded-lg border border-slate-200 px-4 py-3 text-xs font-semibold text-slate-700">System architecture <ArrowRight size={14}/></Link></div></section>
  </main>;
}
