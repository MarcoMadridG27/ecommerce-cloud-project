// src/pages/OrdenesPage.tsx
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {OrdenResponse} from "../../interfaces/ordenes/OrdenResponse.ts";
import {ProductDetail} from "../../interfaces/products/ProductDetail.ts";
import {useAuth} from "../../contexts/AuthContext.tsx";
import {getProductById} from "../../services/product/getProductById.ts";
import {obtenerOrdenesPorUsuario} from "../../services/ordenes/obtenerOrdenesPorUsuario.ts";


interface OrdenConDetalles extends OrdenResponse {
    detallesProductos: (ProductDetail & { cantidad: number })[];
}

const OrdenesPage = () => {
    const { user, isAuthenticated } = useAuth();
    const [ordenes, setOrdenes] = useState<OrdenConDetalles[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    useEffect(() => {
        const cargarOrdenes = async () => {
            if (!isAuthenticated || !user) {
                navigate("/login");
                return;
            }

            try {
                const data = await obtenerOrdenesPorUsuario(String(user.user_id));

                // Ordenar por fecha (más reciente primero)
                data.sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime());

                const ordenesDetalladas: OrdenConDetalles[] = [];

                for (const orden of data) {
                    const detalles: (ProductDetail & { cantidad: number })[] = [];

                    for (const prod of orden.productos) {
                        const detalle = await getProductById(Number(prod.producto_id));
                        detalles.push({ ...detalle, cantidad: prod.cantidad });
                    }

                    ordenesDetalladas.push({ ...orden, detallesProductos: detalles });
                }

                setOrdenes(ordenesDetalladas);
            } catch (err) {
                console.error("❌ Error al cargar órdenes:", err);
            } finally {
                setLoading(false);
            }
        };

        cargarOrdenes();
    }, [user, isAuthenticated, navigate]);

    if (loading) {
        return <div className="pt-20 text-center">Cargando órdenes...</div>;
    }

    return (
        <div className="pt-20 px-4">
            <h1 className="text-2xl font-bold mb-6">📦 Historial de órdenes</h1>

            {ordenes.length === 0 ? (
                <p>No tienes órdenes registradas.</p>
            ) : (
                <div className="space-y-6">
                    {ordenes.map((orden) => (
                        <div key={orden._id} className="card bg-base-100 shadow-md p-4">
                            <div className="mb-2 text-sm text-gray-500">ID: {orden._id}</div>
                            <div className="flex justify-between">
                                <span className="text-lg font-semibold">Estado: {orden.estado}</span>
                                <span className="text-sm">{new Date(orden.fecha).toLocaleString()}</span>
                            </div>

                            <div className="mt-4 space-y-2">
                                {orden.detallesProductos.map((prod) => (
                                    <div
                                        key={prod.id}
                                        className="flex justify-between items-center border-b pb-2"
                                    >
                                        <div>
                                            <div className="font-semibold">{prod.nombre}</div>
                                            <div className="text-sm text-gray-500">
                                                Cantidad: {prod.cantidad}
                                            </div>
                                        </div>
                                        <div className="text-right">
                                            <div className="text-sm">S/ {prod.precio.toFixed(2)}</div>
                                            <div className="text-xs text-gray-400">
                                                Subtotal: S/ {(prod.precio * prod.cantidad).toFixed(2)}
                                            </div>
                                        </div>
                                    </div>
                                ))}
                            </div>

                            <div className="text-right mt-4 font-bold text-primary">
                                Total: S/ {orden.total.toFixed(2)}
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default OrdenesPage;
