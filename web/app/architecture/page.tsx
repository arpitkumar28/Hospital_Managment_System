import type { Metadata } from 'next';
import { GitBranch, Monitor, Network, Shield, Globe2 } from 'lucide-react';
import { ArchitectureDiagram } from '@/components/architecture-diagram';
import { PageIntro, SectionHeading } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Architecture', description: 'How the Java Swing desktop app, PostgreSQL migration target and separate Vercel project website fit together.' };

const boundaries = [
  { icon: Monitor, title: 'Desktop is the product', body: 'The Java Swing application remains the primary hospital operations system. Vercel does not execute or host the desktop GUI.' },
  { icon: Network, title: 'Database access stays private', body: 'The desktop app uses JDBC to connect to PostgreSQL over TLS. The public website has no direct database connection or secret environment values.' },
  { icon: GitBranch, title: 'One repository, separate directories', body: 'Java source remains in the existing project tree. The independently built Next.js presentation lives under web/.' },
  { icon: Shield, title: 'Migration status remains visible', body: 'The current Java source uses local MySQL configuration. PostgreSQL, service-layer boundaries and account security are target work.' },
];

export default function ArchitecturePage() {
  return <main id="main"><PageIntro eyebrow="APPLICATION ARCHITECTURE" title="Two deliverables. Clear boundaries." description="The desktop app handles hospital workflows. The Next.js site explains the project and is deployed separately on Vercel." />
    <section className="mx-auto max-w-[1280px] px-6 py-12 sm:py-16 lg:px-10"><ArchitectureDiagram />
      <div className="mt-14"><SectionHeading eyebrow="WHY THIS SPLIT" title="Keep responsibilities easy to understand."/><div className="grid gap-4 sm:grid-cols-2">{boundaries.map(({icon:Icon,title,body})=><article key={title} className="flex gap-4 rounded-2xl border border-slate-200 bg-white p-5"><span className="grid size-10 shrink-0 place-items-center rounded-xl bg-teal-50 text-teal-800"><Icon size={18} aria-hidden="true"/></span><div><h2 className="text-xs font-bold text-slate-950">{title}</h2><p className="mt-2 text-[11px] leading-6 text-slate-600">{body}</p></div></article>)}</div></div>
      <div className="mt-12 rounded-2xl bg-slate-50 p-5 sm:p-7"><div className="flex items-start gap-3"><Globe2 size={18} className="mt-0.5 text-sky-800" aria-hidden="true"/><div><h2 className="text-sm font-bold text-slate-950">Vercel’s role</h2><p className="mt-2 max-w-4xl text-xs leading-6 text-slate-600">Vercel serves only the public project website. It does not run the Java desktop application, provide a JDBC proxy or store PostgreSQL credentials. A future browser-based clinical application would need a separately designed, authenticated API.</p></div></div></div>
    </section>
  </main>;
}
