'use client'
import { useForm } from 'react-hook-form';
import { useState, useEffect, useRef } from 'react'; // 1. useRef EKLENDİ
import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/navigation';
import Link from "next/link";

export default function AddProductPage() {
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
    const { user } = useAuth();
    const router = useRouter();
    
    // useRef Tanımı
    const fileInputRef = useRef(null); // 2. Referans oluşturuldu

    // State tanımları
    const [categories, setCategories] = useState([]);
    const [serverError, setServerError] = useState('');
    const [successMessage, setSuccessMessage] = useState('');
    const [selectedImage, setSelectedImage] = useState(null); 
    const [imagePreview, setImagePreview] = useState(null);   

    // Kategorileri çek
   useEffect(() => {
        async function fetchCategories() {
            try {
                // Token'ı alıyoruz (opsiyonel, backend public olabilir)
                const token = localStorage.getItem('token');
                const headers = {};
                if (token) headers['Authorization'] = `Bearer ${token}`;

                const res = await fetch('http://localhost:8080/api/categories', { headers });

                if (res.ok) {
                    const data = await res.json();
                    setCategories(data);
                } else {
                    if (res.status === 401 || res.status === 403) {
                        console.error("Kategoriler çekilemedi, Yetki yok.");
                        setServerError('Kategoriler çekilemedi: yetki hatası.');
                    } else {
                        const text = await res.text();
                        console.error("Kategoriler çekilemedi", text);
                        setServerError('Kategoriler yüklenemedi.');
                    }
                }
            } catch (e) {
                console.error("Kategoriler yüklenemedi", e);
                setServerError('Kategoriler yüklenemedi: ' + (e.message || e));
            }
        }
        fetchCategories();
    }, []);

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            setSelectedImage(file);
            setImagePreview(URL.createObjectURL(file));
        }
    };

    const onSubmit = async (data) => {
        setServerError('');
        setSuccessMessage('');

        const token = localStorage.getItem('token'); 

        const formData = new FormData();

        const productData = {
            name: data.name,
            description: data.description,
            sku: data.sku,
            price: parseFloat(data.price),
            stockQuantity: parseInt(data.stockQuantity),
            categoryId: data.categoryId 
        };

        formData.append('product', new Blob([JSON.stringify(productData)], { type: 'application/json' }));

        if (selectedImage) {
            formData.append('image', selectedImage);
        }

        try {
            const res = await fetch('http://localhost:8080/api/products', {
                method: 'POST',
                headers: {
                    'Authorization': `Bearer ${token}`
                },
                body: formData
            });

            if (!res.ok) {
                const errorData = await res.text();
                throw new Error(errorData || 'Ürün eklenirken hata oluştu');
            }

            setSuccessMessage('Ürün ve Görsel başarıyla yüklendi! Yönlendiriliyorsunuz...');
            
            setTimeout(() => {
                router.push('/admin/products'); 
            }, 2000);

        } catch (e) {
            console.error(e);
            setServerError(e.message);
        }
    };

    return (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh', padding: '20px' }}>
            <div className="glass-panel" style={{ padding: '40px', width: '100%', maxWidth: '600px' }}>
                <h1 className="title-gradient" style={{ fontSize: '2rem', marginBottom: '20px', textAlign: 'center' }}>
                    YENİ ÜRÜN EKLE
                </h1>

                <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>

                    <div style={{ textAlign: 'center', marginBottom: '10px' }}>

                        <div 
                            onClick={() => fileInputRef.current.click()} 
                            style={{ display: 'block', marginBottom: '8px', color: 'var(--text-muted)', cursor: 'pointer' }}
                        >
                            {imagePreview ? (
                                <img 
                                    src={imagePreview} 
                                    alt="Önizleme" 
                                    style={{ width: '100px', height: '100px', objectFit: 'cover', borderRadius: '10px', margin: '0 auto', border: '2px solid rgba(255,255,255,0.2)' }}
                                />
                            ) : (
                                <div style={{ width: '100px', height: '100px', background: 'rgba(255,255,255,0.1)', borderRadius: '10px', margin: '0 auto', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '2px dashed rgba(255,255,255,0.3)' }}>
                                    <span style={{ fontSize: '2rem', color: 'rgba(255,255,255,0.5)' }}>+</span>
                                </div>
                            )}
                            <div style={{ marginTop: '5px', fontSize: '0.8rem', color: '#aaa' }}>Ürün Görseli Seç</div>
                        </div>

                        {/* 4. Gizli input'a ref eklendi */}
                        <input 
                            type="file" 
                            accept="image/*"
                            ref={fileInputRef}
                            onChange={handleFileChange}
                            style={{ display: 'none' }} 
                        />
                    </div>
                    {/* --- Resim Alanı Bitiş --- */}

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                        <div>
                            <label style={labelStyle}>Ürün Adı</label>
                            <input
                                {...register('name', { required: 'Ürün adı gerekli' })}
                                placeholder="Örn: Sony XM5"
                                style={inputStyle}
                            />
                            {errors.name && <span style={errorStyle}>{errors.name.message}</span>}
                        </div>

                        <div>
                            <label style={labelStyle}>SKU (Stok Kodu)</label>
                            <input
                                {...register('sku', { required: 'SKU gerekli' })}
                                placeholder="Örn: SONY-WH-XM5"
                                style={inputStyle}
                            />
                            {errors.sku && <span style={errorStyle}>{errors.sku.message}</span>}
                        </div>
                    </div>

                    <div>
                        <label style={labelStyle}>Açıklama</label>
                        <textarea
                            {...register('description')}
                            placeholder="Ürün açıklaması..."
                            rows={3}
                            style={{ ...inputStyle, resize: 'vertical' }}
                        />
                    </div>

                    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '15px' }}>
                        <div>
                            <label style={labelStyle}>Fiyat (TL)</label>
                            <input
                                type="number"
                                step="0.01"
                                {...register('price', { required: 'Fiyat gerekli', min: 0 })}
                                placeholder="0.00"
                                style={inputStyle}
                            />
                            {errors.price && <span style={errorStyle}>{errors.price.message}</span>}
                        </div>

                        <div>
                            <label style={labelStyle}>Stok Adedi</label>
                            <input
                                type="number"
                                {...register('stockQuantity', { required: 'Stok gerekli', min: 0 })}
                                placeholder="0"
                                style={inputStyle}
                            />
                            {errors.stockQuantity && <span style={errorStyle}>{errors.stockQuantity.message}</span>}
                        </div>
                    </div>

                    <div>
                        <label style={labelStyle}>Kategori</label>
                        <select
                            {...register('categoryId', { required: 'Kategori seçilmeli' })}
                            style={inputStyle}
                        >
                            <option value="">Kategori Seçiniz</option>
                            {categories.map(cat => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                        {categories.length === 0 && <div style={{ fontSize: '0.9rem', color: '#aaa', marginTop: '6px' }}>Kategoriler yüklenemedi veya boş.</div>}
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

                    <div style={{ display: 'flex', justifyContent: 'center' }}>
                        <Link href="/" className="link-back">
                            Ana Sayfaya Dön
                        </Link>
                    </div>
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

const labelStyle = { 
    display: 'block', 
    marginBottom: '5px', 
    color: 'var(--text-muted)', 
    fontSize: '0.9rem' 
};

const errorStyle = { color: '#ef4444', fontSize: '0.8rem', marginTop: '4px', display: 'block' };