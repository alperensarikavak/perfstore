'use client'
import { useForm } from 'react-hook-form';
import { useAuth } from '@/context/AuthContext';
import Link from 'next/link';
import { useState } from 'react';

export default function RegisterPage() {
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
    const { register: registerUser } = useAuth(); // rename to avoid conflict
    const [serverError, setServerError] = useState('');

    const onSubmit = async (data) => {
        setServerError('');
        const res = await registerUser(data.username, data.email, data.password);
        if (!res.success) {
            setServerError(res.message);
        }
    };

    return (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
            <div className="glass-panel" style={{ padding: '40px', width: '100%', maxWidth: '400px' }}>
                <h1 className="title-gradient" style={{ fontSize: '2rem', marginBottom: '10px', textAlign: 'center' }}>
                    KAYIT OL
                </h1>
                <p style={{ color: 'var(--text-muted)', textAlign: 'center', marginBottom: '30px' }}>
                    Yeni bir hesap oluşturun.
                </p>

                <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <div>
                        <input
                            type="email"
                            {...register('email', { required: 'E-posta gerekli' })}
                            placeholder="E-Posta"
                            style={{
                                width: '100%',
                                padding: '12px',
                                borderRadius: '8px',
                                border: '1px solid rgba(255,255,255,0.1)',
                                background: 'rgba(0,0,0,0.2)',
                                color: 'white',
                                outline: 'none'
                            }}
                        />
                        {errors.email && <span style={{ color: '#ef4444', fontSize: '0.8rem' }}>{errors.email.message}</span>}
                    </div>

                    <div>
                        <input
                            {...register('username', { required: 'Kullanıcı adı gerekli' })}
                            placeholder="Kullanıcı Adı"
                            style={{
                                width: '100%',
                                padding: '12px',
                                borderRadius: '8px',
                                border: '1px solid rgba(255,255,255,0.1)',
                                background: 'rgba(0,0,0,0.2)',
                                color: 'white',
                                outline: 'none'
                            }}
                        />
                        {errors.username && <span style={{ color: '#ef4444', fontSize: '0.8rem' }}>{errors.username.message}</span>}
                    </div>

                    <div>
                        <input
                            type="password"
                            {...register('password', { required: 'Şifre gerekli', minLength: { value: 6, message: 'En az 6 karakter' } })}
                            placeholder="Şifre"
                            style={{
                                width: '100%',
                                padding: '12px',
                                borderRadius: '8px',
                                border: '1px solid rgba(255,255,255,0.1)',
                                background: 'rgba(0,0,0,0.2)',
                                color: 'white',
                                outline: 'none'
                            }}
                        />
                        {errors.password && <span style={{ color: '#ef4444', fontSize: '0.8rem' }}>{errors.password.message}</span>}
                    </div>

                    {serverError && (
                        <div style={{ padding: '10px', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.2)', borderRadius: '8px', color: '#fca5a5', fontSize: '0.9rem', textAlign: 'center' }}>
                            {serverError}
                        </div>
                    )}

                    <button type="submit" className="btn-primary" disabled={isSubmitting}>
                        {isSubmitting ? 'Kayıt Olunuyor...' : 'KAYIT OL'}
                    </button>
                </form>

                <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                    Zaten hesabın var mı? <Link href="/login" style={{ color: 'var(--accent)' }}>Giriş Yap</Link>
                </div>
            </div>
        </div>
    );
}
