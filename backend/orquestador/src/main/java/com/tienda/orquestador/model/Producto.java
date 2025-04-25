package com.tienda.orquestador.model;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Producto {
    private String id;
    private String nombre;
    private Double precio;

    // Getters y setters
}