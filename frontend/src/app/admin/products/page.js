'use client'
import { useEffect, useState } from 'react';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useAuth } from '@/context/AuthContext';

export default function AdminProductsPage() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [actionMsg, setActionMsg] = useState('');
  const router = useRouter();
  const { user } = useAuth();

  useEffect(() => {
    fetchProducts();
  }, []);

  async function fetchProducts() {
    setLoading(true);
    setError('');
    try {
      const res = await fetch('http://localhost:8080/api/products', { cache: 'no-store' });
      if (!res.ok) throw new Error('Ürünler yüklenirken hata oluştu');
      const data = await res.json();
      setProducts(data);
    } catch (e) {
      console.error(e);
      setError('Ürünler yüklenemedi');
    } finally {
      setLoading(false);
    }
  }

  async function handleDelete(id) {
    const ok = window.confirm('Bu ürünü gerçekten silmek istiyor musunuz?');
    if (!ok) return;

    setActionMsg('Siliniyor...');
    const token = localStorage.getItem('token');
    try {
      const headers = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const res = await fetch(`http://localhost:8080/api/products/${id}`, {
        method: 'DELETE',
        headers
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Silme işleminde hata');
      }

      setProducts(prev => prev.filter(p => p.id !== id));
      setActionMsg('Ürün silindi.');
    } catch (e) {
      console.error(e);
      setActionMsg('Silme başarısız: ' + (e.message || e));
    } finally {
      setTimeout(() => setActionMsg(''), 3000);
    }
  }

  return (
      <div style={{ padding: '40px', maxWidth: '1100px', margin: '0 auto' }}>

        {/* ÜST ORTA: ANA SAYFAYA DÖN BUTONU */}
        <div style={{ display: 'flex', justifyContent: 'center', marginBottom: '30px' }}>
          <Link href="/" className="link-back">
            Ana Sayfaya Dön
          </Link>
        </div>

        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
          <h1 className="title-gradient">Ürünler (Admin)</h1>
          <div>
            <Link href="/admin/add-product">
              <button className="btn-primary">Yeni Ürün Ekle</button>
            </Link>
          </div>
        </div>

        {/* Mesajlar */}
        {actionMsg && <div style={{ marginBottom: '15px', color: '#4ade80', background: 'rgba(74, 222, 128, 0.1)', padding: '10px', borderRadius: '8px' }}>{actionMsg}</div>}
        {error && <div style={{ marginBottom: '15px', color: '#ef4444', background: 'rgba(239, 68, 68, 0.1)', padding: '10px', borderRadius: '8px' }}>{error}</div>}

        {loading ? (
            <div style={{ textAlign: 'center', padding: '50px', color: 'var(--text-muted)' }}>Yükleniyor...</div>
        ) : (
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse' }}>
                <thead>
                <tr style={{ textAlign: 'left', borderBottom: '1px solid rgba(255,255,255,0.06)' }}>
                  <th style={{ padding: '12px', color: 'var(--text-muted)' }}>Görsel</th>
                  <th style={{ padding: '12px', color: 'var(--text-muted)' }}>Ad</th>
                  <th style={{ padding: '12px', color: 'var(--text-muted)' }}>Kategori</th>
                  <th style={{ padding: '12px', color: 'var(--text-muted)' }}>Fiyat</th>
                  <th style={{ padding: '12px', color: 'var(--text-muted)' }}>Stok</th>
                  <th style={{ padding: '12px', textAlign: 'center', color: 'var(--text-muted)' }}>İşlemler</th>
                </tr>
                </thead>
                <tbody>
                {products.map(prod => (
                    <tr key={prod.id} style={{ borderBottom: '1px solid rgba(255,255,255,0.03)' }} className="table-row-hover">
                      <td style={{ padding: '12px', width: '80px' }}>
                        {prod.imageUrl ? (
                            <img src={prod.imageUrl} alt={prod.name} style={{ width: '50px', height: '50px', objectFit: 'cover', borderRadius: '8px' }} />
                        ) : (
                            <div style={{ width: '50px', height: '50px', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'rgba(255,255,255,0.03)', borderRadius: '8px', fontSize: '1.2rem' }}>📦</div>
                        )}
                      </td>
                      <td style={{ padding: '12px', fontWeight: '500' }}>{prod.name}</td>
                      <td style={{ padding: '12px' }}><span style={{ background: 'rgba(255,255,255,0.05)', padding: '4px 8px', borderRadius: '4px', fontSize: '0.8rem' }}>{prod.categoryName || '-'}</span></td>
                      <td style={{ padding: '12px', color: 'var(--accent)', fontWeight: 'bold' }}>{prod.price?.toFixed(2)} ₺</td>
                      <td style={{ padding: '12px' }}>{prod.stockQuantity}</td>
                      <td style={{ padding: '12px' }}>
                        <div style={{ display: 'flex', gap: '10px', justifyContent: 'center' }}>
                          <Link href={`/admin/edit-product/${prod.id}`}>
                            <button className="btn-secondary">Düzenle</button>
                          </Link>
                          <button className="btn-danger" onClick={() => handleDelete(prod.id)}>Sil</button>
                        </div>
                      </td>
                    </tr>
                ))}
                </tbody>
              </table>

              {products.length === 0 && <div style={{ textAlign: 'center', marginTop: '30px', color: '#9ca3af' }}>Henüz listelenecek bir ürün bulunmuyor.</div>}
            </div>
        )}
      </div>
  );
}
