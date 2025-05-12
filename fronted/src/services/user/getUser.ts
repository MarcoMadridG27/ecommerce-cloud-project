// src/services/users/getUser.ts
import Api from "../api";
import { UserResponse } from "../../interfaces/user/UserResponse";
import { GetUserError } from "../../interfaces/user/GetUserError";

export async function getUser(id: number): Promise<UserResponse | GetUserError> {
    const api = await Api.getInstance();

    try {
        const response = await api.get<null, UserResponse | GetUserError>({
            url: `/users/${id}`,
        });

        return response.data;
        // eslint-disable-next-line @typescript-eslint/no-explicit-any
    } catch (error: any) {
        if (error.response?.data?.error) {
            return { error: error.response.data.error };
        }

        throw new Error("Error inesperado al obtener usuario");
    }
}
