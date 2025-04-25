export interface Orden {
    usuario_id: string;
    productos: { producto_id: string; cantidad: number }[];
    total: number;
    fecha: string;
    estado: "pendiente" | "enviado" | "entregado";
  }
  
  export interface Pago {
    orden_id: string;
    metodo: "tarjeta" | "transferencia" | "efectivo";
    monto: number;
    fecha: string;
    estado: "pendiente" | "completado" | "fallido";
  }
  