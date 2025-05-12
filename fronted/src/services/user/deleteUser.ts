// src/services/user/deleteUser.ts
import Api from "../api";

export async function deleteUser(id: number): Promise<{ message: string }> {
    const api = await Api.getInstance();

    const response = await api.delete<{ message: string }>({
        url: `/users/${id}`,
    });

    return response.data;
}
