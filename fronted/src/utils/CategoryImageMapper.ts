// src/utils/CategoryImageMapper.ts
import { getCategoryById } from "../services/category/getCategoryById";

/**
 * Retorna la URL de la imagen correspondiente a una categoría obtenida por ID.
 * Si no se encuentra la categoría o no hay imagen disponible, retorna una imagen genérica.
 */
export const getCategoryImageById = async (categoryId: number): Promise<string> => {
    try {
        const category = await getCategoryById(categoryId);

        const categoryImages: Record<string, string> = {
            Kids: "/images/kids.png",
            Shoes: "/images/shoes.png",
            Grocery: "/images/grocery.png",
            Electronics: "/images/tecnologia.png",
            Clothing: "/images/ropa.png",
            Books: "/images/libros.png",
            Home: "/images/hogar.png",
            Baby: "/images/baby.png",
            Tools: "/images/tools.png",

        };

        return categoryImages[category.name] || "https://via.placeholder.com/400x200?text=Producto";
    } catch (error) {
        console.error("Error al obtener la categoría:", error);
        return "https://via.placeholder.com/400x200?text=Producto";
    }
};
