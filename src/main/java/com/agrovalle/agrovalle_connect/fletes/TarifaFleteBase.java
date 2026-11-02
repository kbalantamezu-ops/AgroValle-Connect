package com.agrovalle.agrovalle_connect.fletes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;
import java.util.Set;

/** Base común: costo = tarifa base + tarifa por kilo * peso. Las tarifas son valores de ejemplo. */
public abstract class TarifaFleteBase implements EstrategiaFlete {

  private final String zona;
  private final BigDecimal tarifaBase;
  private final BigDecimal tarifaPorKilo;
  private final Set<String> municipios;

  protected TarifaFleteBase(String zona, BigDecimal tarifaBase, BigDecimal tarifaPorKilo, Set<String> municipios) {
    this.zona = zona;
    this.tarifaBase = tarifaBase;
    this.tarifaPorKilo = tarifaPorKilo;
    this.municipios = municipios;
  }

  @Override
  public String zona() {
    return zona;
  }

  @Override
  public boolean aplicaA(String municipio) {
    return municipios.contains(municipio.trim().toLowerCase(Locale.ROOT));
  }

  @Override
  public BigDecimal calcular(BigDecimal pesoKg) {
    return tarifaBase.add(tarifaPorKilo.multiply(pesoKg)).setScale(2, RoundingMode.HALF_UP);
  }
}
