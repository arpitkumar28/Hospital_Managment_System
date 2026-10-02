import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Security', description: 'Security development information for the Hospital Management System.' };

export default function SecurityPage() {
  return <main id="main"><PageIntro eyebrow="SECURITY" title="Security capabilities are under development." description="Authentication and access controls will be described here as they are implemented and verified."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><p className="max-w-2xl text-sm leading-7 text-slate-600">Do not use the current application with real patient data until authentication, password protection and access control have been implemented and reviewed.</p></section></main>;
}
