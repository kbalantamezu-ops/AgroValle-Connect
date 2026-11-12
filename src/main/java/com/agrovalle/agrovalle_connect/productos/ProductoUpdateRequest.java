package com.agrovalle.agrovalle_connect.productos;

import com.agrovalle.agrovalle_connect.catalogo.Categoria;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/** Datos editables de una publicacion. El agricultor propietario no puede cambiarse. */
public record ProductoUpdateRequest(
        @NotBlank(message = "El nombre del producto es obligatorio")
        @Size(max = 120, message = "El nombre no puede superar 120 caracteres")
        String nombre,
        @NotNull(message = "La categoría es obligatoria")
        Categoria categoria,
        @NotNull(message = "La cantidad disponible es obligatoria")
        @DecimalMin(value = "0.0", message = "La cantidad no puede ser negativa")
        Double cantidadDisponible,
        @NotNull(message = "El precio unitario es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor que cero")
        BigDecimal precioUnitario,
        @Size(max = 20, message = "La unidad no puede superar 20 caracteres")
        String unidad,
        @NotNull(message = "La fecha de cosecha es obligatoria")
        LocalDate fechaCosecha) {
}
