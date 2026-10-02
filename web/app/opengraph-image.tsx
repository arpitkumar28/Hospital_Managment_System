import { ImageResponse } from 'next/og';

export const alt = 'Hospital Management System — Healthcare Operations Platform';
export const size = { width: 1200, height: 630 };
export const contentType = 'image/png';

export default function OpenGraphImage() {
  return new ImageResponse(
    <div style={{ width: '100%', height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: 72, color: '#102c42', background: 'linear-gradient(135deg,#f8fcfd 0%,#e7f4f4 55%,#d8eeea 100%)', fontFamily: 'Arial' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 16, fontSize: 25, fontWeight: 700 }}><div style={{ display: 'flex', width: 50, height: 50, alignItems: 'center', justifyContent: 'center', borderRadius: 14, color: 'white', background: '#087f8c', fontSize: 34 }}>+</div>Hospital Management System</div>
      <div style={{ display: 'flex', flexDirection: 'column' }}><div style={{ fontSize: 62, fontWeight: 700, letterSpacing: -3, lineHeight: 1.08 }}>Hospital Management System</div><div style={{ marginTop: 14, color: '#087f8c', fontSize: 42, fontWeight: 600 }}>Healthcare Operations Platform</div><div style={{ marginTop: 25, color: '#526a7a', fontSize: 22 }}>Connected workflows for modern hospital operations</div></div>
      <div style={{ display: 'flex', justifyContent: 'space-between', color: '#587181', fontSize: 18 }}><span>JAVA SWING · JDBC · POSTGRESQL</span><span>HEALTHCARE OPERATIONS PLATFORM</span></div>
    </div>,
    { ...size },
  );
}
