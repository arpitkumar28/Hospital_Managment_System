import type { LucideIcon } from 'lucide-react';
import {
  Activity, BedDouble, Bell, CalendarDays, ClipboardList, CreditCard,
  FileBarChart, KeyRound, LockKeyhole, Settings2, Stethoscope, UserRound,
  UserRoundCog, Users, Wallet, Workflow,
} from 'lucide-react';

export const repositoryUrl = 'https://github.com/arpitkumar28/Hospital_Managment_System';

export const routes = [
  { label: 'Home', href: '/' },
  { label: 'Features', href: '/features' },
  { label: 'Modules', href: '/modules' },
  { label: 'Architecture', href: '/architecture' },
  { label: 'Screenshots', href: '/screenshots' },
  { label: 'Security', href: '/security' },
  { label: 'Documentation', href: '/docs' },
  { label: 'Team', href: '/team' },
  { label: 'Download', href: '/download' },
  { label: 'Contact', href: '/contact' },
] as const;

export type SitePath = (typeof routes)[number]['href'];

export type Capability = {
  title: string;
  description: string;
  status: 'Existing screens' | 'In progress' | 'Planned';
  details: string[];
  icon: LucideIcon;
};

export const capabilities: Capability[] = [
  { title: 'Authentication', description: 'Staff sign-in starts the desktop workflow.', status: 'In progress', details: ['Login screen exists', 'Legacy password comparison must be replaced with BCrypt', 'Registration and password reset are not implemented'], icon: KeyRound },
  { title: 'Role-based access', description: 'Staff roles should see only the actions their work requires.', status: 'Planned', details: ['Admin, receptionist, doctor and accountant roles', 'Enforce access rules in services', 'Admin user management'], icon: LockKeyhole },
  { title: 'Patient management', description: 'Keep essential patient contact and demographic records together.', status: 'Existing screens', details: ['Add, edit and remove patient records', 'Search and medical history are future work', 'Appointments, admissions and billing links'], icon: Users },
  { title: 'Doctor management', description: 'Maintain doctor details, specialization and availability.', status: 'Existing screens', details: ['Profiles and contact information', 'Specialization and availability fields', 'Appointment assignment'], icon: Stethoscope },
  { title: 'Appointments', description: 'Coordinate visits against the patient and doctor record.', status: 'Existing screens', details: ['Schedule and edit appointments', 'Track appointment status', 'Upcoming, completed and cancellation views are planned'], icon: CalendarDays },
  { title: 'Admissions', description: 'Connect patient stays with room and bed capacity.', status: 'Existing screens', details: ['Admit into an available bed', 'Record discharge and release the bed', 'DAO uses transactions for admit/discharge'], icon: ClipboardList },
  { title: 'Bed management', description: 'Make room capacity and bed availability easier to see.', status: 'Existing screens', details: ['Manage rooms and beds', 'Available and occupied states', 'Preventing conflicting admissions needs database verification'], icon: BedDouble },
  { title: 'Billing', description: 'Track bill charges and current balance.', status: 'Existing screens', details: ['Room, doctor, medicine and other charges', 'Current total and paid amount', 'Invoice consolidation remains unresolved'], icon: Wallet },
  { title: 'Payments', description: 'Record money received against a bill.', status: 'In progress', details: ['Current DAO updates the aggregate bill balance', 'Separate payment history is planned', 'Cash, card, UPI and other methods are planned'], icon: CreditCard },
  { title: 'Reports', description: 'A future summary view for operational and financial activity.', status: 'Planned', details: ['Patient and appointment summaries', 'Admissions and bed occupancy', 'Billing, payment and revenue reports'], icon: FileBarChart },
  { title: 'Audit logs', description: 'Record important administrative and account activity.', status: 'Planned', details: ['Actor and action', 'Timestamp and relevant entity', 'Protected review by administrators'], icon: Activity },
  { title: 'Notifications', description: 'A future way to call attention to operational changes.', status: 'Planned', details: ['Appointment reminders', 'Admission and discharge updates', 'Account and system notices'], icon: Bell },
  { title: 'User management', description: 'Administrator controls for staff accounts.', status: 'Planned', details: ['Review pending registration', 'Assign role and status', 'Unlock and reset accounts'], icon: UserRoundCog },
  { title: 'Profile management', description: 'Let staff review their own account details.', status: 'Planned', details: ['View full name and contact details', 'Change password securely', 'Review role and account status'], icon: UserRound },
  { title: 'Settings', description: 'A home for application preferences and connection status.', status: 'Planned', details: ['Application preferences', 'Database health check', 'Environment-based connection configuration'], icon: Settings2 },
];

