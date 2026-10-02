import type { Metadata } from 'next';
import Link from 'next/link';
import { ArrowRight, CheckCircle2, CircleAlert, Database, Fingerprint, LockKeyhole, ShieldCheck } from 'lucide-react';
import { DatabaseDiagram } from '@/components/database-diagram';
import { PageIntro, SectionHeading } from '@/components/page-intro';
import { StatusPill } from '@/components/status-pill';

export const metadata: Metadata = { title: 'Database', description: 'Explore the Hospital Management System relational database model, PostgreSQL target and data-integrity approach.' };

const principles = [
  [Database,'Standard PostgreSQL','Use the PostgreSQL JDBC driver and plain SQL/JDBC. Supabase is an intended managed PostgreSQL option, not a separate REST client.'],
  [Fingerprint,'Constraints & indexes','The draft schema uses identity columns, foreign keys, checks and selective indexes. It still needs authoritative source-schema review.'],
  [CheckCircle2,'Transactional workflows','Existing admission and discharge DAO methods already group related updates in transactions. Cross-process bed safety needs PostgreSQL validation.'],
  [LockKeyhole,'Protected connections','Cloud connection values belong in environment variables. Require TLS and use a restricted app role; do not put credentials on this site.'],
];

export default function DatabasePage() {
  return <main id="main"><PageIntro eyebrow="DATABASE & DATA MODEL" title="A relational foundation for hospital workflows." description="The target database is PostgreSQL, with Supabase as a managed PostgreSQL option. The application currently uses MySQL; PostgreSQL connectivity has not yet been verified." />
    <section className="mx-auto max-w-[1280px] px-6 py-12 sm:py-16 lg:px-10"><div className="mb-7 grid gap-3 md:grid-cols-3"><StatusCard icon={CircleAlert} label="CURRENT CONNECTION" title="MySQL" detail="The desktop application is configured for a local MySQL database." status="Available"/><StatusCard icon={Database} label="DATABASE TARGET" title="PostgreSQL / Supabase" detail="A schema foundation exists and requires source database comparison." status="Not verified"/><StatusCard icon={ShieldCheck} label="PUBLIC WEBSITE" title="No database access" detail="This Next.js site has no hospital data connection." status="Separate site"/></div><DatabaseDiagram />
      <div className="mt-12"><SectionHeading eyebrow="DATABASE PRINCIPLES" title="Make each data boundary deliberate."/><div className="grid gap-4 sm:grid-cols-2">{principles.map(([Icon,title,body])=>{const Glyph=Icon as typeof Database;return <article key={String(title)} className="rounded-2xl border border-slate-200 p-5"><span className="grid size-9 place-items-center rounded-lg bg-sky-50 text-sky-800"><Glyph size={17} aria-hidden="true"/></span><h2 className="mt-4 text-sm font-bold text-slate-950">{String(title)}</h2><p className="mt-2 text-[11px] leading-6 text-slate-600">{String(body)}</p></article>;})}</div></div>
      <div className="mt-8 flex flex-wrap items-center justify-between gap-3 rounded-2xl bg-slate-950 p-5 text-white sm:p-6"><span><b className="block text-sm">Review the migration foundation</b><small className="mt-1 block text-[10px] text-slate-300">The authoritative MySQL dump is not in the current checkout.</small></span><Link href="/docs" className="inline-flex items-center gap-2 rounded-lg bg-white px-4 py-2.5 text-[10px] font-bold text-slate-900">Database notes <ArrowRight size={13}/></Link></div>
    </section></main>;
}

function StatusCard({icon:Icon,label,title,detail,status}:{icon:typeof Database;label:string;title:string;detail:string;status:string}) { return <article className="rounded-2xl border border-slate-200 bg-white p-4"><div className="flex items-center justify-between"><span className="grid size-9 place-items-center rounded-lg bg-slate-50 text-teal-800"><Icon size={17}/></span><StatusPill status={status}/></div><p className="mt-4 text-[8px] font-bold uppercase tracking-[.14em] text-slate-400">{label}</p><h2 className="mt-1 text-sm font-bold text-slate-950">{title}</h2><p className="mt-1 text-[10px] leading-5 text-slate-600">{detail}</p></article>; }
