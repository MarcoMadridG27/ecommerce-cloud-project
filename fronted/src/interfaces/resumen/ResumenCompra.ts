// src/interfaces/resumen/ResumenCompra.ts

export interface ResumenCompra {
    orden: {
        _id: string;
        usuario_id: string;
        productos: {
            producto_id: string;
            cantidad: number;
        }[];
        total: number;
        estado: string;
        fecha: string;
    };
    pagos: {
        _id: string;
        metodo: string;
        monto: number;
        estado: string;
        fecha: string;
    }[];
    usuario: {
        id: string;
        nombre: string;
        email: string;
    };
    productos: {
        id: string;
        nombre: string;
        precio: number;
    }[];
}
