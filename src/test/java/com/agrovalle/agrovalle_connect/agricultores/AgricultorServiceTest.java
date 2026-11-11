package com.agrovalle.agrovalle_connect.agricultores;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import com.agrovalle.agrovalle_connect.comun.ApiException;
import com.agrovalle.agrovalle_connect.resenas.ResenaRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AgricultorServiceTest {

    @Mock
    private AgricultorRepository agricultorRepository;

    @Mock
    private ResenaRepository resenaRepository;

    @InjectMocks
    private AgricultorService servicio;

    private AgricultorRequest crearRequest() {
        return new AgricultorRequest("Juan Pérez", "1112223334", Municipio.PALMIRA,
                "3001234567", "juan@correo.com", "Finca La Esperanza");
    }

    @Test
    void registrarGuardaAgricultorYDevuelveRespuesta() {
        when(agricultorRepository.save(any(Agricultor.class))).thenAnswer(inv -> {
            Agricultor agricultor = inv.getArgument(0);
            ReflectionTestUtils.setField(agricultor, "id", 1L);
            return agricultor;
        });
        when(resenaRepository.promedioCalificacion(1L)).thenReturn(Optional.of(4.5));

        AgricultorResponse respuesta = servicio.registrar(crearRequest());

        assertEquals(1L, respuesta.id());
        assertEquals("Juan Pérez", respuesta.nombre());
        assertEquals("PALMIRA", respuesta.municipio());
        assertEquals("Finca La Esperanza", respuesta.finca());
        assertEquals(4.5, respuesta.promedioCalificacion(), 0.001);
    }

    @Test
    void registrarRechazaCedulaDuplicada() {
        when(agricultorRepository.existsByCedula("1112223334")).thenReturn(true);

        ApiException error = assertThrows(ApiException.class, () -> servicio.registrar(crearRequest()));

        assertEquals(HttpStatus.CONFLICT, error.getStatus());
        verify(agricultorRepository, never()).save(any());
    }

    @Test
    void obtenerAgricultorInexistenteLanza404() {
        when(agricultorRepository.findById(99L)).thenReturn(Optional.empty());

        ApiException error = assertThrows(ApiException.class, () -> servicio.obtener(99L));

        assertEquals(HttpStatus.NOT_FOUND, error.getStatus());
    }
}
