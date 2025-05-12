import { useEffect, useState, useMemo } from "react";
import { useSearchParams } from "react-router-dom";
import ProductCard from "../components/ProductCard";
import Pagination from "../components/Pagination";
import { getProducts } from "../services/product/getProducts";
import { Product } from "../interfaces/products/Product";

const BuscarPage = () => {
    const [params] = useSearchParams();
    const [pagina, setPagina] = useState(1);
    const [productos, setProductos] = useState<Product[]>([]);
    const [loading, setLoading] = useState(true);


    const query = params.get("q")?.toLowerCase() || "";
    const cat = params.get("cat")?.toLowerCase() || "";

    useEffect(() => {
        setPagina(1);
    }, [query, cat]);

    useEffect(() => {
        const fetchData = async () => {
            setLoading(true);
            try {
                const productosData = await getProducts();
                setProductos(productosData);

                // 🔍 Conteo de categorías
                // const conteoCategorias: Record<number, number> = {};
                //
                // productosData.forEach((producto) => {
                //     const id = producto.category_id;
                //     conteoCategorias[id] = (conteoCategorias[id] || 0) + 1;
                // });
                //
                // const resumen = Object.entries(conteoCategorias).map(([id, count]) => ({
                //     category_id: Number(id),
                //     count,
                // }));
                //
                // console.log("📊 Categorías únicas encontradas con sus repeticiones:", resumen);
            } catch (error) {
                console.error("Error al obtener productos:", error);
            } finally {
                setLoading(false); // <-- termina la carga
            }
        };

        fetchData();
    }, []);

    const productosFiltrados = useMemo(() => {
        return productos.filter((p) => {
            const coincideTexto = p.name.toLowerCase().includes(query);
            const coincideCategoria = !cat || String(p.category_id) === cat;
            return coincideTexto && coincideCategoria;
        });
    }, [productos, query, cat]);

    const productosPorPagina = 32;
    const totalPages = Math.ceil(productosFiltrados.length / productosPorPagina);

    const productosPagina = productosFiltrados.slice(
        (pagina - 1) * productosPorPagina,
        pagina * productosPorPagina
    );

    return (
        <div className="pt-20 px-4 space-y-6">
            {loading ? (
                <p className="text-center text-lg font-semibold">Cargando productos...</p>
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

export default BuscarPage;
