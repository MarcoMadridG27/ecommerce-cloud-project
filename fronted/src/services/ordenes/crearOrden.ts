import {OrdenRequest} from "../../interfaces/ordenes/OrdenRequest.ts";
import Api from "../api.ts";

export async function crearOrden(data: OrdenRequest): Promise<{ insertedId: string }> {
    const api = await Api.getInstance();
    const response = await api.post<OrdenRequest, { insertedId: string }>(data, {
        url: "/ordenes",
    });
    return response.data;
}