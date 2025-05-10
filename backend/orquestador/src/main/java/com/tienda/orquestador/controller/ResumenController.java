package com.tienda.orquestador.controller;

import com.tienda.orquestador.model.ResumenCompra;
import com.tienda.orquestador.service.ResumenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ResumenController {

    @Autowired
    private ResumenService resumenService;

    private final RestTemplate restTemplate = new RestTemplate();
    private final String USUARIOS_URL = "http://usuarios-direcciones:8000";
    private final String PRODUCTOS_URL = "http://productos-inventarios:8000/products";
    private final String INVENTARIOS_URL = "http://productos-inventarios:8000/inventories";
    private final String CATEGORIAS_URL = "http://productos-inventarios:8000/categories";
    private final String baseUrl = "http://ordenes-pagos:8000";


    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("echo test ok");
    }

    @GetMapping("/resumen/{ordenId}")
    public ResponseEntity<ResumenCompra> obtenerResumen(@PathVariable String ordenId) {
        ResumenCompra resumen = resumenService.generarResumen(ordenId);
        return ResponseEntity.ok(resumen);
    }

    // --- Endpoints de Usuarios ---

    @GetMapping("/users")
    public ResponseEntity<String> getUsers() {
        return restTemplate.getForEntity(USUARIOS_URL + "/users", String.class);
    }

    @PostMapping("/users")
    public ResponseEntity<String> addUser(@RequestBody Map<String, Object> user) {
        return restTemplate.postForEntity(USUARIOS_URL + "/users", user, String.class);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<String> getUser(@PathVariable int id) {
        return restTemplate.getForEntity(USUARIOS_URL + "/users/" + id, String.class);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<Void> updateUser(@PathVariable int id, @RequestBody Map<String, Object> user) {
        restTemplate.put(USUARIOS_URL + "/users/" + id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable int id) {
        restTemplate.delete(USUARIOS_URL + "/users/" + id);
        return ResponseEntity.ok().build();
    }

    // --- Endpoints de Addresses ---

    @GetMapping("/addresses")
    public ResponseEntity<String> getAddresses() {
        return restTemplate.getForEntity(USUARIOS_URL + "/addresses", String.class);
    }

    @PostMapping("/addresses")
    public ResponseEntity<String> addAddress(@RequestBody Map<String, Object> address) {
        return restTemplate.postForEntity(USUARIOS_URL + "/addresses", address, String.class);
    }

    @GetMapping("/addresses/user/{userId}")
    public ResponseEntity<String> getAddressByUserId(@PathVariable int userId) {
        return restTemplate.getForEntity(USUARIOS_URL + "/addresses/user/" + userId, String.class);
    }

    @PutMapping("/addresses/user/{userId}")
    public ResponseEntity<Void> updateAddress(@PathVariable int userId, @RequestBody Map<String, Object> address) {
        restTemplate.put(USUARIOS_URL + "/addresses/user/" + userId, address);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/addresses/user/{userId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable int userId) {
        restTemplate.delete(USUARIOS_URL + "/addresses/user/" + userId);
        return ResponseEntity.ok().build();
    }

    // --- Login y Register ---

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, Object> credentials) {
        return restTemplate.postForEntity(USUARIOS_URL + "/login", credentials, String.class);
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, Object> user) {
        return restTemplate.postForEntity(USUARIOS_URL + "/register", user, String.class);
    }
     // --- PRODUCTOS ---

    @GetMapping("/products")
    public ResponseEntity<String> getAllProducts() {
        return restTemplate.getForEntity(PRODUCTOS_URL, String.class);
    }

    @PostMapping("/products")
    public ResponseEntity<String> createProduct(@RequestBody String productJson) {
        return restTemplate.postForEntity(PRODUCTOS_URL, getHttpEntity(productJson), String.class);
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<String> getProductById(@PathVariable int id) {
        return restTemplate.getForEntity(PRODUCTOS_URL + "/" + id, String.class);
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Void> updateProduct(@PathVariable int id, @RequestBody String productJson) {
        restTemplate.put(PRODUCTOS_URL + "/" + id, getHttpEntity(productJson));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int id) {
        restTemplate.delete(PRODUCTOS_URL + "/" + id);
        return ResponseEntity.ok().build();
    }

    // --- INVENTARIO ---

    @GetMapping("/inventories")
    public ResponseEntity<String> getAllInventories() {
        return restTemplate.getForEntity(INVENTARIOS_URL, String.class);
    }

    @PostMapping("/inventories")
    public ResponseEntity<String> createInventory(@RequestBody String inventoryJson) {
        return restTemplate.postForEntity(INVENTARIOS_URL, getHttpEntity(inventoryJson), String.class);
    }

    @GetMapping("/inventories/{productId}")
    public ResponseEntity<String> getInventoryByProductId(@PathVariable int productId) {
        return restTemplate.getForEntity(INVENTARIOS_URL + "/" + productId, String.class);
    }

    @PutMapping("/inventories/{productId}")
    public ResponseEntity<Void> updateInventory(@PathVariable int productId, @RequestBody String inventoryJson) {
        restTemplate.put(INVENTARIOS_URL + "/" + productId, getHttpEntity(inventoryJson));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/inventories/{productId}")
    public ResponseEntity<Void> deleteInventory(@PathVariable int productId) {
        restTemplate.delete(INVENTARIOS_URL + "/" + productId);
        return ResponseEntity.ok().build();
    }

    // --- CATEGORIAS ---

    @GetMapping("/categories")
    public ResponseEntity<String> getAllCategories() {
        return restTemplate.getForEntity(CATEGORIAS_URL, String.class);
    }

    @PostMapping("/categories")
    public ResponseEntity<String> createCategory(@RequestBody String categoryJson) {
        return restTemplate.postForEntity(CATEGORIAS_URL, getHttpEntity(categoryJson), String.class);
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<String> getCategoryById(@PathVariable int id) {
        return restTemplate.getForEntity(CATEGORIAS_URL + "/" + id, String.class);
    }

    @PutMapping("/categories/{id}")
    public ResponseEntity<Void> updateCategory(@PathVariable int id, @RequestBody String categoryJson) {
        restTemplate.put(CATEGORIAS_URL + "/" + id, getHttpEntity(categoryJson));
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/categories/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable int id) {
        restTemplate.delete(CATEGORIAS_URL + "/" + id);
        return ResponseEntity.ok().build();
    }

    // --- Auxiliar ---
    private HttpEntity<String> getHttpEntity(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<>(body, headers);
    }
    @GetMapping("/ordenes")
    public ResponseEntity<?> getAllOrdenes(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int limit) {
        String url = baseUrl + "/ordenes?page=" + page + "&limit=" + limit;
        return restTemplate.exchange(url, HttpMethod.GET, null, Object.class);
    }

    @GetMapping("/ordenes/{id}")
    public ResponseEntity<?> getOrdenById(@PathVariable String id) {
        String url = baseUrl + "/ordenes/" + id;
        return restTemplate.exchange(url, HttpMethod.GET, null, Object.class);
    }

    @PostMapping("/ordenes")
    public ResponseEntity<?> createOrden(@RequestBody Object ordenData) {
        String url = baseUrl + "/ordenes";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(ordenData, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
    }

    @GetMapping("/pagos")
    public ResponseEntity<?> getAllPagos() {
        String url = baseUrl + "/pagos";
        return restTemplate.exchange(url, HttpMethod.GET, null, Object.class);
    }

    @PostMapping("/pagos")
    public ResponseEntity<?> createPago(@RequestBody Object pagoData) {
        String url = baseUrl + "/pagos";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> request = new HttpEntity<>(pagoData, headers);
        return restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
    }

    @GetMapping("/pagos/orden/{ordenId}")
    public ResponseEntity<?> getPagosByOrdenId(@PathVariable String ordenId) {
        String url = baseUrl + "/pagos/orden/" + ordenId;
        return restTemplate.exchange(url, HttpMethod.GET, null, Object.class);
    }
}
