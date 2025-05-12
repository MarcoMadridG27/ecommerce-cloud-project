package com.tienda.orquestador.controller;

import com.tienda.orquestador.model.ResumenCompra;
import com.tienda.orquestador.service.ResumenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/")
public class ResumenController {

    @Autowired
    private ResumenService resumenService;


    @GetMapping
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("echo test ok");
    }

    @GetMapping("/resumen/{ordenId}")
    public ResponseEntity<ResumenCompra> obtenerResumen(@PathVariable String ordenId) {
        ResumenCompra resumen = resumenService.generarResumen(ordenId);
        return ResponseEntity.ok(resumen);
    }

   
}
