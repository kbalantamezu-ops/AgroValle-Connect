package com.agrovalle.agrovalle_connect.productos;

import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/precios")
public class PrecioController {

    private final PrecioService precioService;

    public PrecioController(PrecioService precioService) {
        this.precioService = precioService;
    }

    @GetMapping("/promedio")
    public PrecioPromedioResponse promedio(@RequestParam Categoria categoria) {
        return precioService.promedioPorCategoria(categoria);
    }
}
