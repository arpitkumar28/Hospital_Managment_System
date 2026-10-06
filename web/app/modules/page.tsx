import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Product areas', description: 'Hospital Management System desktop application modules.' };

const modules = ['Patients', 'Doctors', 'Appointments', 'Admissions and discharge', 'Rooms', 'Beds', 'Billing', 'Reports', 'Admin user management', 'Staff authentication', 'Patient authentication', 'Audit logging'];

export default function ModulesPage() {
  return <main id="main"><PageIntro eyebrow="PRODUCT AREAS" title="Hospital operations, organized by module." description="The desktop application covers clinical records, operational workflows, billing and account administration."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><ul className="grid gap-x-8 sm:grid-cols-2 lg:grid-cols-3">{modules.map((name)=><li key={name} className="border-t border-slate-200 py-5 text-sm font-medium text-slate-700">{name}</li>)}</ul></section></main>;
}
