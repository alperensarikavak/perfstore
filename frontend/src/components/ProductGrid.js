'use client'
import { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import { useCart } from '@/context/CartContext';
import Link from 'next/link';
import Image from 'next/image'; // 1. EKLENDİ: Resim optimizasyonu için gerekli

export default function ProductGrid({ initialProducts, categories }) {
    const { user, logout } = useAuth();
    const { addToCart, setIsCartOpen, cartCount } = useCart();
    const [searchTerm, setSearchTerm] = useState('');

    const filteredProducts = initialProducts.filter(p =>
        p.name && p.name.toLowerCase().includes(searchTerm.toLowerCase())
    );

    return (
        <div>
            {/* Header */}
            <div className="glass-panel" style={{ padding: '20px', marginBottom: '30px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '20px' }}>
                <h1 className="title-gradient" style={{ fontSize: '1.8rem', margin: 0, letterSpacing: '2px' }}>PERFSTORE</h1>

                <div style={{ flex: 1, maxWidth: '500px' }}>
                    <input
                        type="text"
                        placeholder="Ürün ara..."
                        value={searchTerm}
                        onChange={(e) => setSearchTerm(e.target.value)}
                        className="search-input" // Güncellendi
                    />
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                    <button onClick={() => setIsCartOpen(true)} className="cart-icon-btn"> {/* Güncellendi */}
                        🛒
                        {cartCount > 0 && (
                            <span className="cart-badge" style={{ position: 'absolute', top: '-5px', right: '-8px', background: 'var(--accent)', color: 'black', fontSize: '0.75rem', fontWeight: 'bold', width: '20px', height: '20px', borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                        {cartCount}
                    </span>
                        )}
                    </button>

                    {user ? (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                            <span style={{ color: 'var(--text-muted)' }}>{user.username}</span>
                            <Link href="/admin/add-product" className="btn-add-product">+ Ürün Ekle</Link> {/* Güncellendi */}
                            <Link href="/orders" className="nav-link">Siparişlerim</Link> {/* Güncellendi */}
                            <button onClick={logout} className="btn-logout">ÇIKIŞ</button> {/* Güncellendi */}
                        </div>
                    ) : (
                        <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                            <Link href="/login" className="nav-link">Giriş Yap</Link> {/* Güncellendi */}
                            <Link href="/register" className="btn-primary">Kayıt Ol</Link>
                        </div>
                    )}
                </div>
            </div>

            {/* Grid */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '30px' }}>
                {filteredProducts.map(product => (
                    <div key={product.id} className="glass-panel" style={{ overflow: 'hidden', padding: 0, transition: 'transform 0.2s' }}>

                        {/* 2. GÜNCELLENDİ: Resim Alanı */}
                        <div style={{ height: '220px', position: 'relative', background: 'rgba(255,255,255,0.03)' }}>
                            {product.imageUrl ? (
                                // Cloudinary Resmi Varsa Göster
                                <Image
                                    src={product.imageUrl}
                                    alt={product.name}
                                    fill // Resmi kutuya doldur
                                    style={{ objectFit: 'cover' }} // Resmi bozmadan kırp
                                    sizes="(max-width: 768px) 100vw, (max-width: 1200px) 50vw, 33vw"
                                />
                            ) : (
                                // Resim Yoksa (Fallback)
                                <div style={{ height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', flexDirection: 'column', color: 'var(--text-muted)' }}>
                                    <div style={{ fontSize: '3rem', marginBottom: '10px' }}>📦</div>
                                    <div>{product.categoryName || 'Ürün'}</div>
                                </div>
                            )}
                        </div>

                        <div style={{ padding: '24px' }}>
                            <h3 style={{ fontSize: '1.2rem', marginBottom: '8px', fontWeight: 600 }}>{product.name}</h3>

                            {/* Stock Display */}
                            <div style={{ marginBottom: '8px', fontSize: '0.85rem' }}>
                                <span style={{
                                    color: product.stockQuantity > 0 ? '#4ade80' : '#ef4444',
                                    background: product.stockQuantity > 0 ? 'rgba(74, 222, 128, 0.1)' : 'rgba(239, 68, 68, 0.1)',
                                    padding: '4px 8px',
                                    borderRadius: '4px',
                                    fontWeight: 500
                                }}>
                                    {product.stockQuantity > 0 ? `Stokta: ${product.stockQuantity} Adet` : 'Stok Tükendi'}
                                </span>
                            </div>

                            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '20px', height: '40px', overflow: 'hidden', lineHeight: '1.4' }}>
                                {product.description}
                            </p>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <span style={{ color: 'var(--accent)', fontWeight: 'bold', fontSize: '1.3rem', textShadow: '0 0 20px rgba(34, 197, 94, 0.4)' }}>
                                    {new Intl.NumberFormat('tr-TR', { minimumFractionDigits: 2 }).format(product.price)} ₺
                                </span>

                                {/* 3. GÜNCELLENDİ: onClick Event'i eklendi */}
                                <button
                                    className="btn-primary"
                                    onClick={() => product.stockQuantity > 0 && addToCart(product)}
                                    style={{
                                        padding: '10px 24px',
                                        fontSize: '0.9rem',
                                        opacity: product.stockQuantity > 0 ? 1 : 0.5,
                                        cursor: product.stockQuantity > 0 ? 'pointer' : 'not-allowed',
                                        filter: product.stockQuantity > 0 ? 'none' : 'grayscale(100%)'
                                    }}
                                >
                                    {product.stockQuantity > 0 ? 'SEPETE EKLE' : 'TÜKENDİ'}
                                </button>
                            </div>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
}