package com.agrovalle.agrovalle_connect.productos;

import com.agrovalle.agrovalle_connect.auth.AutorizacionService;
import com.agrovalle.agrovalle_connect.auth.UsuarioPrincipal;
import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    private final ProductoService productoService;

    private final AutorizacionService autorizacionService;

    public ProductoController(ProductoService productoService,
            AutorizacionService autorizacionService) {
        this.productoService = productoService;
        this.autorizacionService = autorizacionService;
    }

    @PostMapping
    public ResponseEntity<ProductoResponse> publicar(
            @AuthenticationPrincipal UsuarioPrincipal principal,
            @Valid @RequestBody ProductoRequest request) {
        autorizacionService.exigirPropietarioOAdmin(principal, request.agricultorId());
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.publicar(request));
    }

    @GetMapping
    public List<ProductoResponse> listar(@RequestParam(required = false) String q,
            @RequestParam(required = false) Municipio municipio,
            @RequestParam(required = false) Categoria categoria,
            @RequestParam(required = false) Boolean disponible) {
        return productoService.listar(q, municipio, categoria, disponible);
    }

    @GetMapping("/{id}")
    public ProductoResponse obtener(@PathVariable Long id) {
        return productoService.obtener(id);
    }
}
