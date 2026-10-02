import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Features', description: 'Hospital Management System product areas.' };

const features = ['Patient management', 'Doctor management', 'Appointments', 'Admissions', 'Rooms & beds', 'Billing & payments', 'Reports', 'User management', 'Settings', 'Notifications', 'Audit logs'];

export default function FeaturesPage() {
  return <main id="main"><PageIntro eyebrow="PRODUCT AREAS" title="Designed around hospital operations." description="These areas describe the product scope. Functionality will be presented as available only after the corresponding Java application workflow is implemented."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><ul className="grid gap-x-8 sm:grid-cols-2 lg:grid-cols-3">{features.map((feature)=><li key={feature} className="border-t border-slate-200 py-5 text-sm font-medium text-slate-700">{feature}</li>)}</ul></section></main>;
}
