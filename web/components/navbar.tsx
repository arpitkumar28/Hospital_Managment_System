'use client';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { ArrowRight, Github, Menu, Plus, X } from 'lucide-react';
import { useState } from 'react';
import { repositoryUrl, routes, type SitePath } from '@/lib/site-data';

export function Navbar() {
  const pathname = usePathname();
  const [open, setOpen] = useState(false);

  return (
    <header className="sticky top-0 z-50 border-b border-slate-200/80 bg-white/90 backdrop-blur-xl">
      <div className="mx-auto flex h-[76px] max-w-[1360px] items-center justify-between gap-5 px-5 sm:px-8 xl:px-12">
        <Link href="/" aria-label="Hospital Management System home" className="flex shrink-0 items-center gap-3">
          <span className="grid size-10 place-items-center rounded-xl bg-teal-50 text-teal-700 ring-1 ring-teal-100"><Plus size={24} strokeWidth={2.5} aria-hidden="true" /></span>
          <span className="flex flex-col"><strong className="text-[15px] font-bold tracking-[-.04em] text-slate-900">Hospital Management</strong><small className="mt-0.5 text-[8px] font-bold tracking-[.12em] text-slate-500">Healthcare Operations Platform</small></span>
        </Link>

        <nav aria-label="Main navigation" className="hidden items-center gap-3 lg:flex xl:gap-4">
          {['/','/product','/features','/modules','/screenshots','/security','/architecture'].map((href) => {const route=routes.find((item)=>item.href===href);return route?<NavLink key={route.href} {...route} current={pathname}/>:null;})}
        </nav>

        <div className="hidden shrink-0 items-center gap-2 lg:flex">
          <Link href="/docs" className="hidden text-[11px] font-semibold text-slate-600 transition hover:text-teal-700 xl:inline-flex">Documentation</Link>
          <a href={repositoryUrl} target="_blank" rel="noreferrer" className="inline-flex h-10 items-center gap-2 rounded-lg border border-slate-200 px-3 text-[11px] font-semibold text-slate-700 transition hover:border-slate-300 hover:bg-slate-50"><Github size={15} aria-hidden="true" /> GitHub</a>
          <Link href="/contact" className="inline-flex h-10 items-center gap-2 rounded-lg bg-teal-700 px-3.5 text-[11px] font-semibold text-white shadow-sm transition hover:bg-teal-800">Request demo <ArrowRight size={14} aria-hidden="true" /></Link>
          <Link href="/download" className="inline-flex h-10 items-center gap-2 rounded-lg border border-slate-200 px-3 text-[11px] font-semibold text-slate-700 transition hover:border-teal-300 hover:text-teal-800">Download</Link>
        </div>

        <button type="button" className="grid size-10 place-items-center rounded-lg border border-slate-200 text-slate-700 lg:hidden" aria-label={open ? 'Close navigation menu' : 'Open navigation menu'} aria-expanded={open} aria-controls="mobile-navigation" onClick={() => setOpen((value) => !value)}>
          {open ? <X size={19} aria-hidden="true" /> : <Menu size={19} aria-hidden="true" />}
        </button>
      </div>
      {open && <nav id="mobile-navigation" aria-label="Mobile navigation" className="border-t border-slate-200 bg-white px-5 py-3 lg:hidden">
        <div className="mx-auto grid max-w-[1360px] grid-cols-2 gap-1 sm:grid-cols-3">
          {routes.map((route) => <NavLink key={route.href} {...route} current={pathname} onNavigate={() => setOpen(false)} />)}
          <a href={repositoryUrl} target="_blank" rel="noreferrer" className="flex items-center gap-2 rounded-lg px-3 py-3 text-xs font-semibold text-slate-700 hover:bg-slate-50"><Github size={15} aria-hidden="true" /> GitHub <ArrowRight size={13} aria-hidden="true" /></a>
        </div>
        <div className="mx-auto mt-3 flex max-w-[1360px] gap-2 border-t border-slate-100 pt-3"><Link href="/docs" onClick={()=>setOpen(false)} className="flex-1 rounded-lg border border-slate-200 px-3 py-3 text-center text-xs font-semibold text-slate-700">Documentation</Link><Link href="/contact" onClick={()=>setOpen(false)} className="flex-1 rounded-lg bg-teal-700 px-3 py-3 text-center text-xs font-bold text-white">Request demo</Link><Link href="/download" onClick={()=>setOpen(false)} className="flex-1 rounded-lg border border-slate-200 px-3 py-3 text-center text-xs font-semibold text-slate-700">Download</Link></div>
      </nav>}
    </header>
  );
}

function NavLink({ label, href, current, onNavigate }: { label: string; href: SitePath; current: string; onNavigate?: () => void }) {
  const active = href === '/' ? current === '/' : current.startsWith(href);
  return <Link href={href} onClick={onNavigate} aria-current={active ? 'page' : undefined} className={`rounded-md text-[11px] font-semibold transition focus-visible:outline-teal-500 ${active ? 'text-teal-700' : 'text-slate-600 hover:text-teal-700'} px-2 py-2 lg:px-0 lg:py-1`}>{label}</Link>;
}
