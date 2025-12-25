'use client'
import { useState, useEffect } from 'react';
import { useCart } from '@/context/CartContext';
import { useAuth } from '@/context/AuthContext';
import { useRouter } from 'next/navigation';

export default function CheckoutPage() {
    const { cart, cartTotal, clearCart } = useCart();
    const { user, token } = useAuth();
    const router = useRouter();

    const [address, setAddress] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);

    useEffect(() => {
        if (!user && !localStorage.getItem('token')) {
            router.push('/login');
        }
    }, [user, router]);

    if (cart.length === 0) {
        return (
            <div style={{ textAlign: 'center', padding: '50px', color: 'white' }}>
                <h2>Sepetiniz boş.</h2>
                <button
                    onClick={() => router.push('/')}
                    className="btn-primary"
                    style={{ marginTop: '20px' }}>
                    Alışverişe Başla
                </button>
            </div>
        );
    }

    const handleOrderSubmit = async (e) => {
        e.preventDefault();
        if (!address.trim()) {
            alert("Lütfen teslimat adresi giriniz.");
            return;
        }

        setIsSubmitting(true);
        try {
            const payload = {
                items: cart.map(item => ({
                    productId: item.product.id,
                    quantity: item.quantity
                })),
                shippingAddress: address,
                paymentMethod: "CASH_ON_DELIVERY"
            };

            const authToken = localStorage.getItem('token');
            const res = await fetch('/api/orders', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${authToken}`
                },
                body: JSON.stringify(payload)
            });

            if (!res.ok) {
                let errorMessage = 'Sipariş oluşturulamadı';
                try {
                    const errorText = await res.text();
                    if (errorText) {
                        try {
                            const errorJson = JSON.parse(errorText);
                            errorMessage = errorJson.message || errorMessage;
                        } catch {
                            errorMessage = errorText;
                        }
                    } else {
                        errorMessage += ` (${res.status} ${res.statusText})`;
                    }
                } catch (e) {
                    errorMessage += ` (${res.status} ${res.statusText})`;
                }
                throw new Error(errorMessage);
            }

            alert("Siparişiniz başarıyla alındı! 🎉");
            clearCart();
            router.push('/orders');

        } catch (error) {
            alert("Hata: " + error.message);
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div style={{ padding: '40px', maxWidth: '800px', margin: '0 auto', color: 'white' }}>
            <h1 className="title-gradient" style={{ marginBottom: '30px' }}>Ödeme ve Teslimat</h1>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '40px' }}>
                {/* Left: Form */}
                <div className="glass-panel" style={{ padding: '30px' }}>
                    <h3 style={{ marginBottom: '20px' }}>Teslimat Bilgileri</h3>
                    <form onSubmit={handleOrderSubmit}>
                        <div style={{ marginBottom: '20px' }}>
                            <label style={{ display: 'block', marginBottom: '10px', color: 'var(--text-muted)' }}>Adres</label>
                            <textarea
                                value={address}
                                onChange={(e) => setAddress(e.target.value)}
                                placeholder="Açık adresinizi giriniz..."
                                rows="5"
                                style={{
                                    width: '100%',
                                    padding: '15px',
                                    borderRadius: '12px',
                                    border: '1px solid rgba(255,255,255,0.1)',
                                    background: 'rgba(0,0,0,0.3)',
                                    color: 'white',
                                    resize: 'vertical'
                                }}
                                required
                            />
                        </div>

                        <div style={{ marginBottom: '20px' }}>
                            <label style={{ display: 'block', marginBottom: '10px', color: 'var(--text-muted)' }}>Ödeme Yöntemi</label>
                            <div style={{
                                padding: '15px',
                                borderRadius: '12px',
                                border: '1px solid var(--accent)',
                                background: 'rgba(34, 197, 94, 0.1)',
                                display: 'flex',
                                alignItems: 'center',
                                gap: '10px'
                            }}>
                                <input type="radio" checked readOnly />
                                <span>Kapıda Ödeme</span>
                            </div>
                        </div>

                        <button
                            type="submit"
                            disabled={isSubmitting}
                            className="btn-primary"
                            style={{ width: '100%' }}>
                            {isSubmitting ? 'İşleniyor...' : 'SİPARİŞİ ONAYLA'}
                        </button>
                    </form>
                </div>

                {/* Right: Summary */}
                <div className="glass-panel" style={{ padding: '30px', height: 'fit-content' }}>
                    <h3 style={{ marginBottom: '20px' }}>Sipariş Özeti</h3>
                    <div style={{ marginBottom: '20px', maxHeight: '300px', overflowY: 'auto' }}>
                        {cart.map(item => (
                            <div key={item.product.id} style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px', fontSize: '0.9rem' }}>
                                <span>{item.quantity}x {item.product.name}</span>
                                <span>{item.quantity * item.product.price} TL</span>
                            </div>
                        ))}
                    </div>
                    <div style={{ borderTop: '1px solid rgba(255,255,255,0.1)', paddingTop: '20px', display: 'flex', justifyContent: 'space-between', fontWeight: 'bold', fontSize: '1.2rem' }}>
                        <span>Toplam</span>
                        <span style={{ color: 'var(--accent)' }}>{cartTotal.toFixed(2)} TL</span>
                    </div>
                </div>
            </div>
        </div>
    );
}
