import { useAuth } from "../contexts/AuthContext";
import { deleteAddressByUserId } from "../services/address/deleteAddressByUserId";
import { deleteUser } from "../services/user/deleteUser";
import {useNavigate} from "react-router-dom";

const DeleteUserSection = () => {
    const navigate = useNavigate();
    const { user, logout } = useAuth();

    const handleDelete = async () => {
        if (!user?.user_id) return;

        try {
            await deleteAddressByUserId(user.user_id);
            await deleteUser(user.user_id);
            alert("✅ Cuenta eliminada");
            logout();
            navigate("/");
        } catch (err) {
            console.error(err);
            alert("❌ Error al eliminar cuenta o dirección");
        }
    };

    return (
        <>
            <div className="card bg-base-200 shadow-md">
                <div className="card-body">
                    <h3 className="card-title text-error">Eliminar cuenta</h3>
                    <p className="text-sm text-base-content/70">
                        Esta acción eliminará tu cuenta de forma permanente. No se puede deshacer.
                    </p>
                    <div className="card-actions justify-end">
                        <label htmlFor="confirmar-eliminar" className="btn btn-error">
                            Eliminar cuenta
                        </label>
                    </div>
                </div>
            </div>

            {/* Modal de confirmación */}
            <input type="checkbox" id="confirmar-eliminar" className="modal-toggle" />
            <div className="modal">
                <div className="modal-box">
                    <h3 className="font-bold text-lg text-error">¿Estás absolutamente seguro?</h3>
                    <p className="py-4">
                        Esto eliminará todos tus datos, pedidos y configuraciones.
                        <br />
                        <span className="text-error font-semibold">Esta acción no se puede deshacer.</span>
                    </p>
                    <div className="modal-action">
                        <label htmlFor="confirmar-eliminar" className="btn">Cancelar</label>
                        <button onClick={handleDelete} className="btn btn-error">Eliminar definitivamente</button>
                    </div>
                </div>
            </div>
        </>
    );
};

export default DeleteUserSection;
