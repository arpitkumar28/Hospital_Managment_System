import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Product', description: 'Hospital Management System application overview.' };

export default function ProductPage() {
  return <main id="main"><PageIntro eyebrow="PRODUCT" title="A desktop workspace for hospital operations." description="The Hospital Management System brings core clinical and administrative workflows into one Java Swing application backed by PostgreSQL."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><div className="grid gap-8 md:grid-cols-2"><article className="rounded-xl border border-slate-200 bg-white p-6"><h2 className="text-base font-semibold text-slate-900">Operational workflows</h2><p className="mt-3 text-sm leading-6 text-slate-600">Manage patient and doctor records, appointments, admissions, rooms, beds, billing and operational reports.</p></article><article className="rounded-xl border border-slate-200 bg-white p-6"><h2 className="text-base font-semibold text-slate-900">Secure access</h2><p className="mt-3 text-sm leading-6 text-slate-600">Separate staff and patient sign-in paths, staff roles, patient-specific sessions and service-layer authorization.</p></article></div><p className="mt-7 text-xs leading-5 text-slate-500">This is a desktop application. The public website provides product information and is not the hospital application.</p></section></main>;
}
