// src/services/product/getProducts.ts
import Api from "../api";
import { Product } from "../../interfaces/products/Product";

export async function getProducts(): Promise<Product[]> {
    const api = await Api.getInstance();
    const response = await api.get<null, Product[]>({
        url: "/products",
    });
    return response.data;
}
