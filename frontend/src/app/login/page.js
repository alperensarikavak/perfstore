'use client'
import { useForm } from 'react-hook-form';
import { useAuth } from '@/context/AuthContext';
import Link from 'next/link';
import { useState } from 'react';

export default function LoginPage() {
    const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
    const { login } = useAuth();
    const [serverError, setServerError] = useState('');

    const onSubmit = async (data) => {
        setServerError('');
        const res = await login(data.username, data.password);
        if (!res.success) {
            setServerError(res.message);
        }
    };

    return (
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
            <div className="glass-panel" style={{ padding: '40px', width: '100%', maxWidth: '400px' }}>
                <h1 className="title-gradient" style={{ fontSize: '2rem', marginBottom: '10px', textAlign: 'center' }}>
                    GİRİŞ YAP
                </h1>
                <p style={{ color: 'var(--text-muted)', textAlign: 'center', marginBottom: '30px' }}>
                    Perfect System dünyasına hoş geldiniz.
                </p>

                <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                    <div>
                        <input
                            {...register('username', { required: 'Kullanıcı adı gerekli' })}
                            placeholder="Kullanıcı Adı"
                            className="form-input" // CSS sınıfı eklendi
                        />
                        {errors.username && <span style={{ color: '#ef4444', fontSize: '0.8rem' }}>{errors.username.message}</span>}
                    </div>

                    <div>
                        <input
                            type="password"
                            {...register('password', { required: 'Şifre gerekli' })}
                            placeholder="Şifre"
                            className="form-input" // CSS sınıfı eklendi
                        />
                        {errors.password && <span style={{ color: '#ef4444', fontSize: '0.8rem' }}>{errors.password.message}</span>}
                    </div>

                    {serverError && (
                        <div style={{ padding: '10px', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid rgba(239, 68, 68, 0.2)', borderRadius: '8px', color: '#fca5a5', fontSize: '0.9rem', textAlign: 'center' }}>
                            {serverError}
                        </div>
                    )}

                    <button type="submit" className="btn-primary" disabled={isSubmitting}>
                        {isSubmitting ? 'Giriş Yapılıyor...' : 'GİRİŞ YAP'}
                    </button>
                </form>

                <div style={{ marginTop: '20px', textAlign: 'center', fontSize: '0.9rem', color: 'var(--text-muted)' }}>
                    Hesabın yok mu? <Link href="/register" className="link-accent-hover">Kayıt Ol</Link>
                </div>
            </div>
        </div>
    );
}
