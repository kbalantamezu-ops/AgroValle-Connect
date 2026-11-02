package com.agrovalle.agrovalle_connect.agricultores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.agrovalle_connect.excepciones.ReglaNegocioException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AgricultorServiceTest {

  @Mock
  private AgricultorRepository repositorio;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Spy
  private AgricultorMapper mapper = new AgricultorMapper();

  @InjectMocks
  private AgricultorService servicio;

  private AgricultorRequest crearRequest() {
    return new AgricultorRequest("Juan Pérez", "Palmira", "1112223334", "juan@correo.com", "clave1234");
  }

  @Test
  void registrarGuardaAgricultorConContrasenaCifrada() {
    when(passwordEncoder.encode("clave1234")).thenReturn("HASH");
    when(repositorio.save(any(Agricultor.class))).thenAnswer(inv -> {
      Agricultor a = inv.getArgument(0);
      a.setId(1L);
      return a;
    });

    AgricultorResponse respuesta = servicio.registrar(crearRequest());

    ArgumentCaptor<Agricultor> captor = ArgumentCaptor.forClass(Agricultor.class);
    verify(repositorio).save(captor.capture());
    assertEquals("HASH", captor.getValue().getPasswordHash());
    assertNotEquals("clave1234", captor.getValue().getPasswordHash());
    assertEquals(1L, respuesta.id());
    assertEquals("juan@correo.com", respuesta.email());
  }

  @Test
  void registrarRechazaCedulaDuplicada() {
    when(repositorio.existsByCedula("1112223334")).thenReturn(true);

    assertThrows(ReglaNegocioException.class, () -> servicio.registrar(crearRequest()));

    verify(repositorio, never()).save(any());
  }

  @Test
  void registrarRechazaCorreoDuplicado() {
    when(repositorio.existsByEmail("juan@correo.com")).thenReturn(true);

    assertThrows(ReglaNegocioException.class, () -> servicio.registrar(crearRequest()));

    verify(repositorio, never()).save(any());
  }
}
