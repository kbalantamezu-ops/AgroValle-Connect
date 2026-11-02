package com.agrovalle.agrovalle_connect.productos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

/** Datos de entrada para publicar un producto. El agricultor llega en el cuerpo (sin JWT por ahora). */
public record ProductoRequest(
    @NotNull Long agricultorId,
    @NotNull Long categoriaId,
    @NotBlank @Size(max = 120) String nombre,
    @Size(max = 500) String descripcion,
    @NotNull @DecimalMin(value = "0.01", message = "debe ser mayor que cero") BigDecimal precio,
    @NotNull @Min(1) Integer cantidadDisponible,
    @NotBlank @Size(max = 30) String unidad,
    @Size(max = 500) String fotoUrl) {
}
