import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Application availability', description: 'Availability information for the Hospital Management System application.' };

export default function DownloadPage() {
  return <main id="main"><PageIntro eyebrow="APPLICATION AVAILABILITY" title="Desktop application distribution." description="The Hospital Management System is a Java Swing desktop application. This page will link to a downloadable package when a release artifact is published."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><p className="max-w-2xl text-sm leading-7 text-slate-600">No downloadable installer or release package is currently published. The public website provides product information and repository access.</p></section></main>;
}
