'use client'
import { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import { useCart } from '@/context/CartContext';
import Link from 'next/link';

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
                        className="search-input"
                    />
                </div>

                <div style={{ display: 'flex', alignItems: 'center', gap: '20px' }}>
                    {/* Cart Icon */}
                    <button
                        onClick={() => setIsCartOpen(true)}
                        className="cart-icon-btn"
                    >
                        🛒
                        {cartCount > 0 && (
                            <span style={{
                                position: 'absolute', top: '-5px', right: '-8px',
                                background: 'var(--accent)', color: 'black',
                                fontSize: '0.75rem', fontWeight: 'bold',
                                width: '20px', height: '20px', borderRadius: '50%',
                                display: 'flex', alignItems: 'center', justifyContent: 'center',
                                pointerEvents: 'none' // Sayının hover etkisini bozmaması için
                            }}>
                         {cartCount}
                     </span>
                        )}
                    </button>

                    {user ? (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                            <span style={{ color: 'var(--text-muted)' }}>{user.username}</span>
                            <Link href="/admin/add-product" className="btn-add-product">
                                + Ürün Ekle
                            </Link>
                            <Link href="/orders" className="link-orders">
                                Siparişlerim
                            </Link>
                            <button onClick={logout} className="btn-logout">
                                ÇIKIŞ
                            </button>
                        </div>
                    ) : (
                        <div style={{ display: 'flex', gap: '15px' }}>
                            <Link href="/login" style={{ color: 'white', paddingTop: '10px' }}>Giriş Yap</Link>
                            <Link href="/register" className="btn-primary">Kayıt Ol</Link>
                        </div>
                    )}
                </div>
            </div>

            {/* Grid */}
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '30px' }}>
                {filteredProducts.map(product => (
                    <div key={product.id} className="glass-panel" style={{ overflow: 'hidden', padding: 0, transition: 'transform 0.2s' }}>
                        <div style={{ height: '220px', background: 'rgba(255,255,255,0.03)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                            {/* Fallback Image */}
                            <div style={{ color: 'var(--text-muted)', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                                <div style={{ fontSize: '3rem', marginBottom: '10px' }}>📦</div>
                                <div>{product.categoryName || 'Ürün'}</div>
                            </div>
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
                                <button
                                    className="btn-primary"
                                    style={{
                                        padding: '10px 24px',
                                        fontSize: '0.9rem',
                                        opacity: product.stockQuantity > 0 ? 1 : 0.5,
                                        cursor: product.stockQuantity > 0 ? 'pointer' : 'not-allowed',
                                        filter: product.stockQuantity > 0 ? 'none' : 'grayscale(100%)'
                                    }}
                                    onClick={() => product.stockQuantity > 0 && addToCart(product)}
                                    disabled={product.stockQuantity <= 0}
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