export const hospitalModules = [
  { title: 'Patient management', icon: Users, status: 'Existing screens', description: 'Maintain patient contact and demographic records.', items: ['Add and edit patient', 'Search and medical history — planned', 'Admission, appointment and billing history — planned'] },
  { title: 'Doctor management', icon: Stethoscope, status: 'Existing screens', description: 'Keep doctor profiles, specialization and availability organized.', items: ['Doctor profiles', 'Specialization and availability', 'Appointment association'] },
  { title: 'Appointment management', icon: CalendarDays, status: 'Existing screens', description: 'Create and update appointments between patients and doctors.', items: ['Schedule and update', 'Track status', 'Upcoming, completed and cancelled views — planned'] },
  { title: 'Admission management', icon: ClipboardList, status: 'Existing screens', description: 'Assign a bed for a stay and release it at discharge.', items: ['Admit a patient', 'Assign an available bed', 'Discharge and bed release'] },
  { title: 'Rooms & beds', icon: BedDouble, status: 'Existing screens', description: 'Manage rooms, individual beds and availability states.', items: ['Room list and daily rate', 'Bed list by room', 'Occupancy and availability'] },
  { title: 'Billing', icon: CreditCard, status: 'Existing screens', description: 'Calculate bill charges and keep the balance visible.', items: ['Room, doctor, medicine and other charges', 'Payment status and balance', 'Invoice consolidation is unresolved'] },
  { title: 'Payments', icon: Wallet, status: 'In progress', description: 'The current baseline updates a paid amount on its bill.', items: ['Current aggregate payment update', 'Cash, card, UPI and other methods — planned', 'Separate payment history — planned'] },
  { title: 'Reports', icon: FileBarChart, status: 'Planned', description: 'Operational and financial summaries for authorized staff.', items: ['Patients and doctors', 'Appointments, admissions and bed occupancy', 'Billing, payments and revenue'] },
  { title: 'User management', icon: UserRound, status: 'Planned', description: 'An administrator workflow for staff accounts.', items: ['Roles and status', 'Account lock and unlock', 'Secure password reset'] },
  { title: 'Audit logging', icon: Workflow, status: 'Planned', description: 'A durable history of important system events.', items: ['User actions and timestamps', 'Security and administration events', 'Restricted log access'] },
  { title: 'Notifications & settings', icon: Settings2, status: 'Planned', description: 'Future staff-facing reminders and application preferences.', items: ['Appointment and stay alerts', 'Profile and preferences', 'Database health status'] },
];

export const technology = [
  { name: 'Java 17+', note: 'Desktop runtime', kind: 'current' },
  { name: 'Swing', note: 'Desktop UI', kind: 'current' },
  { name: 'FlatLaf', note: 'Look & feel', kind: 'current' },
  { name: 'Maven', note: 'Build', kind: 'current' },
  { name: 'JDBC', note: 'Data access', kind: 'current' },
  { name: 'PostgreSQL', note: 'Migration target', kind: 'target' },
  { name: 'Supabase', note: 'Managed DB option', kind: 'target' },
  { name: 'Next.js', note: 'Project website', kind: 'current' },
  { name: 'Vercel', note: 'Website hosting', kind: 'target' },
  { name: 'GitHub', note: 'Source repository', kind: 'current' },
];

export const screenshotAreas = ['Login', 'Dashboard', 'Patients', 'Doctors', 'Appointments', 'Admissions', 'Rooms', 'Beds', 'Billing', 'Payments', 'Reports', 'Users', 'Settings'];

export const projectFacts = [
  ['Project', 'Hospital Management System'],
  ['College', 'Arya College of Engineering & I.T.'],
  ['Campus', 'Kukas, Jaipur'],
  ['Academic year', '2026–27 · 5th Semester'],
  ['Project type', 'Java Project Based Learning'],
];
