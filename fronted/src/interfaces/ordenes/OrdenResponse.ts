export interface OrdenResponse {
    _id: string;
    usuario_id: string;
    productos: { producto_id: string; cantidad: number }[];
    total: number;
    estado: string;
    fecha: string;
}