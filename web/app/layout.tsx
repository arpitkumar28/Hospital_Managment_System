import type { Metadata } from 'next';
import { Footer } from '@/components/footer';
import { Navbar } from '@/components/navbar';
import './globals.css';

export const metadata: Metadata = {
  title: {
    default: 'Hospital Management System | Arya College Jaipur',
    template: '%s | Hospital Management System',
  },
  description: 'A Java Swing hospital management project for Arya College of Engineering & I.T., Kukas, Jaipur. Explore modules, architecture, database and project progress.',
  applicationName: 'Hospital Management System Project',
  openGraph: {
    type: 'website',
    siteName: 'Hospital Management System Project',
    title: 'Hospital Management System | Arya College Jaipur',
    description: 'A Java desktop hospital management project and its technical architecture.',
  },
  icons: { icon: '/favicon.svg' },
  twitter: {
    card: 'summary_large_image',
    title: 'Hospital Management System | Arya College Jaipur',
    description: 'A Java desktop hospital management project and its technical architecture.',
  },
};

export default function RootLayout({ children }: Readonly<{ children: React.ReactNode }>) {
  return (
    <html lang="en">
      <body>
        <a className="skip-link" href="#main">Skip to content</a>
        <Navbar />
        {children}
        <Footer />
      </body>
    </html>
  );
}
