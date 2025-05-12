import {PagoRequest} from "../../interfaces/pagos/PagoRequest.ts";
import Api from "../api.ts";

export async function registrarPago(data: PagoRequest): Promise<{ insertedId: string }> {
    const api = await Api.getInstance();
    const response = await api.post<PagoRequest, { insertedId: string }>(data, {
        url: "/pagos",
    });
    return response.data;
}