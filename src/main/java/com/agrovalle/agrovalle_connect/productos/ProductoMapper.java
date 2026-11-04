package com.agrovalle.agrovalle_connect.productos;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class ProductoMapper {

  public Producto toEntity(ProductoRequest request, Agricultor agricultor, Categoria categoria) {
    return Producto.builder()
        .agricultor(agricultor)
        .categoria(categoria)
        .nombre(request.nombre())
        .descripcion(request.descripcion())
        .precio(request.precio())
        .cantidadDisponible(request.cantidadDisponible())
        .unidad(request.unidad())
        .fotoUrl(request.fotoUrl())
        .fechaPublicacion(LocalDateTime.now())
        .build();
  }

  public ProductoResponse toResponse(Producto producto) {
    return new ProductoResponse(producto.getId(), producto.getAgricultor().getId(),
        producto.getCategoria().getId(), producto.getNombre(), producto.getDescripcion(), producto.getPrecio(),
        producto.getCantidadDisponible(), producto.getUnidad(), producto.getFotoUrl(),
        producto.getFechaPublicacion());
  }
}
