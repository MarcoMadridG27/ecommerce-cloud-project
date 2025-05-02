package main.java.com.ejemplo;
import com.github.javafaker.Faker;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Random;


public class GeneradorDatos {

    public static void main(String[] args) {
        Faker faker = new Faker();
        Random random = new Random();

        try (Connection conn = DBconnection.getConnection()) {
            System.out.println("📦 Generando datos...");

            // Categorías
            for (int i = 0; i < 100; i++) {
                String sql = "INSERT INTO categories (category_name) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, faker.commerce().department());
                    stmt.executeUpdate();
                }
            }

            // Proveedores
            for (int i = 0; i < 100; i++) {
                String sql = "INSERT INTO suppliers (supplier_name) VALUES (?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, faker.company().name());
                    stmt.executeUpdate();
                }
            }

            // Productos
            for (int i = 0; i < 20000; i++) {
                String sql = "INSERT INTO products (name, category_id, supplier_id) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, faker.commerce().productName());
                    stmt.setInt(2, random.nextInt(100) + 1); // IDs 1-100
                    stmt.setInt(3, random.nextInt(100) + 1);
                    stmt.executeUpdate();
                }
            }

            // Inventario
            String sql = "INSERT INTO inventory (product_id, quantity) VALUES (?, ?)";
            for (int i = 1; i <= 20000; i++) {
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setInt(1, i); // producto_id igual al ID insertado
                    stmt.setInt(2, random.nextInt(500)); // cantidad entre 0-499
                    stmt.executeUpdate();
                }
            }

            System.out.println("✅ Datos generados correctamente.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
