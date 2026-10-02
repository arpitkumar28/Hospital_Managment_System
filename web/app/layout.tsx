import type { Metadata } from 'next';
import { Footer } from '@/components/footer';
import { Navbar } from '@/components/navbar';
import './globals.css';

export const metadata: Metadata = {
  title: {
    default: 'Hospital Management System | Healthcare Operations Platform',
    template: '%s | Hospital Management System',
  },
  description: 'Professional hospital management software for managing patients, doctors, appointments, admissions, beds, billing and payments.',
  applicationName: 'Hospital Management System',
  openGraph: {
    type: 'website',
    siteName: 'Hospital Management System',
    title: 'Hospital Management System | Healthcare Operations Platform',
    description: 'Professional hospital operations software for connected patient, clinical, administrative and financial workflows.',
  },
  icons: { icon: '/favicon.svg' },
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en" data-scroll-behavior="smooth">
      <body>
        <a className="skip-link" href="#main">Skip to content</a>
        <Navbar />
        {children}
        <Footer />
      </body>
    </html>
  );
}
