import { useState } from "react";
import { obtenerOrdenesPorUsuario } from "../services/ordenes/obtenerOrdenesPorUsuario";
import { getResumenCompra } from "../services/resumen/getResumenCompra";
import { ResumenCompra } from "../interfaces/resumen/ResumenCompra";

const AdministradorOrdenesPage = () => {
    const [usuarioId, setUsuarioId] = useState("");
    const [resumenes, setResumenes] = useState<ResumenCompra[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    const handleBuscar = async () => {
        setError("");
        setResumenes([]);
        if (!usuarioId.trim()) return setError("⚠️ Ingresa un ID de usuario válido");

        try {
            setLoading(true);
            const ordenes = await obtenerOrdenesPorUsuario(usuarioId.trim());

            const resumenesOrdenes = await Promise.all(
                ordenes.map((orden) => getResumenCompra(orden._id))
            );

            setResumenes(resumenesOrdenes);
        } catch (err) {
            console.error("Error al obtener datos:", err);
            setError("❌ No se pudo obtener la información del usuario.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-6 max-w-5xl mx-auto space-y-6">
            <h1 className="text-2xl font-bold">Administrador - Órdenes por Usuario</h1>

            <div className="flex gap-2 items-center">
                <input
                    type="text"
                    placeholder="ID del usuario"
                    className="input input-bordered w-full max-w-xs"
                    value={usuarioId}
                    onChange={(e) => setUsuarioId(e.target.value)}
                />
                <button className="btn btn-primary" onClick={handleBuscar}>
                    Buscar
                </button>
            </div>

            {error && <div className="alert alert-error mt-4">{error}</div>}
            {loading && <div className="loading loading-spinner text-primary mt-4"></div>}

            {resumenes.map((resumen) => (
                <div key={resumen.orden._id} className="card bg-base-100 shadow-md p-5 mt-4">
                    <h2 className="text-lg font-bold mb-2">Orden #{resumen.orden._id}</h2>

                    <div className="mb-2">
                        <strong>Estado:</strong> {resumen.orden.estado}
                        <br />
                        <strong>Fecha:</strong> {new Date(resumen.orden.fecha).toLocaleString()}
                        <br />
                        <strong>Total:</strong> S/ {resumen.orden.total.toFixed(2)}
                    </div>

                    <div className="mb-2">
                        <strong>Usuario:</strong> {resumen.usuario.nombre} ({resumen.usuario.email})
                    </div>

                    <div className="mb-2">
                        <strong>Productos:</strong>
                        <ul className="list-disc pl-5">
                            {resumen.orden.productos.map((prod) => {
                                const detalle = resumen.productos.find((p) => p.id === prod.producto_id);
                                return (
                                    <li key={prod.producto_id}>
                                        {detalle?.nombre ?? "Producto no encontrado"} - S/{" "}
                                        {detalle?.precio.toFixed(2)} x {prod.cantidad} unidades
                                    </li>
                                );
                            })}
                        </ul>
                    </div>

                    <div className="mb-2">
                        <strong>Pagos:</strong>
                        <ul className="list-disc pl-5">
                            {resumen.pagos.map((pago) => (
                                <li key={pago._id}>
                                    {pago.metodo} - S/ {pago.monto.toFixed(2)} - {pago.estado} -{" "}
                                    {new Date(pago.fecha).toLocaleString()}
                                </li>
                            ))}
                        </ul>
                    </div>
                </div>
            ))}
        </div>
    );
};

export default AdministradorOrdenesPage;
