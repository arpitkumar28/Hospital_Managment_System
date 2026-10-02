import type { Metadata } from 'next';
import { ArrowUpRight, Github } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';
import { repositoryUrl } from '@/lib/site-data';

export const metadata: Metadata = { title: 'Contact', description: 'Contact and source information for the Hospital Management System.' };

export default function ContactPage() {
  return <main id="main"><PageIntro eyebrow="CONTACT" title="Contact the Hospital Management System team." description="No contact form or submission service is configured. The repository is available for product questions and development discussion."/><section className="mx-auto max-w-[1120px] px-6 py-12 lg:px-10"><a href={repositoryUrl} target="_blank" rel="noreferrer" className="inline-flex items-center gap-2 rounded-lg bg-slate-950 px-4 py-3 text-xs font-bold text-white">GitHub <Github size={15}/><ArrowUpRight size={14}/></a></section></main>;
}
