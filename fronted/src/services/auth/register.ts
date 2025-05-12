import Api from "../api";
import { RegisterRequest } from "../../interfaces/auth/RegisterRequest";
import { RegisterResponse } from "../../interfaces/auth/RegisterResponse";

export async function register(registerRequest: RegisterRequest): Promise<RegisterResponse> {
  const api = await Api.getInstance();

  const response = await api.post<RegisterRequest, RegisterResponse>(registerRequest, {
    url: "/register",
  });

  return response.data;
}
