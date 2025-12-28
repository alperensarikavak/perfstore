'use client'
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import Image from 'next/image'; // 1. EKLENDİ: Resim bileşeni import edildi

export default function CartSidebar() {
    const {
        cart,
        isCartOpen,
        setIsCartOpen,
        removeFromCart,
        updateQuantity,
        clearCart,
        cartTotal
    } = useCart();

    const { user, token } = useAuth();
    const router = useRouter();
    const [isCheckingOut, setIsCheckingOut] = useState(false);

    if (!isCartOpen) return null;

    const handleCheckout = () => {
        if (!user) {
            alert("Lütfen önce giriş yapın!");
            router.push('/login');
            setIsCartOpen(false);
            return;
        }

        setIsCartOpen(false);
        router.push('/checkout');
    };

    return (
        <div style={overlayStyle}>
            <div style={sidebarStyle} className="glass-panel">
                <div style={headerStyle}>
                    <h2 className="title-gradient">Sepetim ({cart.length})</h2>
                    <button onClick={() => setIsCartOpen(false)} style={closeBtnStyle}>✕</button>
                </div>

                <div style={contentStyle}>
                    {cart.length === 0 ? (
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', height: '100%', color: 'var(--text-muted)' }}>
                            <span style={{ fontSize: '3rem', marginBottom: '10px', opacity: 0.5 }}>🛒</span>
                            <p>Sepetiniz şu an boş.</p>
                        </div>
                    ) : (
                        cart.map(item => (
                            <div key={item.product.id} style={itemStyle}>
                                
                                {/* 2. EKLENDİ: Resim Alanı */}
                                <div style={{ width: '60px', height: '60px', borderRadius: '8px', overflow: 'hidden', position: 'relative', background: 'rgba(255,255,255,0.05)', flexShrink: 0 }}>
                                    {item.product.imageUrl ? (
                                        <Image 
                                            src={item.product.imageUrl} 
                                            alt={item.product.name}
                                            fill
                                            style={{ objectFit: 'cover' }}
                                        />
                                    ) : (
                                        <div style={{ width: '100%', height: '100%', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '1.5rem' }}>📦</div>
                                    )}
                                </div>

                                <div style={{ flex: 1 }}>
                                    <h4 style={{ margin: '0 0 5px 0', fontSize: '0.95rem' }}>{item.product.name}</h4>
                                    <span style={{ color: 'var(--accent)', fontSize: '0.9rem', fontWeight: 'bold' }}>
                                        {item.product.price} TL
                                    </span>
                                </div>

                                <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'end', gap: '5px' }}>
                                    <div style={{ display: 'flex', alignItems: 'center', gap: '8px', background: 'rgba(255,255,255,0.05)', borderRadius: '4px', padding: '2px' }}>
                                        <button
                                            onClick={() => updateQuantity(item.product.id, -1)}
                                            style={qtyBtnStyle}>-</button>
                                        <span style={{ fontSize: '0.9rem', minWidth: '20px', textAlign: 'center' }}>{item.quantity}</span>
                                        <button
                                            onClick={() => updateQuantity(item.product.id, 1)}
                                            style={qtyBtnStyle}>+</button>
                                    </div>
                                    
                                    <button
                                        onClick={() => removeFromCart(item.product.id)}
                                        style={removeBtnStyle}>
                                        Sil
                                    </button>
                                </div>
                            </div>
                        ))
                    )}
                </div>

                {cart.length > 0 && (
                    <div style={footerStyle}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '15px' }}>
                            <span>Toplam:</span>
                            <span style={{ color: 'var(--accent)', fontWeight: 'bold', fontSize: '1.2rem' }}>
                                {cartTotal.toFixed(2)} TL
                            </span>
                        </div>
                        <button
                            onClick={handleCheckout}
                            disabled={isCheckingOut}
                            className="btn-primary"
                            style={{ width: '100%', padding: '12px', fontSize: '1rem' }}>
                            {isCheckingOut ? 'İşleniyor...' : 'SİPARİŞİ TAMAMLA'}
                        </button>
                    </div>
                )}
            </div>
        </div>
    );
}

// Styles
const overlayStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
    background: 'rgba(0,0,0,0.6)', // Biraz daha koyulaştırdım
    zIndex: 1000,
    display: 'flex',
    justifyContent: 'flex-end',
    backdropFilter: 'blur(2px)' // Arka planı hafif buzladım
};

const sidebarStyle = {
    width: '400px',
    height: '100%',
    maxWidth: '90%',
    background: '#0f172a', // Daha net bir renk (glass-panel sınıfı zaten efekt verir)
    display: 'flex',
    flexDirection: 'column',
    borderLeft: '1px solid rgba(255,255,255,0.1)',
    boxShadow: '-5px 0 25px rgba(0,0,0,0.5)'
};

const headerStyle = {
    padding: '20px',
    borderBottom: '1px solid rgba(255,255,255,0.1)',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
};

const closeBtnStyle = {
    background: 'transparent',
    border: 'none',
    color: 'white',
    fontSize: '1.2rem',
    cursor: 'pointer',
    padding: '5px'
};

const contentStyle = {
    flex: 1,
    overflowY: 'auto',
    padding: '20px'
};

const itemStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '15px',
    marginBottom: '15px',
    paddingBottom: '15px',
    borderBottom: '1px solid rgba(255,255,255,0.05)'
};

const qtyBtnStyle = {
    background: 'transparent',
    border: 'none',
    color: 'white',
    width: '24px',
    height: '24px',
    cursor: 'pointer',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    fontSize: '1rem'
};

const removeBtnStyle = {
    background: 'transparent',
    border: 'none',
    color: '#ef4444', // Kırmızı renk
    fontSize: '0.75rem',
    cursor: 'pointer',
    textDecoration: 'underline',
    opacity: 0.8
};

const footerStyle = {
    padding: '25px',
    borderTop: '1px solid rgba(255,255,255,0.1)',
    background: 'rgba(0,0,0,0.2)'
};