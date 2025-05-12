import Api from "../api";
import { Category } from "../../interfaces/categories/Category";

export async function getCategories(): Promise<Category[]> {
    const api = await Api.getInstance();

    const response = await api.request<null, string>({
        url: "/categories",
        method: "GET",
        headers: {
            Accept: "text/plain",
        },
        responseType: "text",
    });

    try {
        const fixed = response.data.replace(/""/g, '"');
        const parsed = JSON.parse(fixed);
        if (!Array.isArray(parsed)) throw new Error("La respuesta no es un array");
        return parsed as Category[];
    } catch (err) {
        console.error("❌ Error al parsear categorías:", err);
        throw new Error("No se pudieron obtener las categorías");
    }
}
