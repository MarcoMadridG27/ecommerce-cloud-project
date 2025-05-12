// src/services/address/updateAddressByUserId.ts
import Api from "../api";
import { AddressRequest } from "../../interfaces/address/AddressRequest";

export async function updateAddressByUserId(id: number, data: AddressRequest): Promise<{ message: string }> {
    const api = await Api.getInstance();
    const response = await api.put<AddressRequest, { message: string }>(data, {
        url: `/addresses/user/${id}`,
    });
    return response.data;
}
