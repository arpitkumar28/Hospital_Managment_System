import Link from 'next/link';
import { ArrowRight, Github } from 'lucide-react';
import { repositoryUrl } from '@/lib/site-data';

export function CtaSection() {
  return <section className="px-6 py-16 sm:py-20"><div className="mx-auto flex max-w-[1200px] flex-col justify-between gap-7 rounded-3xl bg-gradient-to-br from-[#10364f] via-[#12485a] to-[#087f8c] p-7 text-white shadow-[0_20px_55px_rgba(8,82,101,.2)] sm:p-10 lg:flex-row lg:items-center lg:p-12"><div><p className="text-[10px] font-bold uppercase tracking-[.17em] text-teal-100">Explore the project</p><h2 className="mt-3 max-w-xl text-2xl font-bold tracking-[-.04em] sm:text-3xl">See what’s built and what’s next.</h2><p className="mt-3 max-w-xl text-sm leading-6 text-sky-100/80">Follow the system modules, implementation status and technical architecture.</p></div><div className="flex flex-wrap items-center gap-3"><Link href="/modules" className="inline-flex h-11 items-center gap-2 rounded-lg bg-white px-4 text-xs font-bold text-slate-900 transition hover:bg-teal-50">Explore modules <ArrowRight size={15} aria-hidden="true" /></Link><a href={repositoryUrl} target="_blank" rel="noreferrer" className="inline-flex h-11 items-center gap-2 rounded-lg border border-white/25 px-4 text-xs font-bold text-white transition hover:bg-white/10"><Github size={15} aria-hidden="true" /> GitHub</a></div></div></section>;
}
