import type { ReactNode } from 'react';
import { Building2, Database, ShieldCheck } from 'lucide-react';

export function PageIntro({ eyebrow, title, description, children }: { eyebrow: string; title: string; description: string; children?: ReactNode }) {
  return <section className="mesh-bg border-b border-slate-200/80">
    <div className="mx-auto grid max-w-[1360px] items-center gap-10 px-6 pb-12 pt-14 sm:pb-16 sm:pt-20 lg:grid-cols-[1.25fr_.75fr] lg:px-10 lg:pb-20 lg:pt-24">
      <div>
        <p className="flex items-center gap-2 text-[10px] font-bold uppercase tracking-[.17em] text-teal-800"><span className="size-1.5 rounded-full bg-teal-600" />{eyebrow}</p>
        <h1 className="text-balance mt-5 max-w-4xl text-4xl font-bold leading-[1.08] tracking-[-.055em] text-slate-950 sm:text-5xl lg:text-[60px]">{title}</h1>
        <p className="mt-5 max-w-3xl text-sm leading-7 text-slate-600 sm:text-base sm:leading-8">{description}</p>
        {children}
      </div>
      <aside aria-label="Platform overview" className="relative hidden min-h-[280px] overflow-hidden rounded-[28px] border border-white/80 bg-gradient-to-br from-[#eaf6f3] via-white to-[#eaf2f7] p-7 shadow-xl shadow-slate-900/[.07] lg:block">
        <div aria-hidden="true" className="absolute -right-10 -top-12 size-56 rounded-full border border-teal-900/[.07]" />
        <div aria-hidden="true" className="absolute -right-1 -top-3 size-36 rounded-full border border-teal-900/[.08]" />
        <div className="relative flex h-full min-h-[226px] flex-col justify-between">
          <div className="flex items-center justify-between">
            <span className="grid size-12 place-items-center rounded-2xl bg-teal-800 text-white shadow-lg shadow-teal-900/15"><Building2 size={23} strokeWidth={1.8} /></span>
            <span className="rounded-full border border-white bg-white/80 px-3 py-1.5 text-[9px] font-bold uppercase tracking-[.14em] text-teal-900">Desktop platform</span>
          </div>
          <div>
            <p className="text-[10px] font-bold uppercase tracking-[.17em] text-teal-800">Hospital Management System</p>
            <p className="mt-2 max-w-xs text-2xl font-bold leading-tight tracking-[-.04em] text-slate-900">Care operations, connected.</p>
            <div className="mt-5 flex flex-wrap gap-2">
              <span className="inline-flex items-center gap-1.5 rounded-lg border border-white bg-white/85 px-2.5 py-2 text-[10px] font-semibold text-slate-700"><ShieldCheck size={13} className="text-teal-700" /> Role-aware access</span>
              <span className="inline-flex items-center gap-1.5 rounded-lg border border-white bg-white/85 px-2.5 py-2 text-[10px] font-semibold text-slate-700"><Database size={13} className="text-sky-700" /> PostgreSQL-backed</span>
            </div>
          </div>
        </div>
      </aside>
    </div>
  </section>;
}

export function SectionHeading({ eyebrow, title, description, light=false }: { eyebrow: string; title: string; description?: string; light?: boolean }) {
  return <div className="mb-8 max-w-3xl"><p className={`text-[10px] font-bold uppercase tracking-[.16em] ${light?'text-teal-200':'text-teal-800'}`}><span className="mr-2 inline-block size-1.5 rounded-full bg-current align-middle" />{eyebrow}</p><h2 className={`mt-3 text-2xl font-bold tracking-[-.04em] sm:text-3xl ${light?'text-white':'text-slate-950'}`}>{title}</h2>{description && <p className={`mt-3 max-w-2xl text-sm leading-7 ${light?'text-slate-300':'text-slate-600'}`}>{description}</p>}</div>;
}
