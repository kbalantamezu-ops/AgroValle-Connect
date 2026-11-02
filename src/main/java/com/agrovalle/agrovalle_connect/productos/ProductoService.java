package com.agrovalle.agrovalle_connect.productos;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import com.agrovalle.agrovalle_connect.agricultores.AgricultorRepository;
import com.agrovalle.agrovalle_connect.excepciones.RecursoNoEncontradoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoService {

  private final ProductoRepository productoRepositorio;
  private final AgricultorRepository agricultorRepositorio;
  private final CategoriaRepository categoriaRepositorio;
  private final ProductoMapper mapper;

  /** HU-02: publica un producto de un agricultor existente en una categoría existente. */
  @Transactional
  public ProductoResponse publicar(ProductoRequest request) {
    Agricultor agricultor = agricultorRepositorio.findById(request.agricultorId())
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "No existe el agricultor con id " + request.agricultorId()));
    Categoria categoria = categoriaRepositorio.findById(request.categoriaId())
        .orElseThrow(() -> new RecursoNoEncontradoException(
            "No existe la categoría con id " + request.categoriaId()));
    Producto guardado = productoRepositorio.save(mapper.toEntity(request, agricultor, categoria));
    return mapper.toResponse(guardado);
  }
}
