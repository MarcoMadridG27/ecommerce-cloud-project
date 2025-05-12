// src/contexts/CartContext.tsx
import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import { Product } from "../interfaces/products/Product";

export interface CartItem {
    producto: Product;
    cantidad: number;
}

interface CartContextType {
    items: CartItem[];
    total: number;
    addProduct: (producto: Product, cantidad: number) => void;
    updateQuantity: (productId: number, cantidad: number) => void;
    removeProduct: (productId: number) => void;
    clearCart: () => void;
}

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider = ({ children }: { children: ReactNode }) => {
    const [items, setItems] = useState<CartItem[]>([]);
    const [total, setTotal] = useState(0);
    const [isLoading, setIsLoading] = useState(true); // ⏳ nuevo estado

    // Cargar desde localStorage al iniciar
    useEffect(() => {
        const saved = localStorage.getItem("cart");
        if (saved) {
            try {
                const parsed = JSON.parse(saved);
                setItems(parsed.items || []);
                setTotal(parsed.total || 0);
            } catch (error) {
                console.error("❌ Error al cargar el carrito desde localStorage:", error);
            }
        }
        setIsLoading(false);
    }, []);

    // Guardar en localStorage al cambiar
    useEffect(() => {
        if (!isLoading) {
            const totalCalc = items.reduce(
                (sum, item) => sum + item.producto.price * item.cantidad,
                0
            );
            setTotal(totalCalc);
            localStorage.setItem("cart", JSON.stringify({ items, total: totalCalc }));
        }
    }, [items, isLoading]);

    const addProduct = (producto: Product, cantidad: number) => {
        setItems((prev) => {
            const existingItem = prev.find((item) => item.producto.id === producto.id);

            if (existingItem) {
                return prev.map((item) =>
                    item.producto.id === producto.id
                        ? { ...item, cantidad: item.cantidad + cantidad }
                        : item
                );
            }

            return [...prev, { producto, cantidad }];
        });
    };

    const updateQuantity = (productId: number, cantidad: number) => {
        setItems((prev) =>
            prev.map((item) =>
                item.producto.id === productId ? { ...item, cantidad } : item
            ).filter((item) => item.cantidad > 0)
        );
    };



    const removeProduct = (productId: number) => {
        setItems((prev) => prev.filter((item) => item.producto.id !== productId));
    };

    const clearCart = () => {
        setItems([]);
    };

    if (isLoading) return null; // ❌ No renderiza hasta estar listo

    return (
        <CartContext.Provider value={{ items, total, addProduct, removeProduct, clearCart,updateQuantity }}>
            {children}
        </CartContext.Provider>
    );
};

export const useCart = () => {
    const context = useContext(CartContext);
    if (!context) throw new Error("useCart debe estar dentro de <CartProvider>");
    return context;
};
