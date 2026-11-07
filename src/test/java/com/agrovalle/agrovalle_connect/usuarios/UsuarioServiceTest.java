package com.agrovalle.agrovalle_connect.usuarios;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
class UsuarioServiceTest {

  @Mock
  private UsuarioRepository repositorio;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Spy
  private UsuarioMapper mapper = new UsuarioMapper();

  @InjectMocks
  private UsuarioService servicio;

  private UsuarioRequest crearRequest() {
    return new UsuarioRequest("Ana Gómez", "ana@correo.com", "clave1234", "3001234567");
  }

  @Test
  void registrarGuardaUsuarioConContrasenaCifrada() {
    when(passwordEncoder.encode("clave1234")).thenReturn("HASH");
    when(repositorio.save(any(Usuario.class))).thenAnswer(inv -> {
      Usuario u = inv.getArgument(0);
      u.setId(7L);
      return u;
    });

    UsuarioResponse respuesta = servicio.registrar(crearRequest());

    ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
    verify(repositorio).save(captor.capture());
    assertEquals("HASH", captor.getValue().getPasswordHash());
    assertEquals(7L, respuesta.id());
    assertEquals("3001234567", respuesta.telefono());
  }

  @Test
  void registrarRechazaCorreoDuplicado() {
    when(repositorio.existsByEmail("ana@correo.com")).thenReturn(true);

    assertThrows(ReglaNegocioException.class, () -> servicio.registrar(crearRequest()));

    verify(repositorio, never()).save(any());
  }
}
