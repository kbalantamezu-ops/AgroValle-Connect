package com.agrovalle.agrovalle_connect.fletes;

import java.math.BigDecimal;

/** Estrategia para calcular el flete de un pedido según la zona del municipio (patrón Strategy). */
public interface EstrategiaFlete {

  String zona();

  boolean aplicaA(String municipio);

  BigDecimal calcular(BigDecimal pesoKg);
}
