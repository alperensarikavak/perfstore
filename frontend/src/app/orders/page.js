'use client'
import { useEffect, useState } from 'react';
import { useAuth } from '@/context/AuthContext';
import Link from 'next/link';

export default function OrdersPage() {
    const { user, token } = useAuth();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) return;

        async function fetchOrders() {
            try {
                const authToken = localStorage.getItem('token');
                const res = await fetch('/api/orders/my-orders', {
                    headers: {
                        'Authorization': `Bearer ${authToken}`
                    }
                });

                if (res.ok) {
                    const data = await res.json();
                    // Sort by newest first
                    setOrders(data.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt)));
                }
            } catch (e) {
                console.error("Failed to fetch orders", e);
            } finally {
                setLoading(false);
            }
        }

        fetchOrders();
    }, [user]);

    if (!user) {
        return (
            <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', flexDirection: 'column', gap: '20px' }}>
                <h2 style={{ color: 'white' }}>Siparişlerinizi görmek için giriş yapmalısınız.</h2>
                <Link href="/login" className="btn-primary">Giriş Yap</Link>
            </div>
        );
    }

    return (
        <div style={{ padding: '40px', maxWidth: '800px', margin: '0 auto' }}>
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: '30px' }}>
                <h1 className="title-gradient">Sipariş Geçmişim</h1>
                <Link href="/" style={{ color: 'var(--text-muted)', textDecoration: 'none' }}>← Alışverişe Dön</Link>
            </div>

            {loading ? (
                <div style={{ textAlign: 'center', color: 'var(--text-muted)' }}>Yükleniyor...</div>
            ) : orders.length === 0 ? (
                <div className="glass-panel" style={{ textAlign: 'center', padding: '40px' }}>
                    <p style={{ color: 'var(--text-muted)', fontSize: '1.1rem' }}>Henüz hiç sipariş vermediniz.</p>
                    <Link href="/" className="btn-primary" style={{ display: 'inline-block', marginTop: '20px' }}>Alışverişe Başla</Link>
                </div>
            ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
                    {orders.map(order => (
                        <div key={order.id} className="glass-panel" style={{ padding: '20px' }}>
                            <div style={{ display: 'flex', justifyContent: 'space-between', borderBottom: '1px solid rgba(255,255,255,0.1)', paddingBottom: '15px', marginBottom: '15px' }}>
                                <div>
                                    <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>SİPARİŞ TARİHİ</div>
                                    <div>{new Date(order.createdAt).toLocaleDateString('tr-TR')} {new Date(order.createdAt).toLocaleTimeString('tr-TR', { hour: '2-digit', minute: '2-digit' })}</div>
                                </div>
                                <div style={{ textAlign: 'right' }}>
                                    <div style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>TOPLAM TUTAR</div>
                                    <div style={{ color: 'var(--accent)', fontWeight: 'bold' }}>
                                        {new Intl.NumberFormat('tr-TR', { minimumFractionDigits: 2 }).format(order.totalAmount)} TL
                                    </div>
                                </div>
                            </div>

                            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                                {order.items.map((item, idx) => (
                                    <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                            <span style={{ background: 'rgba(255,255,255,0.1)', width: '24px', height: '24px', display: 'flex', alignItems: 'center', justifyContent: 'center', borderRadius: '4px', fontSize: '0.8rem' }}>
                                                {item.quantity}x
                                            </span>
                                            <span>{item.productName}</span>
                                        </div>
                                        <span style={{ color: 'var(--text-muted)' }}>
                                            {new Intl.NumberFormat('tr-TR', { minimumFractionDigits: 2 }).format(item.price)} TL
                                        </span>
                                    </div>
                                ))}
                            </div>

                            <div style={{ marginTop: '15px', paddingTop: '15px', borderTop: '1px solid rgba(255,255,255,0.05)', textAlign: 'right' }}>
                                <span style={{ fontSize: '0.8rem', color: '#4ade80', background: 'rgba(74, 222, 128, 0.1)', padding: '4px 8px', borderRadius: '4px' }}>
                                    Sipariş Alındı
                                </span>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}
