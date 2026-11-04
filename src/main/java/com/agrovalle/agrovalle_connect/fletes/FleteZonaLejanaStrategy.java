package com.agrovalle.agrovalle_connect.fletes;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Estrategia por defecto: cualquier otro municipio. Siempre va de última en el orden. */
@Component
@Order(Integer.MAX_VALUE)
public class FleteZonaLejanaStrategy extends TarifaFleteBase {

  public FleteZonaLejanaStrategy() {
    super("LEJANA", new BigDecimal("20000"), new BigDecimal("800"), Set.of());
  }

  @Override
  public boolean aplicaA(String municipio) {
    return true;
  }
}
