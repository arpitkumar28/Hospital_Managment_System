import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Architecture', description: 'Confirmed technologies in the Hospital Management System desktop application.' };

const confirmed = ['Java', 'Java Swing', 'FlatLaf', 'Maven', 'JDBC'];

export default function ArchitecturePage() {
  return <main id="main"><PageIntro eyebrow="APPLICATION TECHNOLOGY" title="A Java desktop application." description="These technologies are confirmed in the current application setup. Database and security work will be documented when implemented and verified."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><ul className="grid gap-x-8 sm:grid-cols-2 lg:grid-cols-3">{confirmed.map((item)=><li key={item} className="border-t border-slate-200 py-5 text-sm font-medium text-slate-700">{item}</li>)}</ul><p className="mt-8 max-w-2xl text-xs leading-6 text-slate-500">PostgreSQL connectivity, BCrypt password hashing and role-based access controls are not presented as completed features.</p></section></main>;
}
