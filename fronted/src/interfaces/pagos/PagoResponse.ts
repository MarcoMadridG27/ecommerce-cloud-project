export interface PagoResponse {
    _id: string;
    orden_id: string;
    metodo: string;
    monto: number;
    fecha: string;
    estado: string;
}