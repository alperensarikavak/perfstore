'use client'
import { useEffect, useState, useRef } from 'react';
import { useRouter, useParams } from 'next/navigation';

export default function EditProductPage() {
  const params = useParams();
  const id = params.id;
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [serverMsg, setServerMsg] = useState('');
  const [product, setProduct] = useState(null);
  const [selectedImage, setSelectedImage] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const fileRef = useRef(null);
  const router = useRouter();

  useEffect(() => {
    fetchProduct();
  }, [id]);

  async function fetchProduct() {
    setLoading(true);
    setError('');
    try {
      const res = await fetch(`http://localhost:8080/api/products/${id}`, { cache: 'no-store' });
      if (!res.ok) throw new Error('Ürün yüklenemedi');
      const data = await res.json();
      setProduct(data);
      setImagePreview(data.imageUrl || null);
    } catch (e) {
      console.error(e);
      setError('Ürün yüklenemedi');
    } finally {
      setLoading(false);
    }
  }

  function handleFileChange(e) {
    const file = e.target.files[0];
    if (file) {
      setSelectedImage(file);
      setImagePreview(URL.createObjectURL(file));
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setServerMsg('Kaydediliyor...');
    const token = localStorage.getItem('token');

    const formData = new FormData();
    const productData = {
      name: e.target.name.value,
      description: e.target.description.value,
      price: parseFloat(e.target.price.value),
      stockQuantity: parseInt(e.target.stockQuantity.value)
    };

    formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));
    if (selectedImage) formData.append('image', selectedImage);

    try {
      const headers = {};
      if (token) headers['Authorization'] = `Bearer ${token}`;

      const res = await fetch(`http://localhost:8080/api/products/${id}`, {
        method: 'PUT',
        headers,
        body: formData
      });

      if (!res.ok) {
        const text = await res.text();
        throw new Error(text || 'Güncelleme başarısız');
      }

      setServerMsg('Güncellendi');
      setTimeout(() => router.push('/admin/products'), 1200);
    } catch (e) {
      console.error(e);
      setServerMsg('Güncelleme başarısız: ' + (e.message || e));
    }
  }

  if (loading) return <div style={{ padding: '40px' }}>Yükleniyor...</div>;
  if (error) return <div style={{ padding: '40px', color: '#ef4444' }}>{error}</div>;

  return (
    <div style={{ padding: '40px', maxWidth: '700px', margin: '0 auto' }}>
      <h1 className="title-gradient">Ürün Düzenle</h1>
      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: '12px', marginTop: '20px' }}>
        <div style={{ display: 'flex', gap: '12px' }}>
          <div style={{ width: '120px' }} onClick={() => fileRef.current?.click()}>
            {imagePreview ? (
              // eslint-disable-next-line @next/next/no-img-element
              <img src={imagePreview} alt="preview" style={{ width: '120px', height: '120px', objectFit: 'cover', borderRadius: '8px' }} />
            ) : (
              <div style={{ width: '120px', height: '120px', display: 'flex', alignItems: 'center', justifyContent: 'center', background: 'rgba(255,255,255,0.03)', borderRadius: '8px' }}>📦</div>
            )}
            <div style={{ fontSize: '0.85rem', color: '#9ca3af', marginTop: '8px' }}>Görseli değişmek için tıklayın</div>
            <input type="file" accept="image/*" ref={fileRef} onChange={handleFileChange} style={{ display: 'none' }} />
          </div>

          <div style={{ flex: 1 }}>
            <label style={{ display: 'block', marginBottom: '6px', color: 'var(--text-muted)' }}>Ürün Adı</label>
            <input name="name" defaultValue={product.name} style={{ width: '100%', padding: '10px', borderRadius: '6px' }} />

            <label style={{ display: 'block', marginTop: '10px', marginBottom: '6px', color: 'var(--text-muted)' }}>Açıklama</label>
            <textarea name="description" defaultValue={product.description} rows={3} style={{ width: '100%', padding: '10px', borderRadius: '6px' }} />

            <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
              <div style={{ flex: 1 }}>
                <label style={{ display: 'block', marginBottom: '6px', color: 'var(--text-muted)' }}>Fiyat</label>
                <input name="price" type="number" step="0.01" defaultValue={product.price} style={{ width: '100%', padding: '10px', borderRadius: '6px' }} />
              </div>

              <div style={{ width: '160px' }}>
                <label style={{ display: 'block', marginBottom: '6px', color: 'var(--text-muted)' }}>Stok</label>
                <input name="stockQuantity" type="number" defaultValue={product.stockQuantity} style={{ width: '100%', padding: '10px', borderRadius: '6px' }} />
              </div>
            </div>

            <div style={{ marginTop: '12px' }}>
              <button className="btn-primary" type="submit">Güncelle</button>
              <button type="button" className="btn-secondary" style={{ marginLeft: '8px' }} onClick={() => router.push('/admin/products')}>İptal</button>
            </div>

            {serverMsg && <div style={{ marginTop: '10px', color: '#6b7280' }}>{serverMsg}</div>}
          </div>
        </div>
      </form>
    </div>
  );
}
