import { useEffect, useState } from "react";
import { useAuth } from "../contexts/AuthContext";
import { getUser } from "../services/user/getUser";
import { getAddressByUserId } from "../services/address/getAddressByUserId";
import { updateUser } from "../services/user/updateUser";
import { updateAddressByUserId } from "../services/address/updateAddressByUserId";
import { createAddress } from "../services/address/createAddress";
import { RegisterRequest } from "../interfaces/auth/RegisterRequest";
import { AddressRequest } from "../interfaces/address/AddressRequest";

const EditUserSection = () => {
    const { user } = useAuth();
    const [showForm, setShowForm] = useState(false);

    const [formData, setFormData] = useState<RegisterRequest>({
        firstname: "-",
        lastname: "-",
        phonenumber: "-",
        email: "-",
        age: 0,
        password: "", // Será opcional para editar
    });

    const [addressData, setAddressData] = useState<AddressRequest>({
        user_id: user?.user_id ?? 0,
        address_line: "",
        city: "",
        country: "",
    });

    const [addressExists, setAddressExists] = useState(false);

    useEffect(() => {
        const fetchData = async () => {
            if (!user?.user_id) return;

            const userRes = await getUser(user.user_id);
            if ("error" in userRes) return;



            setFormData((prev) => ({
                ...prev,
                firstname: userRes.nombre.split(" ")[0] || "-",
                lastname: userRes.nombre.split(" ")[1] || "-",
                email: userRes.email,
                phonenumber: userRes.phonenumber,
                age: userRes.age,
                password: "",
            }));

            const addressRes = await getAddressByUserId(user.user_id);
            if ("error" in addressRes) {
                setAddressExists(false);
                setAddressData((prev) => ({
                    ...prev,
                    address_line: "",
                    city: "",
                    country: "",
                }));
            } else {
                setAddressExists(true);
                setAddressData(addressRes.address);
            }
        };

        fetchData();
    }, [user]);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        if (["address_line", "city", "country"].includes(name)) {
            setAddressData((prev) => ({
                ...prev,
                [name]: value,
            }));
        } else {
            setFormData((prev) => ({
                ...prev,
                [name]: name === "age" ? Number(value) : value,
            }));
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!user?.user_id) return;

        try {
            await updateUser(user.user_id, formData);
            if (addressExists) {
                await updateAddressByUserId(user.user_id, addressData);
            } else {
                await createAddress({ ...addressData, user_id: user.user_id });
            }

            alert("✅ Información actualizada correctamente");
            setShowForm(false);
        } catch (err) {
            console.log(err);
            alert("❌ Error al actualizar usuario o dirección");
        }
    };

    // const handleDelete = async () => {
    //     if (!user?.user_id) return;
    //     if (!confirm("¿Estás seguro de eliminar tu cuenta y dirección?")) return;
    //
    //     try {
    //         await deleteAddressByUserId(user.user_id);
    //         await deleteUser(user.user_id);
    //         alert("✅ Cuenta eliminada");
    //         // Puedes agregar logout() aquí si deseas cerrar sesión automáticamente
    //     } catch (err) {
    //         alert("❌ Error al eliminar cuenta o dirección");
    //     }
    // };

    return (
        <div className="card bg-base-200 shadow-md">
            <div className="card-body">
                <h3 className="card-title">Editar cuenta</h3>
                <p>Puedes modificar tus datos personales, contraseña y dirección.</p>

                <div className="card-actions justify-end">
                    <button className="btn btn-primary" onClick={() => setShowForm((prev) => !prev)}>
                        {showForm ? "Ocultar" : "Editar"}
                    </button>

                </div>

                {showForm && (
                    <form onSubmit={handleSubmit} className="mt-6 space-y-4 max-w-md mx-auto">
                        <label className="input validator flex items-center gap-2">
                            👤
                            <input type="text" name="firstname" placeholder="Nombre" required value={formData.firstname} onChange={handleChange} className="grow" />
                        </label>

                        <label className="input validator flex items-center gap-2">
                            👤
                            <input type="text" name="lastname" placeholder="Apellido" required value={formData.lastname} onChange={handleChange} className="grow" />
                        </label>

                        <label className="input validator flex items-center gap-2">
                            ✉️
                            <input type="email" name="email" placeholder="Correo" required value={formData.email} onChange={handleChange} className="grow" />
                        </label>

                        <label className="input validator flex items-center gap-2">
                            📞
                            <input type="tel" name="phonenumber" placeholder="Teléfono" required value={formData.phonenumber} onChange={handleChange} className="grow" />
                        </label>

                        <label className="input validator flex items-center gap-2">
                            🎂
                            <input type="number" name="age" placeholder="Edad" required min={1} max={120} value={formData.age} onChange={handleChange} className="grow" />
                        </label>

                        <label className="input validator flex items-center gap-2">
                            🔑
                            <input type="password" name="password" placeholder="Contraseña (opcional)" value={formData.password} onChange={handleChange} className="grow" />
                        </label>

                        <label className="input validator flex items-center gap-2">
                            📍
                            <input type="text" name="address_line" placeholder="Dirección" required value={addressData.address_line} onChange={handleChange} className="grow" />
                        </label>

                        <label className="input validator flex items-center gap-2">
                            🏙
                            <input type="text" name="city" placeholder="Ciudad" required value={addressData.city} onChange={handleChange} className="grow" />
                        </label>

                        <label className="input validator flex items-center gap-2">
                            🌍
                            <input type="text" name="country" placeholder="País" required value={addressData.country} onChange={handleChange} className="grow" />
                        </label>

                        <div className="flex justify-end">
                            <button type="submit" className="btn btn-primary">Guardar cambios</button>
                        </div>
                    </form>
                )}
            </div>
        </div>
    );
};

export default EditUserSection;
