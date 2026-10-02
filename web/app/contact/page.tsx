import type { Metadata } from 'next';
import Link from 'next/link';
import { ArrowUpRight, Github, GraduationCap, MapPin } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';
import { repositoryUrl } from '@/lib/site-data';

export const metadata: Metadata = { title: 'Contact & repository', description: 'Project repository and academic information for the Hospital Management System project.' };

export default function ContactPage() {
  return <main id="main"><PageIntro eyebrow="PROJECT CONTACT" title="Find the project and its context." description="The repository and academic project information are available. No personal contact details are published here." />
    <section className="mx-auto grid max-w-[1120px] gap-4 px-6 py-12 sm:py-16 lg:grid-cols-2 lg:px-10">
      <a href={repositoryUrl} target="_blank" rel="noreferrer" className="group flex min-h-48 flex-col justify-between rounded-3xl bg-slate-950 p-6 text-white transition hover:-translate-y-1 hover:shadow-xl sm:p-8"><div className="flex items-center justify-between"><span className="grid size-11 place-items-center rounded-xl bg-white/10"><Github size={20}/></span><ArrowUpRight size={16} className="text-slate-400 group-hover:text-teal-200"/></div><span><small className="text-[9px] font-bold uppercase tracking-[.14em] text-teal-200">SOURCE REPOSITORY</small><b className="mt-2 block break-all text-sm sm:text-base">arpitkumar28/Hospital_Managment_System</b><span className="mt-2 block text-[10px] text-slate-400">Open repository on GitHub ↗</span></span></a>
      <article className="flex min-h-48 flex-col justify-between rounded-3xl border border-slate-200 bg-white p-6 sm:p-8"><span className="grid size-11 place-items-center rounded-xl bg-teal-50 text-teal-800"><GraduationCap size={20}/></span><span><small className="text-[9px] font-bold uppercase tracking-[.14em] text-slate-500">ACADEMIC PROJECT</small><b className="mt-2 block text-sm text-slate-950">Arya College of Engineering &amp; I.T.</b><span className="mt-2 flex items-center gap-1.5 text-[10px] text-slate-600"><MapPin size={12}/> Kukas, Jaipur · 5th Semester · 2026–27</span></span></article>
      <div className="rounded-2xl bg-sky-50 p-5 text-xs leading-6 text-sky-950 lg:col-span-2"><b>Project-specific contact information.</b> No public project email or department contact was supplied. The GitHub repository is the verified place to review the source and project activity.</div>
      <div className="lg:col-span-2"><Link href="/docs" className="inline-flex items-center gap-2 text-xs font-bold text-teal-800 hover:text-teal-950">Project documentation <ArrowUpRight size={14}/></Link></div>
    </section></main>;
}
