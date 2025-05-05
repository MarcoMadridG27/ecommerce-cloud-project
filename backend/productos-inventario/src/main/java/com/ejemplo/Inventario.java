package main.java.com.ejemplo;
public class Inventario {
    private int productId;
    private int cantidad;

    public Inventario() {}
    public Inventario(int productId, int cantidad) {
        this.productId = productId;
        this.cantidad = cantidad;
    }

    public int getId() { return productId; }
    public void setId(int productId) { this.productId = productId; }

    public int getCantidad() { return cantidad; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
}