import type { Metadata } from 'next';
import { CircleHelp, ShieldCheck } from 'lucide-react';
import { CapabilityCard } from '@/components/capability-card';
import { PageIntro } from '@/components/page-intro';
import { capabilities } from '@/lib/site-data';

export const metadata: Metadata = { title: 'Features', description: 'Explore patient management, appointments, admissions, billing, security and administration capabilities.' };

export default function FeaturesPage() {
  const existing = capabilities.filter((item) => item.status === 'Available').length;
  const planned = capabilities.length - existing;
  return <main id="main">
    <PageIntro eyebrow="PLATFORM CAPABILITIES" title="Connected tools for hospital operations." description="Explore the current system workflows and the related capabilities under development. Descriptions are based on the application source and clearly identify planned functionality." />
    <section className="mx-auto max-w-[1280px] px-6 py-12 sm:py-16 lg:px-10">
      <div className="mb-8 grid gap-3 rounded-2xl border border-slate-200 bg-slate-50 p-4 sm:grid-cols-2 sm:p-5"><div className="flex items-center gap-3"><span className="grid size-9 place-items-center rounded-lg bg-emerald-50 text-emerald-700"><ShieldCheck size={18} aria-hidden="true"/></span><p className="text-xs leading-5 text-slate-600"><b className="text-slate-900">{existing} capabilities have existing screens</b><br/>Core patient, doctor, appointment, admission, bed and billing workflows.</p></div><div className="flex items-center gap-3"><span className="grid size-9 place-items-center rounded-lg bg-sky-50 text-sky-800"><CircleHelp size={18} aria-hidden="true"/></span><p className="text-xs leading-5 text-slate-600"><b className="text-slate-900">{planned} capabilities need further implementation</b><br/>Marked in progress or planned on each card.</p></div></div>
      <div className="grid gap-4 sm:grid-cols-2 xl:grid-cols-3">{capabilities.map((capability) => <CapabilityCard capability={capability} key={capability.title} />)}</div>
    </section>
  </main>;
}
