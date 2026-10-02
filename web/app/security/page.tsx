import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Security', description: 'Authentication and access-control foundation for the Hospital Management System.' };

export default function SecurityPage() {
  return <main id="main"><PageIntro eyebrow="SECURITY FOUNDATION" title="Authentication is implemented; full application verification remains." description="The desktop application includes BCrypt password hashing, account lockout, sessions, role checks, pending registration and authentication audit events."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><div className="max-w-3xl rounded-xl border border-amber-200 bg-amber-50 p-5"><h2 className="text-sm font-semibold text-amber-950">Verification status</h2><p className="mt-2 text-sm leading-6 text-amber-900">Automated service tests pass. A live PostgreSQL login and registration flow has not been verified, and authorization is not yet integrated across all existing operational screens. Production security review is not complete.</p></div></section></main>;
}
