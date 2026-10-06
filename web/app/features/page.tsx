import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Features', description: 'Hospital Management System product areas.' };

const features = ['Secure staff authentication', 'Patient authentication', 'Role-based access control', 'Patient management', 'Doctor management', 'Appointment management', 'Admissions and discharge', 'Room management', 'Bed management', 'Billing', 'Admin user management', 'Dashboard', 'Authentication audit logging', 'PostgreSQL database', 'BCrypt password hashing', 'Service-layer authorization'];

export default function FeaturesPage() {
  return <main id="main"><PageIntro eyebrow="PRODUCT CAPABILITIES" title="Hospital workflows with secure access." description="Implemented desktop capabilities combine day-to-day hospital operations with staff and patient authentication and service-layer authorization."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><ul className="grid gap-x-8 sm:grid-cols-2 lg:grid-cols-3">{features.map((feature)=><li key={feature} className="border-t border-slate-200 py-5 text-sm font-medium text-slate-700">{feature}</li>)}</ul></section></main>;
}
