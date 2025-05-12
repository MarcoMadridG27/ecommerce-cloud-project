import { useState, useEffect } from "react";
import { useLocation } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import Pagination from "../components/Pagination";
import { getProducts } from "../services/product/getProducts";
import { Product } from "../interfaces/products/Product";

const HomePage = () => {
    const location = useLocation();
    const [pagina, setPagina] = useState(1);
    const [productos, setProductos] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        setPagina(1);
    }, [location.key]);

    useEffect(() => {
        window.scrollTo({ top: 0, behavior: "smooth" });
    }, [pagina]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const data = await getProducts();
                setProductos(data);
            } catch (err) {
                console.error("Error al obtener productos:", err);
            } finally {
                setLoading(false);
            }
        };
        fetchData();
    }, []);

    const productosPorPagina = pagina === 1 ? 16 : 32;

    const totalPages =
        productos.length <= 16
            ? 1
            : 1 + Math.ceil((productos.length - 16) / 32);

    const inicio = pagina === 1 ? 0 : 16 + (pagina - 2) * 32;
    const fin = inicio + productosPorPagina;
    const productosPagina = productos.slice(inicio, fin);

    return (
        <div className="pt-20 px-4 space-y-6">
            {pagina === 1 && (
                <img
                    src="/images/Home.png"
                    alt="Imagen destacada"
                    className="w-full h-auto rounded-xl shadow object-contain"
                />
            )}

            {loading ? (
                <div className="text-center py-20">Cargando productos...</div>
            ) : (
                <>
                    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 xl:grid-cols-4 gap-6">
                        {productosPagina.map((producto) => (
                            <ProductCard key={producto.id} product={producto} />
                        ))}
                    </div>

                    {totalPages > 1 && (
                        <div className="flex justify-center mt-4">
                            <Pagination
                                totalPages={totalPages}
                                currentPage={pagina}
                                onPageChange={setPagina}
                            />
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default HomePage;
