package com.tienda.orquestador.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Pago {

    @JsonProperty("_id")
    private String id;

    private String metodo;
    private Double monto;
    private String fecha;
    private String estado;

    public Pago() {}

    public Pago(String id, String metodo, Double monto, String fecha, String estado) {
        this.id = id;
        this.metodo = metodo;
        this.monto = monto;
        this.fecha = fecha;
        this.estado = estado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMetodo() {
        return metodo;
    }

    public void setMetodo(String metodo) {
        this.metodo = metodo;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
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
