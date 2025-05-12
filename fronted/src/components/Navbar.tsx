import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../contexts/AuthContext";
import SearchBar from "./SearchBar";
import { getCategories } from "../services/category/getCategories";
import { Category } from "../interfaces/categories/Category";
import { useCart } from "../contexts/CartContext";

const Navbar = () => {
    const { isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();
    const [categorias, setCategorias] = useState<Category[]>([]);
    const { items, total } = useCart();

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const data = await getCategories();
                if (Array.isArray(data)) {
                    setCategorias(data);
                } else {
                    console.error("La respuesta no es un array:", data);
                }
            } catch (error) {
                console.error("Error al obtener categorías:", error);
            }
        };
        fetchCategories();
    }, []);


    const handleSearch = (texto: string, categoria: string | null) => {
        const params = new URLSearchParams();
        if (texto) params.set("q", texto);
        if (categoria) params.set("cat", categoria);
        navigate(`/buscar?${params.toString()}`);
    };

    const cantidadTotal = items.reduce((sum, item) => sum + item.cantidad, 0); // 👈 Suma total de productos

    return (
        <div className="navbar bg-base-200 shadow-sm fixed top-0 z-50 w-full px-4">
            <div className="flex-1">
                <Link to="/" className="btn btn-ghost text-xl">Velsora</Link>
                <Link to="/admin" className="btn btn-outline btn-sm normal-case">Administrador</Link>
            </div>
            <div className="hidden md:flex md:flex-1 justify-center">
                <SearchBar categorias={categorias} onSearch={handleSearch} />
            </div>



            {/* DERECHA */}
            <div className="flex-none gap-2">
                {/* 🛒 Carrito */}
                <div className="dropdown dropdown-end">
                    <div tabIndex={0} role="button" className="btn btn-ghost btn-circle">
                        <div className="indicator">
                            <svg xmlns="http://www.w3.org/2000/svg" className="h-5 w-5" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
                            </svg>
                            <span className="badge badge-sm indicator-item">
                                {cantidadTotal}
                            </span>
                        </div>
                    </div>
                    <div tabIndex={0} className="card card-compact dropdown-content bg-base-100 z-10 mt-3 w-60 shadow">
                        <div className="card-body">
                            <span className="text-lg font-bold">
                                {cantidadTotal} producto{cantidadTotal !== 1 && "s"}
                            </span>
                            <span className="text-info">
                                Subtotal: S/ {total.toFixed(2)}
                            </span>
                            <div className="card-actions">
                                <button onClick={() => navigate("/carrito")} className="btn btn-primary btn-block">
                                    Ver carrito
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                {/* 👤 Usuario */}
                <div className="dropdown dropdown-end">
                    <div tabIndex={0} role="button" className="btn btn-ghost btn-circle">
                        <svg xmlns="http://www.w3.org/2000/svg" className="h-6 w-6" fill="none" viewBox="0 0 24 24" stroke="currentColor">
                            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth="2" d="M5.121 17.804A13.937 13.937 0 0112 15c2.219 0 4.29.538 6.121 1.488M15 10a3 3 0 11-6 0 3 3 0 016 0z" />
                        </svg>
                    </div>
                    <ul className="menu menu-sm dropdown-content bg-base-200 rounded-box z-10 mt-3 w-52 p-2 shadow">
                        {isAuthenticated ? (
                            <>
                                <li><Link to="/cuenta/info">Perfil</Link></li>
                                <li><button onClick={logout}>Cerrar sesión</button></li>
                            </>
                        ) : (
                            <>
                                <li><Link to="/login">Iniciar sesión</Link></li>
                                <li><Link to="/register">Registrarse</Link></li>
                            </>
                        )}
                    </ul>
                </div>
            </div>
        </div>
    );
};

export default Navbar;
