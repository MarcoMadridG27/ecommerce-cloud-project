package com.tienda.orquestador.controller;

import com.tienda.orquestador.model.ResumenCompra;
import com.tienda.orquestador.service.ResumenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/resumen")
public class ResumenController {

    @Autowired
    private ResumenService resumenService;

    @GetMapping("/{ordenId}")
    public ResponseEntity<ResumenCompra> obtenerResumen(@PathVariable String ordenId) {
        ResumenCompra resumen = resumenService.generarResumen(ordenId);
        return ResponseEntity.ok(resumen);
    }
}
