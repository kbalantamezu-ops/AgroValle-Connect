package com.agrovalle.agrovalle_connect.fletes;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Municipios del centro del Valle. */
@Component
@Order(2)
public class FleteZonaIntermediaStrategy extends TarifaFleteBase {

  public FleteZonaIntermediaStrategy() {
    super("INTERMEDIA", new BigDecimal("12000"), new BigDecimal("500"),
        Set.of("tuluá", "buga", "cartago", "sevilla", "roldanillo"));
  }
}
