import Link from 'next/link';
import { ArrowUpRight, Github, Plus } from 'lucide-react';
import { repositoryUrl } from '@/lib/site-data';

export function Footer() {
  const productLinks = [['Product','/product'],['Modules','/modules'],['Application screens','/screenshots']] as const;
  return <footer className="border-t border-slate-200 bg-slate-950 text-slate-300">
    <div className="mx-auto grid max-w-[1280px] gap-10 px-6 py-12 sm:grid-cols-2 lg:grid-cols-[1.5fr_1fr_1fr] lg:gap-14 lg:px-10 lg:py-16">
      <div>
        <Link href="/" className="flex items-center gap-3 text-white"><span className="grid size-10 place-items-center rounded-xl bg-teal-700"><Plus size={22} strokeWidth={2.6} aria-hidden="true" /></span><span className="text-sm font-bold tracking-tight">Hospital Management System</span></Link>
        <p className="mt-5 max-w-xs text-xs leading-6 text-slate-400">Java Desktop Application</p>
        <a href={repositoryUrl} target="_blank" rel="noreferrer" className="mt-5 inline-flex items-center gap-2 text-xs font-semibold text-teal-300 hover:text-white"><Github size={15} aria-hidden="true" /> GitHub <ArrowUpRight size={13} aria-hidden="true" /></a>
      </div>
      <div><h2 className="text-[10px] font-bold uppercase tracking-[.16em] text-slate-500">Product</h2><ul className="mt-4 grid gap-3">{productLinks.map(([label,href])=><li key={href}><Link href={href} className="text-xs text-slate-300 hover:text-teal-200">{label}</Link></li>)}</ul></div>
      <div><h2 className="text-[10px] font-bold uppercase tracking-[.16em] text-slate-500">Resources</h2><ul className="mt-4 grid gap-3"><li><Link href="/docs" className="text-xs text-slate-300 hover:text-teal-200">Documentation</Link></li><li><Link href="/contact" className="text-xs text-slate-300 hover:text-teal-200">Contact</Link></li><li><a href={repositoryUrl} target="_blank" rel="noreferrer" className="inline-flex items-center gap-1 text-xs text-slate-300 hover:text-teal-200">GitHub <ArrowUpRight size={12}/></a></li></ul></div>
    </div>
    <div className="border-t border-white/10"><div className="mx-auto flex max-w-[1280px] flex-col gap-2 px-6 py-5 text-[10px] text-slate-500 sm:flex-row sm:items-center sm:justify-between lg:px-10"><span>© 2026 Hospital Management System</span><span>Java Desktop Application</span></div></div>
  </footer>;
}
