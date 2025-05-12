// src/contexts/AuthContext.tsx
import { createContext, useContext, useEffect, useState, ReactNode } from "react";
import { login as loginService } from "../services/auth/login.ts";
import { LoginRequest } from "../interfaces/auth/LoginRequest";
import {LoginError} from "../interfaces/auth/LoginError.ts";


interface AuthContextType {
    isAuthenticated: boolean;
    user: { user_id: number; name: string } | null;
    login: (credentials: LoginRequest) => Promise<LoginError | null>;
    logout: () => void;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider = ({ children }: { children: ReactNode }) => {
    const [user, setUser] = useState<{ user_id: number; name: string } | null>(null);

    useEffect(() => {
        const stored = localStorage.getItem("auth");
        if (stored) {
            setUser(JSON.parse(stored));
        }
    }, []);

    const login = async (credentials: LoginRequest): Promise<LoginError | null> => {
        const result = await loginService(credentials);
        if ("error" in result) {
            return { error: result.error };
        }

        const authUser = { user_id: result.user_id, name: result.name };
        localStorage.setItem("auth", JSON.stringify(authUser));
        setUser(authUser);
        return null;
    };

    const logout = () => {
        localStorage.removeItem("auth");
        setUser(null);
    };

    return (
        <AuthContext.Provider
            value={{
                isAuthenticated: !!user,
                user,
                login,
                logout,
            }}
        >
            {children}
        </AuthContext.Provider>
    );
};

export const useAuth = () => {
    const context = useContext(AuthContext);
    if (!context) throw new Error("useAuth debe estar dentro de <AuthProvider>");
    return context;
};
