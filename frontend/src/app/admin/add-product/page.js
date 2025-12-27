'use client'
import { useForm } from 'react-hook-form';
import { useState, useEffect } from 'react';
import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/navigation';
import Link from 'next/link';

export default function AddProductPage() {
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
    const { user } = useAuth();
    const router = useRouter();
    const [categories, setCategories] = useState([]);
    const [serverError, setServerError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');

    // Fetch categories for the dropdown
    useEffect(() => {
        async function fetchCategories() {
            try {
                const res = await fetch('/api/categories');
                if (res.ok) {
                    const data = await res.json();
                    setCategories(data);
                }
            } catch (e) {
                console.error("Failed to load categories", e);
            }
        }
        fetchCategories();
    }, []);

    const onSubmit = async (data) => {
        setServerError('');
        setSuccessMessage('');

        // Convert numeric fields
        const payload = {
            ...data,
            price: parseFloat(data.price),
            stockQuantity: parseInt(data.stockQuantity),
            // No image URL for now as per backend design
        };

        try {
            // Retrieve token from localStorage manually if needed, or rely on AuthContext if it exposes it.
            // Assuming AuthContext attaches header or we get it from localStorage directly for this simple implementation.
            const token = localStorage.getItem('token');

            const res = await fetch('/api/products', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${token}`
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                const errorData = await res.json();
                throw new Error(errorData.message || 'Ürün eklenirken bir hata oluştu');
            }

            setSuccessMessage('Ürün başarıyla eklendi! Yönlendiriliyorsunuz...');
            setTimeout(() => {
                router.push('/');
            }, 2000);

        } catch (e) {
            setServerError(e.message);
        }
    };

    return (
        <div style={{ display: 'flex', flexDirection: 'column' , alignItems: 'center', justifyContent: 'center', minHeight: '100vh', padding: '20px' }}>

            <div style={{ marginBottom: '20px', textAlign: 'center' }}>
                <Link href="/" className="link-back">
                    Ana Sayfaya Dön
                </Link>
            </div>

            <div className="glass-panel" style={{ padding: '40px', width: '100%', maxWidth: '600px' }}>
                <h1 className="title-gradient" style={{ fontSize: '2rem', marginBottom: '20px', textAlign: 'center' }}>
                    YENİ ÜRÜN EKLE
                </h1>

                <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                        <div>
                            <label style={{ display: 'block', marginBottom: '5px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Ürün Adı</label>
                            <input
                                {...register('name', { required: 'Ürün adı gerekli' })}
                                placeholder="Örn: Sony XM5"
                                className="form-input" // Efekt eklendi
                            />
                            {errors.name && <span style={errorStyle}>{errors.name.message}</span>}
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '5px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>SKU (Stok Kodu)</label>
                            <input
                                {...register('sku', { required: 'SKU gerekli' })}
                                placeholder="Örn: SONY-WH-1000XM5"
                                className="form-input" // Efekt eklendi
                            />
                            {errors.sku && <span style={errorStyle}>{errors.sku.message}</span>}
                        </div>
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '5px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Açıklama</label>
                        <textarea
                            {...register('description')}
                            placeholder="Ürün açıklaması..."
                            rows={3}
                            className="form-input" // Efekt eklendi
                            style={{ resize: 'vertical' }}
                        />
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                        <div>
                            <label style={{ display: 'block', marginBottom: '5px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Fiyat (TL)</label>
                            <input
                                type="number"
                                step="0.01"
                                {...register('price', { required: 'Fiyat gerekli', min: 0 })}
                                placeholder="0.00"
                                className="form-input" // Efekt eklendi
                            />
                            {errors.price && <span style={errorStyle}>{errors.price.message}</span>}
                        </div>

                        <div>
                            <label style={{ display: 'block', marginBottom: '5px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Stok Adedi</label>
                            <input
                                type="number"
                                {...register('stockQuantity', { required: 'Stok gerekli', min: 0 })}
                                placeholder="0"
                                className="form-input" // Efekt eklendi
                            />
                            {errors.stockQuantity && <span style={errorStyle}>{errors.stockQuantity.message}</span>}
                        </div>
                    </div>

                    <div>
                        <label style={{ display: 'block', marginBottom: '5px', color: 'var(--text-muted)', fontSize: '0.9rem' }}>Kategori</label>
                        <select
                            {...register('categoryId', { required: 'Kategori seçilmeli' })}
                            className="form-input" // Efekt eklendi
                        >
                            <option value="">Kategori Seçiniz</option>
                            {categories.map(cat => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                        {errors.categoryId && <span style={errorStyle}>{errors.categoryId.message}</span>}
                    </div>

                    {serverError && (
                        <div style={{ padding: '10px', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.2)', borderRadius: '8px', color: '#fca5a5', fontSize: '0.9rem', textAlign: 'center' }}>
                            {serverError}
                        </div>
                    )}

                    {successMessage && (
                        <div style={{ padding: '10px', background: 'rgba(34, 197, 94, 0.1)', border: '1px solid rgba(34, 197, 94, 0.2)', borderRadius: '8px', color: '#4ade80', fontSize: '0.9rem', textAlign: 'center' }}>
                            {successMessage}
                        </div>
                    )}

                    <button type="submit" className="btn-primary" disabled={isSubmitting} style={{ marginTop: '10px' }}>
                        {isSubmitting ? 'Kaydediliyor...' : 'ÜRÜNÜ KAYDET'}
                    </button>

                </form>
            </div>
        </div>
    );
}

const inputStyle = {
    width: '100%',
    padding: '12px',
    borderRadius: '8px',
    border: '1px solid rgba(255,255,255,0.1)',
    background: 'rgba(0,0,0,0.2)',
    color: 'white',
    outline: 'none',
    fontFamily: 'inherit'
};

const errorStyle = { color: '#ef4444', fontSize: '0.8rem', marginTop: '4px', display: 'block' };
