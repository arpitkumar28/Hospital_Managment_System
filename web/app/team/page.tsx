import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'About', description: 'About the Hospital Management System desktop application.' };

export default function AboutPage() {
  return <main id="main"><PageIntro eyebrow="ABOUT" title="Hospital Management System." description="A Java desktop application for hospital operations."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><p className="max-w-2xl text-sm leading-7 text-slate-600">Application capabilities and technical information will be published here as they are implemented and verified.</p></section></main>;
}
