package com.tienda.orquestador.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class Orden {

    @JsonProperty("_id")
    private String id;

    @JsonProperty("usuario_id")
    private String usuarioId;

    @JsonProperty("estado")
    private String estado;

    private List<Map<String, Object>> productos;
    private Double total;
    private String fecha;

    public Orden() {}

    public Orden(String id, String usuarioId, List<Map<String, Object>> productos, Double total, String fecha, String estado) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.productos = productos;
        this.total = total;
        this.fecha = fecha;
        this.estado=estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public List<Map<String, Object>> getProductos() {
        return productos;
    }

    public void setProductos(List<Map<String, Object>> productos) {
        this.productos = productos;
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
