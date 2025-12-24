import { Geist, Geist_Mono } from "next/font/google";
import "./globals.css";
import './globals.css'
import { Outfit } from "next/font/google";
import "./globals.css";
import { Providers } from "./providers";
import CartSidebar from "@/components/CartSidebar";

const outfit = Outfit({ subsets: ["latin"] });

export const metadata = {
  title: "PerfStore - Perfect Shopping",
  description: "Experience the perfect shopping journey",
};

export default function RootLayout({ children }) {
  return (
    <html lang="en">
      <body className={outfit.className}>
        <Providers>
          {children}
          <CartSidebar />
        </Providers>
      </body>
    </html>
  );
}
