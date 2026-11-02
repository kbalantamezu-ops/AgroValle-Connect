package com.agrovalle.agrovalle_connect.fletes;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

/** Datos de entrada para calcular un flete. */
public record FleteRequest(
    @NotBlank String municipio,
    @NotNull @DecimalMin(value = "0.1", message = "debe ser mayor o igual a 0.1") BigDecimal pesoKg) {
}
