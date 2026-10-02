export const repositoryUrl = 'https://github.com/arpitkumar28/Hospital_Managment_System';

export const routes = [
  { label: 'Home', href: '/' },
  { label: 'Product', href: '/product' },
  { label: 'Features', href: '/features' },
  { label: 'Modules', href: '/modules' },
  { label: 'Screenshots', href: '/screenshots' },
  { label: 'Security', href: '/security' },
  { label: 'Architecture', href: '/architecture' },
  { label: 'Documentation', href: '/docs' },
  { label: 'About', href: '/team' },
  { label: 'Availability', href: '/download' },
  { label: 'Contact', href: '/contact' },
] as const;

export type SitePath = (typeof routes)[number]['href'];

export const plannedModules = ['Patients', 'Doctors', 'Appointments', 'Admissions', 'Rooms & Beds', 'Billing & Payments', 'Reports', 'User Management', 'Settings', 'Notifications', 'Audit Logs'];
