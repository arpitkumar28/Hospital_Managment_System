import type { MetadataRoute } from 'next';

const baseRoutes = ['/', '/product', '/features', '/modules', '/screenshots', '/security', '/architecture', '/database', '/docs', '/team', '/download', '/contact'];

export default function sitemap(): MetadataRoute.Sitemap {
  const host = process.env.VERCEL_PROJECT_PRODUCTION_URL ?? process.env.VERCEL_URL;
  if (!host) return [];
  const origin = `https://${host}`;
  return baseRoutes.map((route) => ({ url: new URL(route, origin).toString(), changeFrequency: 'monthly', priority: route === '/' ? 1 : 0.7 }));
}
