import type { Capability } from '@/lib/site-data';
import { ArrowUpRight } from 'lucide-react';
import { StatusPill } from '@/components/status-pill';

export function CapabilityCard({ capability }: { capability: Capability }) {
  const Icon = capability.icon;
  return <article className="group rounded-2xl border border-slate-200 bg-white p-5 shadow-[0_4px_16px_rgba(16,44,66,.035)] transition duration-200 hover:-translate-y-0.5 hover:border-teal-200 hover:shadow-lg sm:p-6">
    <div className="flex items-start justify-between gap-3"><span className="grid size-11 place-items-center rounded-xl bg-teal-50 text-teal-800 ring-1 ring-inset ring-teal-100"><Icon size={20} strokeWidth={1.8} aria-hidden="true" /></span><StatusPill status={capability.status} /></div>
    <h2 className="mt-5 text-base font-bold tracking-tight text-slate-950">{capability.title}</h2>
    <p className="mt-2 min-h-12 text-xs leading-6 text-slate-600">{capability.description}</p>
    <ul className="mt-4 grid gap-2 border-t border-slate-100 pt-4">{capability.details.map((detail) => <li key={detail} className="flex gap-2 text-[11px] leading-5 text-slate-600"><span className="mt-1.5 size-1 shrink-0 rounded-full bg-teal-500" />{detail}</li>)}</ul>
    <ArrowUpRight size={15} className="mt-4 text-slate-300 transition group-hover:text-teal-700" aria-hidden="true" />
  </article>;
}
