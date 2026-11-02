package com.agrovalle.agrovalle_connect.fletes;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.agrovalle_connect.excepciones.RecursoNoEncontradoException;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FleteServiceTest {

  @Mock
  private EstrategiaFlete primera;

  @Mock
  private EstrategiaFlete segunda;

  private FleteService servicio;

  @BeforeEach
  void preparar() {
    servicio = new FleteService(List.of(primera, segunda));
  }

  @Test
  void usaLaPrimeraEstrategiaQueAplica() {
    BigDecimal peso = new BigDecimal("10");
    when(primera.aplicaA("Cali")).thenReturn(true);
    when(primera.zona()).thenReturn("CERCANA");
    when(primera.calcular(peso)).thenReturn(new BigDecimal("8000.00"));

    FleteResponse respuesta = servicio.calcular(new FleteRequest("Cali", peso));

    assertEquals("CERCANA", respuesta.zona());
    assertEquals(new BigDecimal("8000.00"), respuesta.costo());
    verify(primera).calcular(peso);
  }

  @Test
  void pasaALaSiguienteEstrategiaSiLaPrimeraNoAplica() {
    BigDecimal peso = new BigDecimal("5");
    when(primera.aplicaA("Argelia")).thenReturn(false);
    when(segunda.aplicaA("Argelia")).thenReturn(true);
    when(segunda.zona()).thenReturn("LEJANA");
    when(segunda.calcular(peso)).thenReturn(new BigDecimal("24000.00"));

    FleteResponse respuesta = servicio.calcular(new FleteRequest("Argelia", peso));

    assertEquals("LEJANA", respuesta.zona());
    assertEquals(new BigDecimal("24000.00"), respuesta.costo());
  }

  @Test
  void falla404SiNingunaEstrategiaAplica() {
    when(primera.aplicaA("Nowhere")).thenReturn(false);
    when(segunda.aplicaA("Nowhere")).thenReturn(false);
    FleteRequest request = new FleteRequest("Nowhere", BigDecimal.ONE);

    assertThrows(RecursoNoEncontradoException.class, () -> servicio.calcular(request));
  }
}
