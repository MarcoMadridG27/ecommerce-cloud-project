import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { RegisterRequest } from "../interfaces/auth/RegisterRequest";
import { register } from "../services/auth/register";

export const RegisterForm = () => {
    const [formData, setFormData] = useState<RegisterRequest>({
        firstname: "",
        lastname: "",
        email: "",
        phonenumber: "",
        age: 0,
        password: "",
    });

    const navigate = useNavigate();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({
            ...prev,
            [name]: name === "age" ? Number(value) : value,
        }));
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            await register(formData);
            navigate("/login");
        } catch (error) {
            console.error("Error al registrar:", error);
            alert("Hubo un error al registrar. Inténtalo de nuevo.");
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-5 max-w-md">
            {/* Nombre */}
            <label className="input validator flex items-center gap-2">
                <svg className="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <g fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                        <circle cx="12" cy="7" r="4" />
                    </g>
                </svg>
                <input type="text" name="firstname" placeholder="Nombre" required minLength={2} className="grow" onChange={handleChange} />
            </label>

            {/* Apellido */}
            <label className="input validator flex items-center gap-2">
                <svg className="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <g fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                        <path d="M19 21v-2a4 4 0 0 0-4-4H9a4 4 0 0 0-4 4v2" />
                        <circle cx="12" cy="7" r="4" />
                    </g>
                </svg>
                <input type="text" name="lastname" placeholder="Apellido" required minLength={2} className="grow" onChange={handleChange} />
            </label>

            {/* Correo */}
            <label className="input validator flex items-center gap-2">
                <svg className="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
                    <g fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                        <rect width="20" height="16" x="2" y="4" rx="2"></rect>
                        <path d="m22 7-8.97 5.7a1.94 1.94 0 0 1-2.06 0L2 7" />
                    </g>
                </svg>
                <input type="email" name="email" placeholder="correo@dominio.com" required className="grow" onChange={handleChange} />
            </label>

            {/* Teléfono */}
            <label className="input validator flex items-center gap-2">
                <svg className="h-[1em] opacity-50" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 16 16">
                    <g fill="none">
                        <path d="M7.25 11.5C6.83579 11.5 6.5 11.8358 6.5 12.25C6.5 12.6642 6.83579 13 7.25 13H8.75C9.16421 13 9.5 12.6642 9.5 12.25C9.5 11.8358 9.16421 11.5 8.75 11.5H7.25Z" fill="currentColor" />
                        <path fillRule="evenodd" clipRule="evenodd" d="M6 1C4.61929 1 3.5 2.11929 3.5 3.5V12.5C3.5 13.8807 4.61929 15 6 15H10C11.3807 15 12.5 13.8807 12.5 12.5V3.5C12.5 2.11929 11.3807 1 10 1H6ZM10 2.5H9.5V3C9.5 3.27614 9.27614 3.5 9 3.5H7C6.72386 3.5 6.5 3.27614 6.5 3V2.5H6C5.44771 2.5 5 2.94772 5 3.5V12.5C5 13.0523 5.44772 13.5 6 13.5H10C10.5523 13.5 11 13.0523 11 12.5V3.5C11 2.94772 10.5523 2.5 10 2.5Z" fill="currentColor" />
                    </g>
                </svg>
                <input type="tel" name="phonenumber" placeholder="Teléfono" required minLength={9} maxLength={15} className="grow" onChange={handleChange} />
            </label>

            {/* Edad */}
            <label className="input validator flex items-center gap-2">
                <span className="font-bold text-gray-500 text-sm">🎂</span>
                <input type="number" name="age" placeholder="Edad" required min={1} max={120} className="grow" onChange={handleChange} />
            </label>

            {/* Contraseña */}
            <label className="input validator flex items-center gap-2">
                <span className="text-sm opacity-50">🔒</span>
                <input type="password" name="password" placeholder="Contraseña" required minLength={6} className="grow" onChange={handleChange} />
            </label>

            {/* Botón */}
            <button type="submit" className="btn btn-primary w-full">
                Registrarse
            </button>
        </form>
    );
};
