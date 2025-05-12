export interface OrdenRequest {
    usuario_id: string;
    productos: { producto_id: string; cantidad: number }[];
    total: number;
    estado: string;
}