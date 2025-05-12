import {OrdenResponse} from "../../interfaces/ordenes/OrdenResponse.ts";
import Api from "../api.ts";

export async function obtenerOrdenesPorUsuario(usuarioId: string): Promise<OrdenResponse[]> {
    const api = await Api.getInstance();
    const response = await api.get<null, OrdenResponse[]>({
        url: `/ordenes/usuario/${usuarioId}`,
    });
    return response.data;
}