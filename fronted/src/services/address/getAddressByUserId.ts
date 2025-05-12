// src/services/address/getAddressByUserId.ts
import Api from "../api";
import { AddressResponse } from "../../interfaces/address/AddressResponse";
import { AddressError } from "../../interfaces/address/AddressError";

export async function getAddressByUserId(id: number): Promise<AddressResponse | AddressError> {
    const api = await Api.getInstance();

    try {
        const response = await api.get<null, AddressResponse | AddressError>({
            url: `/addresses/user/${id}`,
        });

        return response.data;
    } catch (error: unknown) {
        console.error("Error al obtener dirección:", error);
        throw new Error("Error inesperado al obtener dirección");
    }
}
