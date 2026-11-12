package com.agrovalle.agrovalle_connect.productos;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import com.agrovalle.agrovalle_connect.agricultores.AgricultorService;
import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import com.agrovalle.agrovalle_connect.comun.ApiException;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    private final AgricultorService agricultorService;

    public ProductoService(ProductoRepository productoRepository, AgricultorService agricultorService) {
        this.productoRepository = productoRepository;
        this.agricultorService = agricultorService;
    }

    @Transactional
    public ProductoResponse publicar(ProductoRequest request) {
        Agricultor agricultor = agricultorService.buscarEntidad(request.agricultorId());
        Producto producto = new Producto();
        producto.setNombre(request.nombre());
        producto.setCategoria(request.categoria());
        producto.setCantidadDisponible(request.cantidadDisponible());
        producto.setPrecioUnitario(request.precioUnitario());
        producto.setUnidad(unidadDe(request.unidad()));
        producto.setFechaCosecha(request.fechaCosecha());
        producto.setFechaPublicacion(LocalDateTime.now());
        producto.setDisponible(true);
        producto.setAgricultor(agricultor);
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public ProductoResponse actualizar(Long id, ProductoUpdateRequest request) {
        Producto producto = buscarEntidad(id);
        producto.setNombre(request.nombre());
        producto.setCategoria(request.categoria());
        producto.setCantidadDisponible(request.cantidadDisponible());
        producto.setPrecioUnitario(request.precioUnitario());
        producto.setUnidad(unidadDe(request.unidad()));
        producto.setFechaCosecha(request.fechaCosecha());
        producto.setDisponible(request.cantidadDisponible() > 0);
        return toResponse(productoRepository.save(producto));
    }

    @Transactional
    public void eliminar(Long id) {
        Producto producto = buscarEntidad(id);
        if (productoRepository.contarPedidosAsociados(id) > 0) {
            throw new ApiException(HttpStatus.CONFLICT,
                    "No se puede eliminar el producto '" + producto.getNombre()
                            + "' porque tiene pedidos asociados");
        }
        productoRepository.delete(producto);
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listar(String q, Municipio municipio, Categoria categoria,
            Boolean disponible) {
        return productoRepository.buscar(q, municipio, categoria, disponible).stream()
                .map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ProductoResponse obtener(Long id) {
        return toResponse(buscarEntidad(id));
    }

    @Transactional(readOnly = true)
    public List<ProductoResponse> listarPorAgricultor(Long agricultorId) {
        return productoRepository.buscarPorAgricultor(agricultorId).stream()
                .map(this::toResponse).toList();
    }

    public Producto buscarEntidad(Long id) {
        return productoRepository.buscarPorId(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND,
                        "Producto no encontrado con id " + id));
    }

    private String unidadDe(String unidad) {
        return unidad == null || unidad.isBlank() ? "kg" : unidad;
    }

    private ProductoResponse toResponse(Producto producto) {
        Agricultor agricultor = producto.getAgricultor();
        ProductoResponse.AgricultorResumen resumen = new ProductoResponse.AgricultorResumen(
                agricultor.getId(), agricultor.getNombre(), agricultor.getFinca(),
                agricultor.getTelefono(), agricultor.getEmail());
        return new ProductoResponse(producto.getId(), producto.getNombre(), producto.getCategoria().name(),
                producto.getCantidadDisponible(), producto.getPrecioUnitario(), producto.getUnidad(),
                producto.getFechaCosecha(), producto.getFechaPublicacion(), producto.isDisponible(),
                agricultor.getMunicipio().name(), resumen);
    }
}
