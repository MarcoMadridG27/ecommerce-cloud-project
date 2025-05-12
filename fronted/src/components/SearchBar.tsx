// src/components/SearchBar.tsx
import { useState } from "react";
import { Category } from "../interfaces/categories/Category";

interface Props {
    categorias: Category[];
    onSearch: (texto: string, categoria: string | null) => void;
}

const SearchBar = ({ categorias, onSearch }: Props) => {
    const [texto, setTexto] = useState("");
    const [categoria, setCategoria] = useState<string | null>(null);

    const handleBuscar = () => {
        onSearch(texto.trim(), categoria);
    };

    const handleKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === "Enter") {
            handleBuscar();
        }
    };

    return (
        <div className="join w-full max-w-2xl mx-auto">
            <input
                className="input join-item w-full"
                placeholder="Buscar..."
                value={texto}
                onChange={(e) => setTexto(e.target.value)}
                onKeyDown={handleKeyDown}
            />
            <select
                className="select join-item"
                onChange={(e) => setCategoria(e.target.value || null)}
            >
                <option value="">Todas las categorías</option>
                {categorias.map((cat) => (
                    <option key={cat.id} value={cat.id.toString()}>
                        {cat.name}
                    </option>
                ))}
            </select>
            <button onClick={handleBuscar} className="btn join-item btn-primary">
                <svg
                    xmlns="http://www.w3.org/2000/svg"
                    className="h-5 w-5"
                    fill="none"
                    viewBox="0 0 24 24"
                    stroke="currentColor"
                >
                    <path
                        strokeLinecap="round"
                        strokeLinejoin="round"
                        strokeWidth="2"
                        d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
                    />
                </svg>
            </button>
        </div>
    );
};

export default SearchBar;
