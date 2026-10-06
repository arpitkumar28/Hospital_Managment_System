import type { Metadata } from 'next';
import { ArrowRight, Building2, KeyRound, LayoutDashboard, UserRound } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Application Experience', description: 'The welcome, portal selection, sign-in and workspace flow of the Java desktop HMS.' };

const flow = [
  { icon: Building2, title: 'Welcome Screen', detail: 'Choose the appropriate portal to continue.' },
  { icon: UserRound, title: 'Staff Portal / Patient Portal', detail: 'Staff choose a role; patients continue to patient sign-in.' },
  { icon: KeyRound, title: 'Secure Login', detail: 'Authenticate through the separate staff or patient flow.' },
  { icon: LayoutDashboard, title: 'Role-specific workspace', detail: 'Open the staff dashboard or patient dashboard after authentication.' },
];

export default function ApplicationExperiencePage() {
  return <main id="main"><PageIntro eyebrow="APPLICATION EXPERIENCE" title="A clear path into the desktop workspace." description="The Java Swing application starts at a welcome screen and routes staff and patients through their appropriate sign-in experiences."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><div className="rounded-2xl border border-slate-200 bg-white p-6 sm:p-9"><p className="text-xs font-semibold uppercase tracking-wider text-teal-800">Desktop application flow</p><ol className="mt-7 grid gap-4 md:grid-cols-4">{flow.map(({icon:Icon,title,detail},index)=><li key={title} className="relative rounded-xl border border-slate-200 bg-slate-50/70 p-5"><span className="grid size-10 place-items-center rounded-lg bg-teal-50 text-teal-800"><Icon size={19}/></span><p className="mt-4 text-sm font-semibold text-slate-900">{title}</p><p className="mt-2 text-xs leading-5 text-slate-600">{detail}</p>{index<flow.length-1&&<ArrowRight className="absolute -right-3 top-1/2 z-10 hidden -translate-y-1/2 text-teal-700 md:block" size={18}/>}</li>)}</ol><p className="mt-6 text-xs leading-5 text-slate-500">This page describes the implemented interface flow. No application screenshot is being represented here.</p></div></section></main>;
}
