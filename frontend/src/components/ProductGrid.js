'use client'
import { useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import Link from 'next/link';

export default function ProductGrid({ initialProducts, categories }) {
    const { user, logout } = useAuth();
    const [searchTerm, setSearchTerm] = useState('');

    const filteredProducts = initialProducts.filter(p =>
        p.name.toLowerCase().includes(searchTerm.toLowerCase())
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
                        style={{
                            width: '100%',
                            padding: '12px 20px',
                            borderRadius: '99px',
                            border: '1px solid rgba(255,255,255,0.1)',
                            background: 'rgba(0,0,0,0.3)',
                            color: 'white',
                            fontSize: '1rem',
                            outline: 'none',
                            transition: 'all 0.2s'
                        }}
                    />
                </div>

                <div>
                    {user ? (
                        <div style={{ display: 'flex', alignItems: 'center', gap: '15px' }}>
                            <span style={{ color: 'var(--text-muted)' }}>{user.username}</span>
                            <button
                                onClick={logout}
                                style={{ background: 'rgba(239, 68, 68, 0.2)', color: '#fca5a5', border: '1px solid rgba(239, 68, 68, 0.4)', padding: '8px 16px', borderRadius: '99px', cursor: 'pointer', fontWeight: 600 }}>
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
                            <p style={{ color: 'var(--text-muted)', fontSize: '0.9rem', marginBottom: '20px', height: '40px', overflow: 'hidden', lineHeight: '1.4' }}>
                                {product.description}
                            </p>
                            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                <span style={{ color: 'var(--accent)', fontWeight: 'bold', fontSize: '1.3rem', textShadow: '0 0 20px rgba(34, 197, 94, 0.4)' }}>
                                    {new Intl.NumberFormat('tr-TR', { minimumFractionDigits: 2 }).format(product.price)} ₺
                                </span>
                                <button
                                    className="btn-primary"
                                    style={{ padding: '10px 24px', fontSize: '0.9rem' }}
                                    onClick={() => alert(`"${product.name}" satın alındı!`)}
                                >
                                    SATIN AL
                                </button>
                            </div>
                        </div>
                    </div>
                ))}
                {filteredProducts.length === 0 && (
                    <div style={{ gridColumn: '1/-1', textAlign: 'center', padding: '40px', color: 'var(--text-muted)' }}>
                        Aradığınız kriterlere uygun ürün bulunamadı.
                    </div>
                )}
            </div>
        </div>
    );
}
