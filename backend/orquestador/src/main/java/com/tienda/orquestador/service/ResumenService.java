package com.tienda.orquestador.service;

import com.tienda.orquestador.model.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class ResumenService {

    private final RestTemplate restTemplate = new RestTemplate();

    public ResumenCompra generarResumen(String ordenId) {
        System.out.println("🟡 Iniciando generación de resumen para orden ID: " + ordenId);

        Orden orden = getOrden(ordenId);
        System.out.println("✅ Orden obtenida: " + orden);

        List<Pago> pagos = getPagos(ordenId);
        System.out.println("✅ Pagos obtenidos: " + pagos.size());

        Usuario usuario = getUsuario(orden.getUsuarioId());
        System.out.println("✅ Usuario obtenido: " + usuario);

        List<Producto> productos = getProductos(orden.getProductos());
        System.out.println("✅ Productos obtenidos: " + productos.size());

        System.out.println("🟢 Resumen generado exitosamente.");
        return new ResumenCompra(orden, pagos, usuario, productos);
    }

    private Orden getOrden(String ordenId) {
        String url = "http://ordenes-pagos:8000/ordenes/" + ordenId;
        System.out.println("🔎 Solicitando orden desde: " + url);
        try {
            // 1. Obtener la respuesta como String
            ResponseEntity<String> rawResponse = restTemplate.getForEntity(url, String.class);
            String body = rawResponse.getBody();
    
            // 2. Imprimir el JSON crudo
            System.out.println("📦 JSON recibido:");
            System.out.println(body);
    
            // 3. Mapear manualmente con ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            Orden orden = mapper.readValue(body, Orden.class);
            return orden;
    
        } catch (Exception e) {
            System.err.println("❌ Error al obtener o mapear orden: " + e.getMessage());
            throw new RuntimeException("Error al obtener orden");
        }
    }
    

    private List<Pago> getPagos(String ordenId) {
        String url = "http://ordenes-pagos:8000/pagos/orden/" + ordenId;
        System.out.println("🔎 Solicitando pagos desde: " + url);
        try {
            ResponseEntity<Pago[]> response = restTemplate.getForEntity(url, Pago[].class);
            if (response.getBody() != null) {
                return Arrays.asList(response.getBody());
            } else {
                return new ArrayList<>(); // ✅ Evita el NullPointerException
            }
        } catch (Exception e) {
            System.err.println("❌ Error al obtener pagos: " + e.getMessage());
            return new ArrayList<>(); // ⚠️ Retorna lista vacía en caso de error
        }
    }

    private Usuario getUsuario(String usuarioId) {
        String url = "http://usuarios-direcciones:8000/users/" + usuarioId;
        System.out.println("🔎 Solicitando usuario desde: " + url);
        try {
            ResponseEntity<Usuario> response = restTemplate.getForEntity(url, Usuario.class);
            return response.getBody();
        } catch (Exception e) {
            System.err.println("❌ Error al obtener usuario: " + e.getMessage());
            throw e;
        }
    }

    private List<Producto> getProductos(List<Map<String, Object>> productosInfo) {
        List<Producto> productos = new ArrayList<>();
        for (Map<String, Object> p : productosInfo) {
            String productoId = (String) p.get("producto_id");
            String url = "http://productos-inventarios:8000/products/" + productoId;
            System.out.println("🔎 Solicitando producto desde: " + url);
            try {
                ResponseEntity<Producto> response = restTemplate.getForEntity(url, Producto.class);
                if (response.getBody() != null) {
                    productos.add(response.getBody());
                }
            } catch (Exception e) {
                System.err.println("❌ Error al obtener producto ID " + productoId + ": " + e.getMessage());
            }
        }
        return productos;
    }
}
