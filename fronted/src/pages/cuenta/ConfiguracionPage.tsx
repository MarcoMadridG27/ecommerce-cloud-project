import EditUserSection from "../../components/EditUserSection";
import DeleteUserSection from "../../components/DeleteUserSection";

const ConfiguracionPage = () => {


    return (
        <div className="p-4 max-w-2xl mx-auto space-y-6">
            <h2 className="text-2xl font-bold">Configuración de cuenta</h2>
            <EditUserSection />
            <DeleteUserSection />
        </div>
    );
};

export default ConfiguracionPage;
