export type ProjectStatus = 'Existing screens' | 'In progress' | 'Planned' | 'Migration target' | 'Not verified';

export function StatusPill({ status }: { status: ProjectStatus | string }) {
  const style = status === 'Existing screens'
    ? 'bg-emerald-50 text-emerald-800 ring-emerald-100'
    : status === 'In progress'
      ? 'bg-amber-50 text-amber-800 ring-amber-100'
      : status === 'Not verified'
        ? 'bg-rose-50 text-rose-800 ring-rose-100'
        : status === 'Migration target'
          ? 'bg-sky-50 text-sky-800 ring-sky-100'
          : 'bg-slate-100 text-slate-600 ring-slate-200';
  return <span className={`inline-flex items-center rounded-full px-2.5 py-1 text-[9px] font-bold uppercase tracking-[.1em] ring-1 ring-inset ${style}`}>{status}</span>;
}
