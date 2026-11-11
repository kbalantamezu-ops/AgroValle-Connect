package com.agrovalle.agrovalle_connect.productos;

import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import com.agrovalle.agrovalle_connect.comun.ApiException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrecioService {

    private final ProductoRepository productoRepository;

    public PrecioService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    @Transactional(readOnly = true)
    public PrecioPromedioResponse promedioPorCategoria(Categoria categoria) {
        List<Producto> productos = productoRepository.findByCategoria(categoria);
        if (productos.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND,
                    "No hay productos publicados en la categoría " + categoria.getEtiqueta());
        }
        BigDecimal suma = productos.stream()
                .map(Producto::getPrecioUnitario)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal promedio = suma.divide(BigDecimal.valueOf(productos.size()), 2, RoundingMode.HALF_UP);
        return new PrecioPromedioResponse(categoria.name(), promedio, productos.size(), "COP");
    }
}
