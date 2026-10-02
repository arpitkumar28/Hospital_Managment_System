import type { Metadata } from 'next';
import { ImagePlus } from 'lucide-react';
import { PageIntro } from '@/components/page-intro';
import { screenshotAreas } from '@/lib/site-data';

export const metadata: Metadata = { title: 'Screenshots', description: 'A gallery waiting for verified screenshots from the Java Swing application.' };

export default function ScreenshotsPage() {
  return <main id="main">
    <PageIntro eyebrow="APPLICATION GALLERY" title="Real screens, when they are ready." description="No verified screenshot set is available yet. Every tile below is explicitly a placeholder; it is not an image or a reconstruction of the running application." />
    <section className="mx-auto max-w-[1280px] px-6 py-12 sm:py-16 lg:px-10"><div className="mb-7 flex items-center gap-3 rounded-2xl border border-slate-200 bg-slate-50 p-4"><span className="grid size-10 place-items-center rounded-xl bg-white text-teal-800 ring-1 ring-slate-200"><ImagePlus size={18} aria-hidden="true"/></span><p className="text-xs leading-5 text-slate-600"><b className="text-slate-900">Screenshot will be added.</b> Capture each view from the actual Java desktop app before replacing these placeholders.</p></div>
      <div className="grid gap-4 sm:grid-cols-2 lg:grid-cols-3">{screenshotAreas.map((name,index)=><figure key={name} className="overflow-hidden rounded-2xl border border-slate-200 bg-white"><div className="soft-grid grid aspect-[16/10] place-items-center bg-slate-50 p-5"><div className="w-[82%] overflow-hidden rounded-xl border border-slate-200 bg-white shadow-sm"><div className="flex h-7 items-center gap-1.5 border-b border-slate-100 px-3"><i className="size-1.5 rounded-full bg-rose-300"/><i className="size-1.5 rounded-full bg-amber-300"/><i className="size-1.5 rounded-full bg-emerald-300"/><span className="ml-auto text-[7px] font-medium text-slate-400">Java Swing · {name}</span></div><div className="p-4"><span className="text-[8px] font-bold uppercase tracking-[.13em] text-teal-700">{name.toUpperCase()}</span><div className="mt-3 grid grid-cols-3 gap-2"><i className="h-8 rounded-md bg-sky-50"/><i className="h-8 rounded-md bg-teal-50"/><i className="h-8 rounded-md bg-slate-100"/></div><div className="mt-2 grid gap-1.5">{[0,1,2].map((line)=><i key={line} className="h-2 rounded bg-slate-100"/>)}</div></div></div></div><figcaption className="flex items-center justify-between gap-2 px-4 py-3"><span><b className="block text-xs text-slate-900">{name}</b><small className="mt-1 block text-[9px] text-slate-500">Screenshot will be added</small></span><span className="rounded-full bg-slate-100 px-2 py-1 text-[8px] font-bold uppercase tracking-wider text-slate-500">{String(index+1).padStart(2,'0')}</span></figcaption></figure>)}</div>
    </section>
  </main>;
}
