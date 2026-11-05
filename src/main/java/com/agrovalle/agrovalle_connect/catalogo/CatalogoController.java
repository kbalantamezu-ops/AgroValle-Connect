package com.agrovalle.agrovalle_connect.catalogo;

import java.util.Arrays;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoController {

    @GetMapping("/municipios")
    public List<OpcionCatalogo> municipios() {
        return Arrays.stream(Municipio.values())
                .map(m -> new OpcionCatalogo(m.name(), m.getEtiqueta()))
                .toList();
    }

    @GetMapping("/categorias")
    public List<OpcionCatalogo> categorias() {
        return Arrays.stream(Categoria.values())
                .map(c -> new OpcionCatalogo(c.name(), c.getEtiqueta()))
                .toList();
    }
}
