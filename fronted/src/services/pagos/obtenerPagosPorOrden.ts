import Api from "../api.ts";
import {PagoResponse} from "../../interfaces/pagos/PagoResponse.ts";

export async function obtenerPagosPorOrden(ordenId: string): Promise<PagoResponse[]> {
    const api = await Api.getInstance();
    const response = await api.get<null, PagoResponse[]>({
        url: `/pagos/orden/${ordenId}`,
    });
    return response.data;
}