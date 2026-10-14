import Link from 'next/link';
import {
  ArrowRight,
  BadgeCheck,
  BedDouble,
  Building2,
  CalendarDays,
  ChartNoAxesCombined,
  ClipboardList,
  CreditCard,
  Database,
  Github,
  Monitor,
  ShieldCheck,
  Stethoscope,
  Users,
} from 'lucide-react';
import { repositoryUrl } from '@/lib/site-data';

const modules = [
  { name: 'Patients', detail: 'Patient records and identity-linked access.', icon: Users },
  { name: 'Doctors', detail: 'Doctor profiles and clinical directory.', icon: Stethoscope },
  { name: 'Appointments', detail: 'Coordinate visits and appointment status.', icon: CalendarDays },
  { name: 'Admissions', detail: 'Manage inpatient admissions and discharge.', icon: ClipboardList },
  { name: 'Rooms & beds', detail: 'Track room inventory and bed availability.', icon: BedDouble },
  { name: 'Billing', detail: 'Create bills, record payments, and review balances.', icon: CreditCard },
  { name: 'Reports', detail: 'Review operational and billing summaries.', icon: ChartNoAxesCombined },
  { name: 'User management', detail: 'Manage staff accounts and role access.', icon: ShieldCheck },
];

export default function HomePage() {
  return (
    <main id="main">
      <section className="mesh-bg overflow-hidden border-b border-slate-200/80">
        <div className="mx-auto grid max-w-[1360px] items-center gap-12 px-6 py-14 sm:py-18 lg:grid-cols-[.92fr_1.08fr] lg:px-10 lg:py-24">
          <div className="relative z-10">
            <p className="inline-flex items-center gap-2 rounded-full border border-teal-100 bg-white/80 px-3 py-2 text-[9px] font-bold uppercase tracking-[.16em] text-teal-900 shadow-sm">
              <span className="size-1.5 rounded-full bg-teal-600" /> Hospital Management System
            </p>
            <h1 className="mt-6 max-w-2xl text-balance text-4xl font-bold leading-[1.07] tracking-[-.055em] text-slate-950 sm:text-5xl lg:text-[62px]">
              Hospital operations, connected in one system.
            </h1>
            <p className="mt-6 max-w-xl text-sm leading-7 text-slate-600 sm:text-base sm:leading-8">
              A Java desktop workspace for patient records, clinical operations, admissions, billing, and secure staff access—backed by PostgreSQL.
            </p>
            <div className="mt-8 flex flex-wrap gap-3">
              <Link href="/product" className="group inline-flex h-12 items-center gap-2 rounded-xl bg-teal-800 px-5 text-xs font-bold text-white shadow-lg shadow-teal-900/15 transition hover:-translate-y-0.5 hover:bg-teal-900">
                Explore the product <ArrowRight size={15} className="transition-transform group-hover:translate-x-0.5" />
              </Link>
              <a href={repositoryUrl} target="_blank" rel="noreferrer" className="inline-flex h-12 items-center gap-2 rounded-xl border border-slate-200 bg-white/90 px-4 text-xs font-semibold text-slate-700 shadow-sm hover:border-slate-300 hover:bg-white">
                <Github size={15} aria-hidden="true" /> View on GitHub
              </a>
            </div>
            <div className="mt-7 flex flex-wrap gap-x-5 gap-y-2 text-[10px] font-medium text-slate-500">
              <span className="inline-flex items-center gap-1.5"><BadgeCheck size={13} className="text-teal-700" /> Role-aware workflows</span>
              <span className="inline-flex items-center gap-1.5"><Database size={13} className="text-sky-700" /> PostgreSQL persistence</span>
            </div>
          </div>

          <div className="relative mx-auto w-full max-w-[660px] lg:ml-auto">
            <div aria-hidden="true" className="absolute -inset-5 rounded-[34px] bg-gradient-to-br from-teal-200/35 via-transparent to-sky-200/50 blur-2xl" />
            <div className="relative rotate-[.4deg] rounded-[24px] border border-white bg-white p-3 shadow-2xl shadow-slate-900/10 sm:p-5" aria-label="Illustrative desktop product view, not an application screenshot">
              <div className="flex items-center justify-between border-b border-slate-100 px-1 pb-4">
                <div className="flex items-center gap-3">
                  <span className="grid size-10 place-items-center rounded-xl bg-teal-800 text-white shadow-sm"><Building2 size={19} /></span>
                  <div><p className="text-xs font-bold text-slate-900">Hospital Management System</p><p className="mt-1 text-[10px] text-slate-500">Desktop workspace · PostgreSQL</p></div>
                </div>
                <span className="rounded-full border border-emerald-100 bg-emerald-50 px-2.5 py-1.5 text-[9px] font-semibold text-emerald-800">Illustrative view</span>
              </div>
              <div className="grid gap-4 py-5 sm:grid-cols-[148px_1fr]">
                <aside className="hidden space-y-1.5 border-r border-slate-100 pr-3 sm:block">
                  <p className="mb-3 px-2 text-[9px] font-bold uppercase tracking-wider text-slate-400">Workspace</p>
                  {['Dashboard', 'Patients', 'Appointments', 'Billing', 'User Management'].map((item, index) => (
                    <div key={item} className={`rounded-lg px-2.5 py-2.5 text-[10px] ${index === 0 ? 'bg-teal-50 font-semibold text-teal-900' : 'text-slate-600'}`}>{item}</div>
                  ))}
                </aside>
                <div className="min-w-0">
                  <div className="flex items-center justify-between gap-3">
                    <div><p className="text-[10px] font-medium text-slate-500">Hospital operations</p><p className="mt-1 text-xl font-bold tracking-tight text-slate-900">Dashboard</p></div>
                    <span className="inline-flex items-center gap-1.5 rounded-full bg-slate-50 px-2.5 py-1.5 text-[9px] font-semibold text-slate-600"><BadgeCheck size={12} className="text-teal-700" /> Role-secured</span>
                  </div>
                  <div className="mt-5 grid grid-cols-2 gap-3">
                    <div className="rounded-xl border border-slate-100 bg-slate-50/70 p-3.5"><Users size={16} className="text-teal-700" /><p className="mt-4 text-[9px] text-slate-500">Patient records</p><p className="mt-1 text-xs font-semibold text-slate-800">Patient management</p></div>
                    <div className="rounded-xl border border-slate-100 bg-slate-50/70 p-3.5"><CalendarDays size={16} className="text-sky-700" /><p className="mt-4 text-[9px] text-slate-500">Care coordination</p><p className="mt-1 text-xs font-semibold text-slate-800">Appointments</p></div>
                  </div>
                  <div className="mt-4 flex items-center justify-between rounded-xl border border-slate-100 bg-white px-3.5 py-3">
                    <div className="flex items-center gap-2.5"><span className="grid size-8 place-items-center rounded-lg bg-amber-50 text-amber-700"><BedDouble size={15} /></span><span><span className="block text-[10px] font-semibold text-slate-800">Room & bed management</span><span className="mt-0.5 block text-[9px] text-slate-500">Admissions linked to availability</span></span></div>
                    <ArrowRight size={14} className="text-slate-400" />
                  </div>
                </div>
              </div>
              <p className="flex items-center justify-center gap-1.5 border-t border-slate-100 pt-3 text-center text-[9px] font-semibold tracking-[.12em] text-slate-400"><Monitor size={12} /> ILLUSTRATIVE PRODUCT VIEW · NOT A SCREENSHOT</p>
            </div>
          </div>
        </div>
      </section>

      <section className="border-b border-teal-100 bg-white">
        <div className="mx-auto grid max-w-[1360px] gap-4 px-6 py-5 sm:grid-cols-3 lg:px-10">
          <PlatformFact title="Desktop application" detail="Java 17 · Swing · Maven" />
          <PlatformFact title="Structured data" detail="PostgreSQL · JDBC · HikariCP" />
          <PlatformFact title="Controlled access" detail="Staff roles · Patient sessions" />
        </div>
      </section>

      <section className="mx-auto max-w-[1360px] px-6 py-16 sm:py-20 lg:px-10">
        <div className="flex flex-col gap-5 sm:flex-row sm:items-end sm:justify-between">
          <div className="max-w-2xl">
            <p className="text-[10px] font-bold uppercase tracking-[.16em] text-teal-800">Product areas</p>
            <h2 className="mt-3 text-2xl font-bold tracking-[-.04em] text-slate-950 sm:text-3xl">A clearer view of hospital operations</h2>
            <p className="mt-3 text-sm leading-7 text-slate-600">Core workflows are organized into focused modules with role-aware access and a shared PostgreSQL data layer.</p>
          </div>
          <Link href="/modules" className="inline-flex shrink-0 items-center gap-2 text-xs font-bold text-teal-800 hover:text-teal-950">Browse all modules <ArrowRight size={14} /></Link>
        </div>
        <div className="mt-8 grid gap-3 sm:grid-cols-2 lg:grid-cols-4">
          {modules.map(({ name, detail, icon: Icon }, index) => (
            <article key={name} className="rounded-2xl border border-slate-200 bg-white p-5">
              <div className="flex items-center justify-between">
                <span className={`grid size-10 place-items-center rounded-xl ${index % 2 === 0 ? 'bg-teal-50 text-teal-800' : 'bg-sky-50 text-sky-800'}`}><Icon size={18} strokeWidth={1.8} /></span>
                <span className="text-[9px] font-bold tracking-[.12em] text-slate-300">{String(index + 1).padStart(2, '0')}</span>
              </div>
              <h3 className="mt-5 text-sm font-bold text-slate-900">{name}</h3>
              <p className="mt-2 text-xs leading-5 text-slate-600">{detail}</p>
            </article>
          ))}
        </div>
      </section>
    </main>
  );
}

function PlatformFact({ title, detail }: { title: string; detail: string }) {
  return <div className="border-l-2 border-teal-600 pl-4">
    <p className="text-[10px] font-bold uppercase tracking-[.1em] text-slate-900">{title}</p>
    <p className="mt-1.5 text-xs text-slate-500">{detail}</p>
  </div>;
}
