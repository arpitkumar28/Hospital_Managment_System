import type { Metadata } from 'next';
import { AlertTriangle, Check, CircleDashed, KeyRound, LockKeyhole, ShieldCheck } from 'lucide-react';
import { PageIntro, SectionHeading } from '@/components/page-intro';
import { StatusPill } from '@/components/status-pill';

export const metadata: Metadata = { title: 'Security', description: 'Review the Hospital Management System security controls, current account protections and safeguards still required.' };

const practices = [
  { title: 'Password hashing', status: 'Planned', icon: KeyRound, text: 'Move the current plaintext comparison to BCrypt hash and verify methods. Existing users must set a new password; old password values must not be copied into PostgreSQL.' },
  { title: 'Role authorization', status: 'Planned', icon: ShieldCheck, text: 'Add Admin, Receptionist, Doctor and Accountant roles, then enforce permissions in the service layer. The current interface only routes ADMIN to its dashboard.' },
  { title: 'Account lockout & sessions', status: 'Planned', icon: LockKeyhole, text: 'Track failed attempts and temporary locks, update successful-login timestamps, and clear session state at logout.' },
  { title: 'Prepared statements', status: 'Existing screens', icon: Check, text: 'Inspected DAO queries use PreparedStatement. Continue checking all persistence paths and keep validation before database calls.' },
  { title: 'TLS & least privilege', status: 'Migration target', icon: CircleDashed, text: 'PostgreSQL should require SSL and use a restricted app role. The current source is still configured for local MySQL.' },
  { title: 'Audit events', status: 'Planned', icon: AlertTriangle, text: 'Capture important user and administrative actions after authentication, authorization and the audit data model exist.' },
];

export default function SecurityPage() {
  return <main id="main">
    <PageIntro eyebrow="SECURITY & DATA PROTECTION" title="Security is part of the system architecture." description="Understand how authentication, database access and operational controls are handled today, and review the safeguards required before production use." />
    <section className="mx-auto max-w-[1280px] px-6 py-12 sm:py-16 lg:px-10">
      <div className="mb-8 flex gap-3 rounded-2xl border border-amber-200 bg-amber-50 p-5 text-amber-950"><AlertTriangle size={19} className="mt-0.5 shrink-0" aria-hidden="true"/><p className="text-xs leading-6"><b>Current authentication is not production-safe.</b> The legacy `UserDAO` compares the submitted password directly in SQL. Do not use real patient information or deploy with administrator credentials until the authentication migration is implemented and verified.</p></div>
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-3">{practices.map(({title,status,icon:Icon,text})=><article key={title} className="rounded-2xl border border-slate-200 bg-white p-5 sm:p-6"><div className="flex items-center justify-between gap-3"><span className="grid size-10 place-items-center rounded-xl bg-sky-50 text-teal-800"><Icon size={19} aria-hidden="true"/></span><StatusPill status={status}/></div><h2 className="mt-5 text-sm font-bold text-slate-950">{title}</h2><p className="mt-2 text-xs leading-6 text-slate-600">{text}</p></article>)}</div>
      <div className="mt-12 grid gap-7 border-t border-slate-200 pt-9 lg:grid-cols-[.8fr_1.2fr]"><div><SectionHeading eyebrow="SAFE BY DEFAULT" title="The public site stays disconnected." description="This website never connects to PostgreSQL. It contains no `.env` values, credentials, patient records, service-role keys or private connection strings."/></div><div className="grid gap-2 sm:grid-cols-2">{['No database credentials in browser code','No direct PostgreSQL connection from website','No fake security certifications','No real patient data in the preview'].map((item)=><div key={item} className="flex gap-2 rounded-xl bg-slate-50 p-4 text-[11px] leading-5 text-slate-700"><Check size={14} className="mt-0.5 shrink-0 text-emerald-700" aria-hidden="true"/>{item}</div>)}</div></div>
    </section>
  </main>;
}
