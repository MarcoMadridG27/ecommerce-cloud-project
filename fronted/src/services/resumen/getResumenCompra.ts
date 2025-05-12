// src/services/resumen/getResumenCompra.ts

import Api from "../api";
import { ResumenCompra } from "../../interfaces/resumen/ResumenCompra";

export async function getResumenCompra(ordenId: string): Promise<ResumenCompra> {
    const api = await Api.getInstance();

    const response = await api.get<undefined, ResumenCompra>({
        url: `/resumen/${ordenId}`,
    });

    return response.data;
}
