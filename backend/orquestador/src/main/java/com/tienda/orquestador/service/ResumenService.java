package com.tienda.orquestador.service;


import com.tienda.orquestador.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ResumenService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ResumenCompra generarResumen(String ordenId) {
        Orden orden = getOrden(ordenId);
        List<Pago> pagos = getPagos(ordenId);
        Usuario usuario = getUsuario(orden.getUsuarioId());
        List<Producto> productos = getProductos(orden.getProductos());

        return new ResumenCompra(orden, pagos, usuario, productos);
    }

    private Orden getOrden(String ordenId) {
        String url = "http://ordenes-pagos:8000/ordenes/" + ordenId;
        ResponseEntity<Orden> response = restTemplate.getForEntity(url, Orden.class);
        return response.getBody();
    }

    private List<Pago> getPagos(String ordenId) {
        String url = "http://ordenes-pagos:8000/pagos/orden/" + ordenId;
        ResponseEntity<Pago[]> response = restTemplate.getForEntity(url, Pago[].class);
        return Arrays.asList(response.getBody());
    }

    private Usuario getUsuario(String usuarioId) {
        String url = "http://usuarios:5000/usuarios/" + usuarioId;
        ResponseEntity<Usuario> response = restTemplate.getForEntity(url, Usuario.class);
        return response.getBody();
    }

    private List<Producto> getProductos(List<Map<String, Object>> productosInfo) {
        List<Producto> productos = new ArrayList<>();
        for (Map<String, Object> p : productosInfo) {
            String productoId = (String) p.get("producto_id");
            String url = "http://productos:8080/productos/" + productoId;
            ResponseEntity<Producto> response = restTemplate.getForEntity(url, Producto.class);
            productos.add(response.getBody());
        }
        return productos;
    }
}
