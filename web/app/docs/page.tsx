import type { Metadata } from 'next';
import Link from 'next/link';
import { ArrowRight } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Documentation', description: 'Hospital Management System documentation.' };

const resources = [
  ['Product areas','/modules'],
  ['Application screens','/screenshots'],
  ['Confirmed technologies','/architecture'],
  ['Database information','/database'],
  ['Security information','/security'],
  ['Application availability','/download'],
] as const;

export default function DocumentationPage() {
  return <main id="main"><PageIntro eyebrow="DOCUMENTATION" title="Hospital Management System documentation." description="Explore product capabilities, the desktop application flow, technical architecture, database design, security controls and availability."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><ul className="grid gap-x-8 sm:grid-cols-2 lg:grid-cols-3">{resources.map(([label,href])=><li key={href} className="border-t border-slate-200 py-5"><Link href={href} className="inline-flex items-center gap-2 text-sm font-medium text-slate-700 hover:text-teal-800">{label}<ArrowRight size={14}/></Link></li>)}</ul></section></main>;
}
