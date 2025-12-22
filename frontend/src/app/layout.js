import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import './globals.css'
import { Outfit } from 'next/font/google'
import { Providers } from './providers'

const outfit = Outfit({ subsets: ['latin'] })

export const metadata = {
  title: 'PerfStore | Perfect Systems',
  description: 'Premium Electronics Store',
}

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={outfit.className}>
        <Providers>
          {children}
        </Providers>
      </body>
    </html>
  )
}
