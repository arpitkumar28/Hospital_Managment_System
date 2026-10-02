export type ProjectStatus = 'Available' | 'In progress' | 'Planned' | 'Migration target' | 'Not verified';

export function StatusPill({ status }: { status: ProjectStatus | string }) {
  const style = status === 'Available'
    ? 'bg-emerald-50 text-emerald-800'
    : status === 'In progress'
      ? 'bg-sky-50 text-sky-800'
      : status === 'Migration target'
        ? 'bg-violet-50 text-violet-800'
        : status === 'Not verified'
          ? 'bg-amber-50 text-amber-800'
          : 'bg-slate-100 text-slate-600';
  return <span className={`inline-flex w-fit items-center rounded-full px-2.5 py-1 text-[8px] font-bold uppercase tracking-wider ${style}`}>{status}</span>;
}
