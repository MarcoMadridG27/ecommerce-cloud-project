package com.tienda.orquestador.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Usuario {
    private String id;
    private String nombre;
    private String email;
}