import { useEffect, useState } from "react";
import { Product } from "../interfaces/products/Product";
import { getCategoryImageById } from "../utils/CategoryImageMapper";
import { useCart } from "../contexts/CartContext";

const ProductCard = ({ product }: { product: Product }) => {
    const [imageUrl, setImageUrl] = useState("https://via.placeholder.com/400x200?text=Producto");
    const [cantidad, setCantidad] = useState(1);
    const [mensaje, setMensaje] = useState("");
    const { addProduct } = useCart();

    useEffect(() => {
        const fetchImage = async () => {
            const img = await getCategoryImageById(product.category_id);
            setImageUrl(img);
        };
        fetchImage();
    }, [product.category_id]);

    const incrementar = () => {
        setCantidad((prev) => prev + 1);
    };

    const decrementar = () => {
        setCantidad((prev) => (prev > 1 ? prev - 1 : 1));
    };

    const handleAgregar = () => {
        addProduct(product, cantidad);
        setMensaje("✅ Producto agregado al carrito exitosamente");
        setTimeout(() => setMensaje(""), 2000);
    };

    return (
        <div className="card bg-base-200 shadow-md p-4">
            <figure>
                <img
                    src={imageUrl}
                    alt={product.name}
                    className="w-full object-contain h-48"
                />
            </figure>
            <div className="card-body">
                <h2 className="card-title">{product.name}</h2>
                <p><span className="line-through text-gray-400">S/ {(product.price * 1.09).toFixed(2)}</span></p>
                <p className="text-sm">S/{product.price.toFixed(2)}</p>

                <div className="flex items-center gap-2 mt-2">
                    <button onClick={decrementar} className="btn btn-sm rounded-full">-</button>
                    <span>{cantidad}</span>
                    <button onClick={incrementar} className="btn btn-sm rounded-full">+</button>
                    <button onClick={handleAgregar} className="btn btn-sm btn-warning ml-2">Agregar</button>
                </div>

                {mensaje && (
                    <div className="mt-2 text-green-600 text-sm font-semibold">
                        {mensaje}
                    </div>
                )}
            </div>
        </div>
    );
};

export default ProductCard;
