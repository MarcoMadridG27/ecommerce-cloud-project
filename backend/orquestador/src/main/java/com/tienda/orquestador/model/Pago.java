package com.tienda.orquestador.model;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    public String id;
    public String ordenId;
    public Double monto;
    public String fecha;
}