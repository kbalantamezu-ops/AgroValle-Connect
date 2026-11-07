package com.agrovalle.agrovalle_connect.fletes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FleteEstrategiasTest {

  @Test
  void zonaCercanaCalculaBasePorKilo() {
    EstrategiaFlete estrategia = new FleteZonaCercanaStrategy();

    assertEquals("CERCANA", estrategia.zona());
    assertTrue(estrategia.aplicaA("  Palmira "));
    assertFalse(estrategia.aplicaA("Tuluá"));
    assertEquals(new BigDecimal("8000.00"), estrategia.calcular(new BigDecimal("10")));
  }

  @Test
  void zonaIntermediaCalculaBasePorKilo() {
    EstrategiaFlete estrategia = new FleteZonaIntermediaStrategy();

    assertEquals("INTERMEDIA", estrategia.zona());
    assertTrue(estrategia.aplicaA("Tuluá"));
    assertFalse(estrategia.aplicaA("Cali"));
    assertEquals(new BigDecimal("17000.00"), estrategia.calcular(new BigDecimal("10")));
  }

  @Test
  void zonaLejanaAplicaAcualquierMunicipio() {
    EstrategiaFlete estrategia = new FleteZonaLejanaStrategy();

    assertEquals("LEJANA", estrategia.zona());
    assertTrue(estrategia.aplicaA("Argelia"));
    assertEquals(new BigDecimal("28000.00"), estrategia.calcular(new BigDecimal("10")));
  }
}
