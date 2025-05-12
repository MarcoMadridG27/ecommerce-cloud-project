// src/services/address/createAddress.ts
import Api from "../api";
import { AddressRequest } from "../../interfaces/address/AddressRequest";

export async function createAddress(data: AddressRequest): Promise<{ message: string }> {
    const api = await Api.getInstance();
    const response = await api.post<AddressRequest, { message: string }>(data, {
        url: "/addresses",
    });
    return response.data;
}
