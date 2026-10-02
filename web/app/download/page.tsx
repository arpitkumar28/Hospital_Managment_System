import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Application availability', description: 'Availability information for the Hospital Management System application.' };

export default function DownloadPage() {
  return <main id="main"><PageIntro eyebrow="APPLICATION AVAILABILITY" title="The desktop application is under development." description="A packaged release is not available. This page will be updated when a verified application build is ready."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><p className="max-w-2xl text-sm leading-7 text-slate-600">There is no download link or installer to provide at this time.</p></section></main>;
}
