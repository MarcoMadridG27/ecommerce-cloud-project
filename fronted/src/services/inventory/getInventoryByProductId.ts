// src/services/inventory/getInventoryByProductId.ts
import Api from "../api";
import { Inventory } from "../../interfaces/inventory/Inventory";

export async function getInventoryByProductId(productId: number): Promise<Inventory> {
    const api = await Api.getInstance();
    const response = await api.get<null, Inventory>({
        url: `/inventories/${productId}`,
    });
    return response.data;
}
