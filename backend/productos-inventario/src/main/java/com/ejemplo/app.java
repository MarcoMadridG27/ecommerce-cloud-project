package main.java.com.ejemplo;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.stream.Collectors;
import java.sql.Statement;


public class app {
    public static void main(String[] args) throws IOException {
        cargarDatosSiVacio();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/products", wrapWithCors(exchange -> {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    try (Connection conn = DBconnection.getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT id, name FROM products")) {

                        StringBuilder json = new StringBuilder("[");
                        while (rs.next()) {
                            json.append(String.format("{\"id\":%d,\"name\":\"%s\"},",
                                    rs.getInt("id"), rs.getString("name")));
                        }
                        if (json.charAt(json.length() - 1) == ',') {
                            json.setLength(json.length() - 1);
                        }
                        json.append("]");

                        byte[] getResponse = json.toString().getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(200, getResponse.length);
                        exchange.getResponseBody().write(getResponse);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1);
                    }
                    break;

                case "POST":
                    String requestBody;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        requestBody = reader.lines().collect(Collectors.joining("\n"));
                    }
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name) VALUES (?)")) {
                        String name = requestBody.replaceAll(".*\"name\":\"(.*?)\".*", "$1");
                        stmt.setString(1, name);
                        stmt.executeUpdate();
                        exchange.sendResponseHeaders(201, -1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/products/", wrapWithCors(exchange -> {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            if (pathParts.length < 3) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }
            int id = Integer.parseInt(pathParts[2]);
            switch (exchange.getRequestMethod()) {
                case "GET":
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("SELECT id, name FROM products WHERE id = ?")) {
                        stmt.setInt(1, id);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            String json = String.format("{\"id\":%d,\"name\":\"%s\"}", rs.getInt("id"), rs.getString("name"));
                            byte[] getResponse = json.getBytes(StandardCharsets.UTF_8);
                            exchange.sendResponseHeaders(200, getResponse.length);
                            exchange.getResponseBody().write(getResponse);
                        } else {
                            exchange.sendResponseHeaders(404, -1);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1);
                    }
                    break;
                case "PUT":
                    String putBody;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        putBody = reader.lines().collect(Collectors.joining("\n"));
                    }
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("UPDATE products SET name = ? WHERE id = ?")) {
                        String name = putBody.replaceAll(".*\"name\":\"(.*?)\".*", "$1");
                        stmt.setString(1, name);
                        stmt.setInt(2, id);
                        stmt.executeUpdate();
                        exchange.sendResponseHeaders(200, -1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1);
                    }
                    break;
                case "DELETE":
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("DELETE FROM products WHERE id = ?")) {
                        stmt.setInt(1, id);
                        stmt.executeUpdate();
                        exchange.sendResponseHeaders(200, -1);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/inventories", wrapWithCors(exchange -> {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    String allInventories = "[{\"id\":1,\"productId\":1,\"cantidad\":10}]";
                    byte[] getResponse = allInventories.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, getResponse.length);
                    exchange.getResponseBody().write(getResponse);
                    break;
                case "POST":
                    String requestBodyProducts;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        requestBodyProducts = reader.lines().collect(Collectors.joining("\n"));
                    }
                    System.out.println("POST Body: " + requestBodyProducts);
                    exchange.sendResponseHeaders(201, -1);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/inventories/", wrapWithCors(exchange -> {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            if (pathParts.length < 3) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }
            String id = pathParts[2];
            switch (exchange.getRequestMethod()) {
                case "PUT":
                    String putBody;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        putBody = reader.lines().collect(Collectors.joining("\n"));
                    }
                    System.out.println("PUT Inventory ID: " + id + ", Body: " + putBody);
                    exchange.sendResponseHeaders(200, -1);
                    break;
                case "DELETE":
                    System.out.println("DELETE Inventory ID: " + id);
                    exchange.sendResponseHeaders(200, -1);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/categories", wrapWithCors(exchange -> {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    String allCategories = "[{\"id\":1,\"name\":\"Electronics\"}]";
                    byte[] getResponse = allCategories.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, getResponse.length);
                    exchange.getResponseBody().write(getResponse);
                    break;
                case "POST":
                    String requestBodyCategory;
                    try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        requestBodyCategory = reader.lines().collect(Collectors.joining("\n"));
                    }
                    System.out.println("POST Category Body: " + requestBodyCategory);
                    exchange.sendResponseHeaders(201, -1);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/categories/", wrapWithCors(exchange -> {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            if (pathParts.length < 3) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }
            String id = pathParts[2];
            switch (exchange.getRequestMethod()) {
                case "PUT":
                    String putBody;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        putBody = reader.lines().collect(Collectors.joining("\n"));
                    }
                    System.out.println("PUT Category ID: " + id + ", Body: " + putBody);
                    exchange.sendResponseHeaders(200, -1);
                    break;
                case "DELETE":
                    System.out.println("DELETE Category ID: " + id);
                    exchange.sendResponseHeaders(200, -1);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/suppliers", wrapWithCors(exchange -> {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    String allSuppliers = "[{\"id\":1,\"name\":\"Supplier A\",\"contact\":\"123456789\"}]";
                    byte[] getResponse = allSuppliers.getBytes(StandardCharsets.UTF_8);
                    exchange.sendResponseHeaders(200, getResponse.length);
                    exchange.getResponseBody().write(getResponse);
                    break;
                case "POST":
                    String requestBodySupplier;
                    try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        requestBodySupplier = reader.lines().collect(Collectors.joining("\n"));
                    }
                    System.out.println("POST Category Body: " + requestBodySupplier);
                    exchange.sendResponseHeaders(201, -1);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.createContext("/suppliers/", wrapWithCors(exchange -> {
            String[] pathParts = exchange.getRequestURI().getPath().split("/");
            if (pathParts.length < 3) {
                exchange.sendResponseHeaders(400, -1);
                exchange.close();
                return;
            }
            String id = pathParts[2];
            switch (exchange.getRequestMethod()) {
                case "PUT":
                    String putBody;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        putBody = reader.lines().collect(Collectors.joining("\n"));
                    }
                    System.out.println("PUT Supplier ID: " + id + ", Body: " + putBody);
                    exchange.sendResponseHeaders(200, -1);
                    break;
                case "DELETE":
                    System.out.println("DELETE Supplier ID: " + id);
                    exchange.sendResponseHeaders(200, -1);
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));

        server.setExecutor(null);
        server.start();
        System.out.println("Server running on http://localhost:8000");
    }

    public static HttpHandler wrapWithCors(HttpHandler handler) {
        return exchange -> {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Access-Control-Allow-Origin", "*");
            headers.add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            headers.add("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
            } else {
                handler.handle(exchange);
            }
        };
    }


private static void cargarDatosSiVacio() {
    try (Connection conn = DBconnection.getConnection();
         Statement stmt = conn.createStatement()) {

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
        if (rs.next() && rs.getInt(1) == 0) {
            System.out.println("📥 Tabla vacía. Cargando CSVs...");
            cargarCSV(conn, "data/categories.csv", "INSERT INTO categories(category_name) VALUES (?)");
            cargarCSV(conn, "data/suppliers.csv", "INSERT INTO suppliers(supplier_name) VALUES (?)");
            cargarCSV(conn, "data/products.csv", "INSERT INTO products(name, category_id, supplier_id) VALUES (?, ?, ?)");
            cargarCSV(conn, "data/inventory.csv", "INSERT INTO inventory(product_id, quantity) VALUES (?, ?)");
            System.out.println("✅ Datos cargados desde CSV correctamente.");
        } else {
            System.out.println("✔️ La tabla products ya tiene datos. No se carga CSV.");
        }
    } catch (SQLException | IOException e) {
        System.err.println("❌ Error al cargar CSVs: " + e.getMessage());
    }
}

private static void cargarCSV(Connection conn, String ruta, String query) throws IOException, SQLException {
    try (BufferedReader br = new BufferedReader(new FileReader(ruta));
         PreparedStatement stmt = conn.prepareStatement(query)) {

        String linea;
        while ((linea = br.readLine()) != null) {
            String[] valores = linea.split(",");
            for (int i = 0; i < valores.length; i++) {
                stmt.setString(i + 1, valores[i].trim());
            }
            stmt.executeUpdate();
        }
    }
}
}

