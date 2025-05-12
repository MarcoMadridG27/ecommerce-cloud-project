// src/components/UserInfoCard.tsx
import { useEffect, useState } from "react";
import { getUser } from "../services/user/getUser";
import { getAddressByUserId } from "../services/address/getAddressByUserId";
import { UserResponse } from "../interfaces/user/UserResponse";
import { AddressResponse } from "../interfaces/address/AddressResponse";
import { useAuth } from "../contexts/AuthContext";

const UserInfoCard = () => {
    const { user } = useAuth();
    const [userInfo, setUserInfo] = useState<UserResponse | null>(null);
    const [addressInfo, setAddressInfo] = useState<AddressResponse | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const fetchData = async () => {
            if (user?.user_id) {
                const userResult = await getUser(user.user_id);
                if ("error" in userResult) {
                    setError(userResult.error);
                } else {
                    setUserInfo(userResult);
                }

                const addressResult = await getAddressByUserId(user.user_id);
                if ("address" in addressResult) {
                    setAddressInfo(addressResult);
                }
            }
        };

        fetchData();
    }, [user]);

    if (error) {
        return <div className="text-red-500">⚠️ {error}</div>;
    }

    if (!userInfo) {
        return <div className="text-center py-10">Cargando información del usuario...</div>;
    }

    const address = addressInfo?.address;

    return (
        <div className="card w-full bg-base-200 shadow-md">
            <div className="card-body">
                <h2 className="card-title text-2xl">Perfil del usuario</h2>

                <div className="flex items-center gap-4 mt-4">
                    <div className="avatar">
                        <div className="w-20 rounded-full ring ring-primary ring-offset-base-100 ring-offset-2">
                            <img
                                src="/images/usuario.jpg"
                                alt="Avatar de usuario"
                            />
                        </div>
                    </div>
                    <div>
                        <p className="font-semibold text-lg">{userInfo.nombre}</p>
                        <p className="text-sm text-base-content/70">{userInfo.email}</p>
                    </div>
                </div>

                <div className="divider my-4">Información personal</div>

                <div className="grid grid-cols-2 gap-4 text-sm">
                    <div><span className="font-medium">Nombre: </span>{userInfo.nombre.split(' ')[0]}</div>
                    <div><span className="font-medium">Apellido: </span>{userInfo.nombre.split(' ')[1]}</div>
                    <div><span className="font-medium">Teléfono: </span>{userInfo.phonenumber}</div>
                    <div><span className="font-medium">Edad: </span>{userInfo.age}</div>
                    <div className="col-span-2">
                        <span className="font-medium">Dirección: </span>{" "}
                        {address
                            ? `${address.address_line}, ${address.city}, ${address.country}`
                            : "-"}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default UserInfoCard;
