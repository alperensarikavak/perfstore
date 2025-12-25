'use client'
import { useCart } from '../context/CartContext';
import { useAuth } from '../context/AuthContext';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

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

    const { user, token } = useAuth(); // Assuming AuthContext provides token
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
                        <p style={{ textAlign: 'center', color: 'var(--text-muted)', marginTop: '20px' }}>
                            Sepetiniz boş.
                        </p>
                    ) : (
                        cart.map(item => (
                            <div key={item.product.id} style={itemStyle}>
                                <div style={{ flex: 1 }}>
                                    <h4 style={{ margin: '0 0 5px 0' }}>{item.product.name}</h4>
                                    <span style={{ color: 'var(--accent)', fontSize: '0.9rem' }}>
                                        {item.product.price} TL
                                    </span>
                                </div>

                                <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                                    <button
                                        onClick={() => updateQuantity(item.product.id, -1)}
                                        style={qtyBtnStyle}>-</button>
                                    <span>{item.quantity}</span>
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
                        ))
                    )}
                </div>

                {cart.length > 0 && (
                    <div style={footerStyle}>
                        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '15px' }}>
                            <span>Toplam:</span>
                            <span style={{ color: 'var(--accent)', fontWeight: 'bold', fontSize: '1.1rem' }}>
                                {cartTotal.toFixed(2)} TL
                            </span>
                        </div>
                        <button
                            onClick={handleCheckout}
                            disabled={isCheckingOut}
                            className="btn-primary"
                            style={{ width: '100%' }}>
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
    background: 'rgba(0,0,0,0.5)',
    zIndex: 1000,
    display: 'flex',
    justifyContent: 'flex-end'
};

const sidebarStyle = {
    width: '400px',
    height: '100%',
    maxWidth: '90%',
    background: 'rgba(11, 16, 32, 0.95)',
    display: 'flex',
    flexDirection: 'column',
    borderLeft: '1px solid rgba(255,255,255,0.1)'
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
    cursor: 'pointer'
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
    background: 'rgba(255,255,255,0.1)',
    border: 'none',
    color: 'white',
    width: '24px',
    height: '24px',
    borderRadius: '4px',
    cursor: 'pointer'
};

const removeBtnStyle = {
    background: 'transparent',
    border: 'none',
    color: '#ef4444',
    fontSize: '0.8rem',
    cursor: 'pointer',
    marginLeft: '10px'
};

const footerStyle = {
    padding: '20px',
    borderTop: '1px solid rgba(255,255,255,0.1)',
    background: 'rgba(0,0,0,0.2)'
};
