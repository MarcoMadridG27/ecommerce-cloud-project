// src/services/product/getProductById.ts
import Api from "../api";
import { ProductDetail } from "../../interfaces/products/ProductDetail";

export async function getProductById(id: number): Promise<ProductDetail> {
    const api = await Api.getInstance();

    const response = await api.get<undefined, ProductDetail>({
        url: `/products/${id}`,
    });

    return response.data;
}
