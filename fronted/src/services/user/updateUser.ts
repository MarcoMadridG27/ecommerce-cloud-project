// src/services/user/updateUser.ts
import Api from "../api";
import { RegisterRequest } from "../../interfaces/auth/RegisterRequest"; // incluye password

export async function updateUser(id: number, userData: RegisterRequest): Promise<{ message: string }> {
    const api = await Api.getInstance();

    const response = await api.put<RegisterRequest, { message: string }>(userData, {
        url: `/users/${id}`,
    });

    return response.data;
}
