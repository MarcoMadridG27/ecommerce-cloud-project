package main.java.com.ejemplo;

import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.stream.Collectors;

public class app {
    public static void main(String[] args) throws IOException {
        cargarDatosSiVacio();
        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/products", wrapWithCors(exchange -> {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    try (Connection conn = DBconnection.getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT id, name, price, category_id FROM products")) {
        
                        StringBuilder json = new StringBuilder("[");
                        while (rs.next()) {
                            json.append(String.format("{\"id\":%d,\"name\":\"%s\",\"price\":%.2f,\"category_id\":%d},",
                                    rs.getInt("id"), rs.getString("name"), rs.getDouble("price"), rs.getInt("category_id")));
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
                         PreparedStatement stmt = conn.prepareStatement("INSERT INTO products (name, category_id, price) VALUES (?, ?, ?)")) {
                        String name = requestBody.replaceAll(".*\"name\":\"(.*?)\".*", "$1");
                        int categoryId = Integer.parseInt(requestBody.replaceAll(".*\"category_id\":(\\d+).*", "$1"));
                        double price = Double.parseDouble(requestBody.replaceAll(".*\"price\":(\\d+(\\.\\d+)?).*", "$1"));
        
                        stmt.setString(1, name);
                        stmt.setInt(2, categoryId);
                        stmt.setDouble(3, price);
                        stmt.executeUpdate();
                        exchange.sendResponseHeaders(201, -1);
                    } catch (SQLException | NumberFormatException e) {
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
                         PreparedStatement stmt = conn.prepareStatement("SELECT id, name, price, category_id FROM products WHERE id = ?")) {
        
                        stmt.setInt(1, id);
                        ResultSet rs = stmt.executeQuery();
        
                        if (rs.next()) {
                            String json = String.format(
                                "{\"id\":%d,\"nombre\":\"%s\",\"precio\":%.2f}",
                                rs.getInt("id"), rs.getString("name"), rs.getDouble("price")
                            );
                            byte[] getResponse = json.getBytes(StandardCharsets.UTF_8);
                            exchange.getResponseHeaders().add("Content-Type", "application/json");
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
                         PreparedStatement stmt = conn.prepareStatement("UPDATE products SET name = ?, price = ?, category_id = ? WHERE id = ?")) {
                        String name = putBody.replaceAll(".*\"name\":\"(.*?)\".*", "$1");
                        int categoryId = Integer.parseInt(putBody.replaceAll(".*\"category_id\":(\\d+).*", "$1"));
                        double price = Double.parseDouble(putBody.replaceAll(".*\"price\":(\\d+(\\.\\d+)?).*", "$1"));
        
                        stmt.setString(1, name);
                        stmt.setDouble(2, price);
                        stmt.setInt(3, categoryId);
                        stmt.setInt(4, id);
                        stmt.executeUpdate();
                        exchange.sendResponseHeaders(200, -1);
                    } catch (SQLException | NumberFormatException e) {
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
                    try (Connection conn = DBconnection.getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT product_id, quantity FROM inventory")) {
                        StringBuilder json = new StringBuilder("[");
                        while (rs.next()) {
                            json.append(String.format("{\"product_id\":%d,\"quantity\":%d},",
                                    rs.getInt("product_id"), rs.getInt("quantity")));
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
                    String requestBodyProducts;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        requestBodyProducts = reader.lines().collect(Collectors.joining("\n"));
                    }
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("INSERT INTO inventory (product_id, quantity) VALUES (?, ?)")) {
                        int productId = Integer.parseInt(requestBodyProducts.replaceAll(".*\"product_id\":(\\d+).*", "$1"));
                        int quantity = Integer.parseInt(requestBodyProducts.replaceAll(".*\"quantity\":(\\d+).*", "$1"));
                        stmt.setInt(1, productId);
                        stmt.setInt(2, quantity);
                        stmt.executeUpdate();
                        exchange.sendResponseHeaders(201, -1);
                    } catch (SQLException | NumberFormatException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, -1);
                    }
                    break;
                default:
                    exchange.sendResponseHeaders(405, -1);
            }
            exchange.close();
        }));
        
        server.createContext("/categories", wrapWithCors(exchange -> {
            switch (exchange.getRequestMethod()) {
                case "GET":
                    try (Connection conn = DBconnection.getConnection();
                         Statement stmt = conn.createStatement();
                         ResultSet rs = stmt.executeQuery("SELECT id, category_name FROM categories")) {
                        StringBuilder json = new StringBuilder("[");
                        while (rs.next()) {
                            json.append(String.format("{\"id\":%d,\"name\":\"%s\"},",
                                    rs.getInt("id"), rs.getString("category_name")));
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
                    String requestBodyCategory;
                    try (BufferedReader reader = new BufferedReader(
                            new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        requestBodyCategory = reader.lines().collect(Collectors.joining("\n"));
                    }
                    try (Connection conn = DBconnection.getConnection();
                         PreparedStatement stmt = conn.prepareStatement("INSERT INTO categories (category_name) VALUES (?)")) {
                        String name = requestBodyCategory.replaceAll(".*\"name\":\"(.*?)\".*", "$1");
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

// --- Carga personalizada de CSVs ---

private static void cargarCSVProducts(Connection conn, String ruta) throws IOException, SQLException {
    try (BufferedReader br = new BufferedReader(new FileReader(ruta));
         PreparedStatement stmt = conn.prepareStatement("INSERT INTO products(id, name, category_id, price) VALUES (?, ?, ?, ?)")) {
        String linea;
        while ((linea = br.readLine()) != null) {
            if (linea.startsWith("id")) continue;
            String[] valores = linea.split(",");
            if (valores.length != 4) {
                System.err.println("❌ Formato incorrecto en: " + ruta + " => " + linea);
                continue;
            }
            stmt.setInt(1, Integer.parseInt(valores[0].trim()));
            stmt.setString(2, valores[1].trim());
            stmt.setInt(3, Integer.parseInt(valores[2].trim()));
            stmt.setDouble(4, Double.parseDouble(valores[3].trim()));
            stmt.executeUpdate();
        }
    }
}

private static void cargarCSVCategories(Connection conn, String ruta) throws IOException, SQLException {
    try (BufferedReader br = new BufferedReader(new FileReader(ruta));
         PreparedStatement stmt = conn.prepareStatement("INSERT INTO categories(id, category_name) VALUES (?, ?)")) {
        String linea;
        while ((linea = br.readLine()) != null) {
            if (linea.startsWith("id")) continue;
            String[] valores = linea.split(",", 2);
            if (valores.length != 2) {
                System.err.println("❌ Formato incorrecto en: " + ruta + " => " + linea);
                continue;
            }
            stmt.setInt(1, Integer.parseInt(valores[0].trim()));
            stmt.setString(2, valores[1].trim());
            stmt.executeUpdate();
        }
    }
}

private static void cargarCSVInventory(Connection conn, String ruta) throws IOException, SQLException {
    try (BufferedReader br = new BufferedReader(new FileReader(ruta));
         PreparedStatement stmt = conn.prepareStatement("INSERT INTO inventory(product_id, quantity) VALUES (?, ?)")) {
        String linea;
        while ((linea = br.readLine()) != null) {
            if (linea.startsWith("product_id") || linea.startsWith("id")) continue;
            String[] valores = linea.split(",");
            if (valores.length != 2) {
                System.err.println("❌ Formato incorrecto en: " + ruta + " => " + linea);
                continue;
            }
            stmt.setInt(1, Integer.parseInt(valores[0].trim()));
            stmt.setInt(2, Integer.parseInt(valores[1].trim()));
            stmt.executeUpdate();
        }
    }
}

// --- Reemplazo de cargarDatosSiVacio ---

private static void cargarDatosSiVacio() {
    try (Connection conn = DBconnection.getConnection();
         Statement stmt = conn.createStatement()) {

        ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM products");
        if (rs.next() && rs.getInt(1) == 0) {
            System.out.println("📥 Tabla vacía. Cargando CSVs...");
            cargarCSVCategories(conn, "data/categories.csv");
            cargarCSVProducts(conn, "data/products.csv");
            cargarCSVInventory(conn, "data/inventory.csv");
            System.out.println("✅ Datos cargados desde CSV correctamente.");
        } else {
            System.out.println("✔️ La tabla products ya tiene datos. No se carga CSV.");
        }
    } catch (SQLException | IOException e) {
        System.err.println("❌ Error al cargar CSVs: " + e.getMessage());
    }
}
}
