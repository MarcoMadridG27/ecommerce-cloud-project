// src/services/category/getCategoryById.ts
import Api from "../api";
import { Category } from "../../interfaces/categories/Category";

export async function getCategoryById(id: number): Promise<Category> {
    const api = await Api.getInstance();
    const response = await api.get<null, Category>({
        url: `/categories/${id}`,
    });
    return response.data;
}
