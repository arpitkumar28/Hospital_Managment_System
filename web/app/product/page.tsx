import type { Metadata } from 'next';
import { PageIntro } from '@/components/page-intro';

export const metadata: Metadata = { title: 'Product', description: 'Hospital Management System application overview.' };

export default function ProductPage() {
  return <main id="main"><PageIntro eyebrow="PRODUCT" title="Application under development." description="We are building the Hospital Management System screen-by-screen with connected database operations, authentication, validation, security and real application workflows."/><section className="mx-auto max-w-[1280px] px-6 py-12 lg:px-10"><div className="grid min-h-64 place-items-center rounded-2xl border border-slate-200 bg-white p-8 text-center"><p className="max-w-lg text-sm leading-6 text-slate-600">Real application screenshots will appear here after the corresponding Java screens are completed.</p></div></section></main>;
}
