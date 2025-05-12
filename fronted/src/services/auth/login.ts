import Api from "../api";
import { LoginRequest } from "../../interfaces/auth/LoginRequest";
import { AuthResponse } from "../../interfaces/auth/AuthResponse";
import { LoginError } from "../../interfaces/auth/LoginError";

export async function login(loginRequest: LoginRequest): Promise<AuthResponse | LoginError> {
 const api = await Api.getInstance();

 try {
  const response = await api.post<LoginRequest, AuthResponse>(loginRequest, {
   url: "/login",
  });

  return response.data;
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
 } catch (error: any) {
  // Si el backend responde con 200 pero error en cuerpo, esto se debe manejar en el componente.
  if (error.response?.data?.error) {
   return { error: error.response.data.error };
  }

  throw new Error("Error inesperado en la autenticación");
 }
}
