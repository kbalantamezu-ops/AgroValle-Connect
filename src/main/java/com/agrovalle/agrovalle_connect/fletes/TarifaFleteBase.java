package com.agrovalle.agrovalle_connect.fletes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

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
    this.municipios = municipios.stream()
        .map(TarifaFleteBase::normalizar)
        .collect(Collectors.toUnmodifiableSet());
  }

  @Override
  public String zona() {
    return zona;
  }

  @Override
  public boolean aplicaA(String municipio) {
    return municipio != null && municipios.contains(normalizar(municipio));
  }

  @Override
  public BigDecimal calcular(BigDecimal pesoKg) {
    return tarifaBase.add(tarifaPorKilo.multiply(pesoKg)).setScale(2, RoundingMode.HALF_UP);
  }

  /** Unifica mayúsculas, espacios y tildes para que TULUA y Tuluá refieran al mismo municipio. */
  private static String normalizar(String municipio) {
    String sinTildes = Normalizer.normalize(municipio, Normalizer.Form.NFD)
        .replaceAll("\\p{M}", "");
    return sinTildes.trim().toLowerCase(Locale.ROOT);
  }
}
