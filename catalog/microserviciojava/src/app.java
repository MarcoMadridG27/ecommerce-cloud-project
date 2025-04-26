import java.sql.*;
import com.sun.net.httpserver.*;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class app {
    public static void main(String[] args) throws IOException, SQLException {
        Connection conn = null;
        for (int i = 0; i < 5; i++) {
            try {
                conn = DBconnection.connect();
                if (conn != null && !conn.isClosed()) {
                    System.out.println("Conexión exitosa a PostgreSQL.");
                    break;
                }
            } catch (Exception e) {
                System.out.println("Esperando conexión a PostgreSQL...");
            }
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
            }
        }

        if (conn == null) {
            System.out.println("No se pudo conectar a la base de datos.");
            return;
        }

        final Connection finalConn = conn;

        try (Statement stmt = finalConn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS products (id SERIAL PRIMARY KEY, name VARCHAR(100))");
            stmt.execute("CREATE TABLE IF NOT EXISTS inventory (id SERIAL PRIMARY KEY, product_id INT REFERENCES products(id), quantity INT)");
            stmt.execute("CREATE TABLE IF NOT EXISTS categories (id SERIAL PRIMARY KEY, category_name VARCHAR(100))");
            stmt.execute("CREATE TABLE IF NOT EXISTS suppliers (id SERIAL PRIMARY KEY, supplier_name VARCHAR(100))");
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);

        server.createContext("/products", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try (Statement stmt = finalConn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM products")) {
                    StringBuilder response = new StringBuilder("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) response.append(",");
                        response.append(String.format("{\"id\":%d,\"name\":\"%s\"}", rs.getInt("id"), rs.getString("name")));
                        first = false;
                    }
                    response.append("]");
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.toString().getBytes());
                    }
                } catch (SQLException e) {
                    exchange.sendResponseHeaders(500, 0);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                    String body = reader.lines().collect(Collectors.joining());
                    String name = body.replaceAll(".*\\\"name\\\":\\\"(.*?)\\\".*", "$1");
                    try (PreparedStatement ps = finalConn.prepareStatement("INSERT INTO products (name) VALUES (?)")) {
                        ps.setString(1, name);
                        ps.executeUpdate();
                        exchange.sendResponseHeaders(201, 0);
                    }
                } catch (SQLException | IOException e) {
                    exchange.sendResponseHeaders(500, 0);
                }
                exchange.getResponseBody().close();
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
            }
        });

        server.createContext("/product", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                if ("PUT".equals(exchange.getRequestMethod())) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        String body = reader.lines().collect(Collectors.joining());
                        String name = body.replaceAll(".*\\\"name\\\":\\\"(.*?)\\\".*", "$1");
                        try (PreparedStatement ps = finalConn.prepareStatement("UPDATE products SET name=? WHERE id=?")) {
                            ps.setString(1, name);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            exchange.sendResponseHeaders(200, 0);
                        }
                    } catch (SQLException | IOException e) {
                        exchange.sendResponseHeaders(500, 0);
                    }
                } else if ("DELETE".equals(exchange.getRequestMethod())) {
                    try (PreparedStatement ps = finalConn.prepareStatement("DELETE FROM products WHERE id=?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        exchange.sendResponseHeaders(200, 0);
                    } catch (SQLException e) {
                        exchange.sendResponseHeaders(500, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(405, 0);
                }
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
            exchange.getResponseBody().close();
        });

        server.createContext("/inventories", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try (Statement stmt = finalConn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT i.id, p.name, i.quantity FROM inventory i JOIN products p ON i.product_id = p.id")) {
                    StringBuilder response = new StringBuilder("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) response.append(",");
                        response.append(String.format("{\"id\":%d,\"product\":\"%s\",\"quantity\":%d}", rs.getInt("id"), rs.getString("name"), rs.getInt("quantity")));
                        first = false;
                    }
                    response.append("]");
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.toString().getBytes());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                    String body = reader.lines().collect(Collectors.joining());
                    int productId = Integer.parseInt(body.replaceAll(".*\\\"productId\\\":(\\d+).*", "$1"));
                    int qty = Integer.parseInt(body.replaceAll(".*\\\"cantidad\\\":(\\d+).*", "$1"));
                    try (PreparedStatement ps = finalConn.prepareStatement("INSERT INTO inventory (product_id, quantity) VALUES (?, ?)")) {
                        ps.setInt(1, productId);
                        ps.setInt(2, qty);
                        ps.executeUpdate();
                        exchange.sendResponseHeaders(201, 0);
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                }
                exchange.getResponseBody().close();
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
            }
        });
        
        server.createContext("/inventory", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
        
                if ("PUT".equals(exchange.getRequestMethod())) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        String body = reader.lines().collect(Collectors.joining());
                        int quantity = Integer.parseInt(body.replaceAll(".*\\\"quantity\\\":(\\d+).*", "$1"));
                        try (PreparedStatement ps = finalConn.prepareStatement("UPDATE inventory SET quantity=? WHERE id=?")) {
                            ps.setInt(1, quantity);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            exchange.sendResponseHeaders(200, 0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, 0);
                    }
                } else if ("DELETE".equals(exchange.getRequestMethod())) {
                    try (PreparedStatement ps = finalConn.prepareStatement("DELETE FROM inventory WHERE id=?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        exchange.sendResponseHeaders(200, 0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(405, 0);
                }
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
            exchange.getResponseBody().close();
        });
        

        server.createContext("/categories", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try (Statement stmt = finalConn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM categories")) {
                    StringBuilder response = new StringBuilder("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) response.append(",");
                        response.append(String.format("{\"id\":%d,\"category_name\":\"%s\"}", rs.getInt("id"), rs.getString("category_name")));
                        first = false;
                    }
                    response.append("]");
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.toString().getBytes());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                    String body = reader.lines().collect(Collectors.joining());
                    String categoryName = body.replaceAll(".*\\\"category_name\\\":\\\"(.*?)\\\".*", "$1");
                    try (PreparedStatement ps = finalConn.prepareStatement("INSERT INTO categories (category_name) VALUES (?)")) {
                        ps.setString(1, categoryName);
                        ps.executeUpdate();
                        exchange.sendResponseHeaders(201, 0);
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                }
                exchange.getResponseBody().close();
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
            }
        });
        
        server.createContext("/category", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                if ("PUT".equals(exchange.getRequestMethod())) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        String body = reader.lines().collect(Collectors.joining());
                        String categoryName = body.replaceAll(".*\\\"category_name\\\":\\\"(.*?)\\\".*", "$1");
                        try (PreparedStatement ps = finalConn.prepareStatement("UPDATE categories SET category_name=? WHERE id=?")) {
                            ps.setString(1, categoryName);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            exchange.sendResponseHeaders(200, 0);
                        }
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, 0);
                    }
                } else if ("DELETE".equals(exchange.getRequestMethod())) {
                    try (PreparedStatement ps = finalConn.prepareStatement("DELETE FROM categories WHERE id=?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        exchange.sendResponseHeaders(200, 0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(405, 0);
                }
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
            exchange.getResponseBody().close();
        });

        server.createContext("/suppliers", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                try (Statement stmt = finalConn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT * FROM suppliers")) {
                    StringBuilder response = new StringBuilder("[");
                    boolean first = true;
                    while (rs.next()) {
                        if (!first) response.append(",");
                        response.append(String.format("{\"id\":%d,\"supplier_name\":\"%s\"}", rs.getInt("id"), rs.getString("supplier_name")));
                        first = false;
                    }
                    response.append("]");
                    exchange.sendResponseHeaders(200, response.length());
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.toString().getBytes());
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                }
            } else if ("POST".equals(exchange.getRequestMethod())) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                    String body = reader.lines().collect(Collectors.joining());
                    String supplierName = body.replaceAll(".*\\\"supplier_name\\\":\\\"(.*?)\\\".*", "$1");
                    try (PreparedStatement ps = finalConn.prepareStatement("INSERT INTO suppliers (supplier_name) VALUES (?)")) {
                        ps.setString(1, supplierName);
                        ps.executeUpdate();
                        exchange.sendResponseHeaders(201, 0);
                    }
                } catch (SQLException | IOException e) {
                    e.printStackTrace();
                    exchange.sendResponseHeaders(500, 0);
                }
                exchange.getResponseBody().close();
            } else {
                exchange.sendResponseHeaders(405, 0);
                exchange.getResponseBody().close();
            }
        });
        
        server.createContext("/supplier", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length == 3) {
                int id = Integer.parseInt(parts[2]);
                if ("PUT".equals(exchange.getRequestMethod())) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8))) {
                        String body = reader.lines().collect(Collectors.joining());
                        String supplierName = body.replaceAll(".*\\\"supplier_name\\\":\\\"(.*?)\\\".*", "$1");
                        try (PreparedStatement ps = finalConn.prepareStatement("UPDATE suppliers SET supplier_name=? WHERE id=?")) {
                            ps.setString(1, supplierName);
                            ps.setInt(2, id);
                            ps.executeUpdate();
                            exchange.sendResponseHeaders(200, 0);
                        }
                    } catch (SQLException | IOException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, 0);
                    }
                } else if ("DELETE".equals(exchange.getRequestMethod())) {
                    try (PreparedStatement ps = finalConn.prepareStatement("DELETE FROM suppliers WHERE id=?")) {
                        ps.setInt(1, id);
                        ps.executeUpdate();
                        exchange.sendResponseHeaders(200, 0);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        exchange.sendResponseHeaders(500, 0);
                    }
                } else {
                    exchange.sendResponseHeaders(405, 0);
                }
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
            exchange.getResponseBody().close();
        });

        server.setExecutor(null);
        server.start();
    }
}