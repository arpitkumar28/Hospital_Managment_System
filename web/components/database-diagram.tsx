import { Activity, BedDouble, Bell, CalendarDays, ClipboardList, CreditCard, Database, DoorOpen, FileText, UserRound, Users } from 'lucide-react';

const entities = [
  { name: 'Users', icon: UserRound, group: 'Access' },
  { name: 'Patients', icon: Users, group: 'People' },
  { name: 'Doctors', icon: UserRound, group: 'People' },
  { name: 'Rooms', icon: DoorOpen, group: 'Capacity' },
  { name: 'Beds', icon: BedDouble, group: 'Capacity' },
  { name: 'Admissions', icon: ClipboardList, group: 'Care' },
  { name: 'Appointments', icon: CalendarDays, group: 'Care' },
  { name: 'Bills', icon: FileText, group: 'Finance' },
  { name: 'Payments', icon: CreditCard, group: 'Planned' },
  { name: 'Audit logs', icon: Activity, group: 'Planned' },
  { name: 'Notifications', icon: Bell, group: 'Planned' },
];

export function DatabaseDiagram() {
  return <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm sm:p-7">
    <div className="flex flex-wrap items-center justify-between gap-3"><div><h2 className="text-base font-bold text-slate-950">Conceptual data map</h2><p className="mt-1 text-[10px] leading-5 text-slate-500">A high-level view of related entities—not a verified production schema.</p></div><span className="inline-flex items-center gap-2 rounded-full bg-sky-50 px-3 py-1.5 text-[9px] font-bold uppercase tracking-wider text-sky-800"><Database size={13}/> PostgreSQL target</span></div>
    <div role="img" aria-label="Conceptual hospital database entities: users, patients, doctors, rooms, beds, admissions, appointments, bills, planned payments, audit logs and notifications" className="mt-6 grid gap-2 sm:grid-cols-2 lg:grid-cols-4">{entities.map(({name,icon:Icon,group})=><div key={name} className={`flex items-center gap-3 rounded-xl border p-3 ${group==='Planned'?'border-dashed border-amber-300 bg-amber-50/50':'border-slate-200 bg-slate-50/70'}`}><span className={`grid size-8 place-items-center rounded-lg ${group==='Planned'?'bg-white text-amber-800':'bg-white text-teal-800'}`}><Icon size={15} aria-hidden="true"/></span><span><b className="block text-[10px] text-slate-900">{name}</b><small className="text-[8px] text-slate-500">{group==='Planned'?'Planned entity':`${group} records`}</small></span></div>)}</div>
    <div className="mt-5 grid gap-4 border-t border-slate-100 pt-5 md:grid-cols-2"><div><h3 className="text-[10px] font-bold uppercase tracking-[.12em] text-slate-700">Core relationships</h3><ul className="mt-3 grid gap-2 text-[10px] leading-5 text-slate-600"><li>Patients → appointments ← doctors</li><li>Rooms → beds → admissions ← patients</li><li>Patients → bills ← admissions</li><li>Users → planned audit events and account activity</li></ul></div><div><h3 className="text-[10px] font-bold uppercase tracking-[.12em] text-slate-700">Reliability foundations</h3><ul className="mt-3 grid gap-2 text-[10px] leading-5 text-slate-600"><li>Foreign keys and restrictive deletion behavior in the draft schema</li><li>Indexes for common lookups and joins</li><li>Transactions for admission and discharge flows</li><li>Payments, audit logs and notifications still need full implementation</li></ul></div></div>
  </div>;
}
