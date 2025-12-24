import ProductGrid from '@/components/ProductGrid';

export const dynamic = 'force-dynamic';

async function getProducts() {
  try {
    const res = await fetch('http://localhost:8080/api/products', { cache: 'no-store' });
    if (!res.ok) return [];
    return res.json();
  } catch (e) {
    console.error("Backend fetch error:", e);
    return [];
  }
}

async function getCategories() {
  try {
    const res = await fetch('http://localhost:8080/api/categories', { cache: 'no-store' });
    if (!res.ok) return [];
    return res.json();
  } catch (e) {
    return [];
  }
}

export default async function Home() {
  const products = await getProducts();
  const categories = await getCategories();

  return (
    <main style={{ minHeight: '100vh', padding: '40px 20px', maxWidth: '1400px', margin: '0 auto' }}>
      <ProductGrid initialProducts={products} categories={categories} />
    </main>
  );
}
