package com.agrovalle.agrovalle_connect.fletes;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/** Municipios cercanos al área metropolitana de Cali. */
@Component
@Order(1)
public class FleteZonaCercanaStrategy extends TarifaFleteBase {

  public FleteZonaCercanaStrategy() {
    super("CERCANA", new BigDecimal("5000"), new BigDecimal("300"),
        Set.of("cali", "yumbo", "palmira", "jamundí", "candelaria"));
  }
}
