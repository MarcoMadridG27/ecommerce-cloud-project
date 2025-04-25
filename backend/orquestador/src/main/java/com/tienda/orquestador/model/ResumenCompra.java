package com.tienda.orquestador.model;

import java.util.List;

public class ResumenCompra {
    private Orden orden;
    private List<Pago> pagos;
    private Usuario usuario;
    private List<Producto> productos;

    public ResumenCompra() {}

    public ResumenCompra(Orden orden, List<Pago> pagos, Usuario usuario, List<Producto> productos) {
        this.orden = orden;
        this.pagos = pagos;
        this.usuario = usuario;
        this.productos = productos;
    }

    public Orden getOrden() {
        return orden;
    }

    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    public List<Pago> getPagos() {
        return pagos;
    }

    public void setPagos(List<Pago> pagos) {
        this.pagos = pagos;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }
}
