import type { ReactNode } from 'react';

export function PageIntro({ eyebrow, title, description, children }: { eyebrow: string; title: string; description: string; children?: ReactNode }) {
  return <section className="mesh-bg border-b border-slate-200/80"><div className="mx-auto max-w-[1280px] px-6 pb-12 pt-14 sm:pb-16 sm:pt-20 lg:px-10 lg:pb-20 lg:pt-24">
    <p className="flex items-center gap-2 text-[10px] font-bold uppercase tracking-[.17em] text-teal-800"><span className="size-1.5 rounded-full bg-teal-600" />{eyebrow}</p>
    <h1 className="text-balance mt-5 max-w-4xl text-4xl font-bold leading-[1.08] tracking-[-.055em] text-slate-950 sm:text-5xl lg:text-[64px]">{title}</h1>
    <p className="mt-5 max-w-3xl text-sm leading-7 text-slate-600 sm:text-base sm:leading-8">{description}</p>
    {children}
  </div></section>;
}

export function SectionHeading({ eyebrow, title, description }: { eyebrow: string; title: string; description?: string }) {
  return <div className="mb-8 max-w-3xl"><p className="text-[10px] font-bold uppercase tracking-[.16em] text-teal-800">{eyebrow}</p><h2 className="mt-3 text-2xl font-bold tracking-[-.04em] text-slate-950 sm:text-3xl">{title}</h2>{description && <p className="mt-3 max-w-2xl text-sm leading-7 text-slate-600">{description}</p>}</div>;
}
