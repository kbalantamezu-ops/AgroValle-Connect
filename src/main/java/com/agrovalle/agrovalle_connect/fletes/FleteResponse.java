package com.agrovalle.agrovalle_connect.fletes;

import java.math.BigDecimal;

/** Resultado del cálculo del flete. */
public record FleteResponse(String municipio, String zona, BigDecimal pesoKg, BigDecimal costo) {
}
