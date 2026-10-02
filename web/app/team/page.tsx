import type { Metadata } from 'next';
import { GraduationCap, MapPin, Users } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';
import { projectFacts } from '@/lib/site-data';

export const metadata: Metadata = { title: 'Project team', description: 'Academic project details for the Hospital Management System at Arya College of Engineering & I.T., Kukas, Jaipur.' };

export default function TeamPage() {
  return <main id="main"><PageIntro eyebrow="PROJECT & TEAM" title="Built as a learning project. Designed to grow." description="Hospital Management System · Arya College of Engineering & I.T. · Kukas, Jaipur · 5th Semester · Academic year 2026–27." />
    <section className="mx-auto max-w-[1120px] px-6 py-12 sm:py-16 lg:px-10"><article className="overflow-hidden rounded-3xl border border-slate-200 bg-white shadow-sm"><div className="mesh-bg border-b border-slate-200 p-6 sm:p-9"><span className="grid size-12 place-items-center rounded-2xl bg-white text-teal-800 shadow-sm ring-1 ring-slate-200"><GraduationCap size={22}/></span><h2 className="mt-5 text-2xl font-bold tracking-tight text-slate-950">Hospital Management System</h2><p className="mt-2 text-xs leading-6 text-slate-600">Java Project Based Learning · 5th Semester · 2026–27</p></div><dl className="grid divide-y divide-slate-100 sm:grid-cols-2 sm:divide-y-0">{projectFacts.map(([label,value],index)=><div key={label} className={`flex items-start gap-3 p-5 ${index>1?'sm:border-t sm:border-slate-100':''}`}><span className="mt-0.5 grid size-7 place-items-center rounded-lg bg-slate-50 text-teal-800">{index===2?<MapPin size={14}/>:index===1?<GraduationCap size={14}/>:<Users size={14}/>}</span><div><dt className="text-[8px] font-bold uppercase tracking-[.14em] text-slate-400">{label}</dt><dd className="mt-1 text-xs font-semibold text-slate-800">{value}</dd></div></div>)}</dl></article>
      <div className="mt-5 rounded-2xl border border-dashed border-slate-300 bg-slate-50 p-6"><div className="flex items-start gap-3"><Users size={17} className="mt-0.5 text-slate-500"/><div><h2 className="text-sm font-bold text-slate-900">Team members &amp; mentor</h2><p className="mt-2 max-w-3xl text-xs leading-6 text-slate-600">Names, roles and mentor details were not provided. Add verified information after the project team confirms what should be published. No people or credentials have been invented for this page.</p></div></div></div>
    </section></main>;
}
