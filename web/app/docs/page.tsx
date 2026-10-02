import type { Metadata } from 'next';
import Link from 'next/link';
import { ArrowDownToLine, ArrowRight, BookOpen, Braces, Bug, Cable, Database, FileCode2, FileText, KeyRound, Layers3, Monitor, Rocket, TestTube2, UserRoundCog, Users } from 'lucide-react';
import { PageIntro, SectionHeading } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Documentation', description: 'Installation, user guidance, administration, architecture, database, security and deployment documentation.' };

const docs = [
  { title: 'Introduction', icon: BookOpen, state: 'Available', text: 'Product scope, application responsibilities and platform overview.', target: '/product' },
  { title: 'Installation', icon: Monitor, state: 'In progress', text: 'Java setup and desktop application installation guidance.', target: '/download' },
  { title: 'System requirements', icon: Braces, state: 'Available', text: 'Java 17+ for the desktop app; Node.js 20.9+ for the website.', target: '/download' },
  { title: 'Architecture', icon: Layers3, state: 'Available', text: 'How Java Swing, JDBC, PostgreSQL and the website relate.', target: '/architecture' },
  { title: 'Database setup', icon: Database, state: 'Draft', text: 'PostgreSQL schema assumptions, safety and outstanding source-dump review.', target: '/database' },
  { title: 'Configuration', icon: Cable, state: 'In progress', text: 'Environment-based database setup is planned; no live cloud connection exists.', target: '/database' },
  { title: 'Authentication', icon: KeyRound, state: 'Planned', text: 'BCrypt, account lockout, registration and session work are not implemented yet.', target: '/security' },
  { title: 'User roles', icon: UserRoundCog, state: 'Planned', text: 'Administrator, Receptionist, Doctor and Accountant roles are the target.', target: '/security' },
  { title: 'Modules', icon: Users, state: 'Available', text: 'Patient, doctor, appointment, admission, room, bed and billing workflows.', target: '/modules' },
  { title: 'Development', icon: FileCode2, state: 'Available', text: 'Maven desktop application and separate Next.js public website.', target: '/architecture' },
  { title: 'Testing', icon: TestTube2, state: 'In progress', text: 'Java test suite and database integration coverage still need implementation.', target: '/database' },
  { title: 'Deployment', icon: Rocket, state: 'Available', text: 'Deploy the public product website separately from the desktop application.', target: '/download' },
  { title: 'Troubleshooting', icon: Bug, state: 'In progress', text: 'More setup guidance will follow after database configuration is complete.', target: '/database' },
] as const;

export default function DocumentationPage() {
  return <main id="main"><PageIntro eyebrow="PRODUCT DOCUMENTATION" title="Guidance for users and administrators." description="Find installation, system requirements, architecture, database, security and deployment notes for the Hospital Management System." />
    <section className="mx-auto max-w-[1280px] px-6 py-12 sm:py-16 lg:px-10"><SectionHeading eyebrow="GUIDE" title="Find your way around." description="Browse installation, administration, architecture, data and security guidance for the desktop system."/><div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">{docs.map(({title,icon:Icon,state,text,target})=><Link href={target} key={title} className="group rounded-2xl border border-slate-200 bg-white p-4 transition hover:border-teal-200 hover:shadow-md sm:p-5"><div className="flex items-center justify-between"><span className="grid size-9 place-items-center rounded-lg bg-teal-50 text-teal-800"><Icon size={17} aria-hidden="true"/></span><span className={`rounded-full px-2 py-1 text-[8px] font-bold uppercase tracking-wider ${state==='Available'?'bg-emerald-50 text-emerald-800':state==='Draft'?'bg-amber-50 text-amber-800':'bg-slate-100 text-slate-600'}`}>{state}</span></div><h2 className="mt-4 text-xs font-bold text-slate-950">{title}</h2><p className="mt-2 min-h-10 text-[10px] leading-5 text-slate-600">{text}</p><span className="mt-4 inline-flex items-center gap-1 text-[9px] font-bold text-teal-800">Open section <ArrowRight size={12}/></span></Link>)}</div>
      <details className="mt-10 rounded-2xl border border-slate-200 bg-white p-5"><summary className="cursor-pointer text-sm font-bold text-slate-900">Engineering notes and database migration records</summary><div className="mt-5 grid gap-3 md:grid-cols-2"><DocumentLink href="/documents/database-setup.md" icon={Database} title="Database migration foundation" description="Schema assumptions and the safe migration sequence."/><DocumentLink href="/documents/migration-report.md" icon={FileText} title="Database engineering record" description="Inspection findings, work completed and verification limits."/></div><p className="mt-4 text-xs leading-5 text-slate-500">These engineering notes document an initial schema foundation. Review against the authoritative source database before using it for a client deployment.</p></details>
      <div className="mt-10 rounded-2xl border border-slate-200 bg-slate-50 p-5"><h2 className="text-sm font-bold text-slate-950">Website deployment</h2><p className="mt-2 text-xs text-slate-600">The public product site lives in `web/` and can be deployed to Vercel independently of the Java desktop application.</p><Link href="/contact" className="mt-4 inline-flex items-center gap-2 text-[10px] font-bold text-teal-800">Contact <ArrowRight size={13}/></Link></div>
    </section></main>;
}

function DocumentLink({href,icon:Icon,title,description}:{href:string;icon:typeof Database;title:string;description:string}) { return <a href={href} className="flex items-center gap-4 rounded-2xl border border-slate-200 bg-white p-4 transition hover:border-teal-200 hover:shadow-sm"><span className="grid size-10 shrink-0 place-items-center rounded-xl bg-sky-50 text-sky-800"><Icon size={18}/></span><span className="min-w-0 flex-1"><b className="block text-xs text-slate-900">{title}</b><small className="mt-1 block text-[9px] leading-5 text-slate-500">{description}</small></span><ArrowDownToLine size={15} className="shrink-0 text-slate-400" aria-hidden="true"/></a>; }
