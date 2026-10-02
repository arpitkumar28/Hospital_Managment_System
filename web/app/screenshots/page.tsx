import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Application screens', description: 'Application screen captures will be published as the Java desktop interface is completed.' };

export default function ScreenshotsPage() {
  return <main id="main"><PageIntro eyebrow="APPLICATION SCREENS" title="Application interface currently under development." description="Real application screenshots will appear here after the corresponding Java screens are completed."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><div className="grid min-h-72 place-items-center rounded-2xl border border-slate-200 bg-white p-8 text-center"><p className="max-w-lg text-sm leading-6 text-slate-600">No application images are displayed on this page.</p></div></section></main>;
}
