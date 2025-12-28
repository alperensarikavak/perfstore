'use client'
import { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';
import { useRouter } from 'next/navigation';

const AuthContext = createContext();

export function AuthProvider({ children }) {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const router = useRouter();

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        const storedUser = localStorage.getItem('user');
        if (storedToken) {
            setToken(storedToken);
            if (storedUser) setUser(JSON.parse(storedUser));
        }
    }, []);

    const login = async (username, password) => {
        try {
            const res = await axios.post('/api/auth/login', { username, password });
            const { token, username: dbUsername } = res.data;

            const userData = { username: dbUsername };
            setToken(token);
            setUser(userData);

            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify(userData));

            router.push('/');
            return { success: true };
        } catch (error) {
            console.error("Login Error:", error);
            return {
                success: false,
                message: error.response?.data?.message || 'Giriş başarısız. Bilgilerinizi kontrol edin.'
            };
        }
    };

    const register = async (username, email, password, pin) => {
        try {
            await axios.post('/api/auth/register', { username, email, password, pin });
            return await login(username, password);
        } catch (error) {
            console.error("Register Error:", error);
            return {
                success: false,
                message: error.response?.data?.message || 'Kayıt başarısız. Başka bir kullanıcı adı deneyin.'
            };
        }
    };

    const logout = () => {
        setToken(null);
        setUser(null);
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        router.push('/login');
    };

    return (
        <AuthContext.Provider value={{ user, token, login, register, logout }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);
