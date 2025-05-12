import Api from "../api";

export async function deleteAddressByUserId(id: number): Promise<{ message: string }> {
    const api = await Api.getInstance();
    const response = await api.delete<{ message: string }>({
        url: `/addresses/user/${id}`,
    });
    return response.data;
}
