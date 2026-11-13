package com.agrovalle.agrovalle_connect.comun;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Endpoint publico de salud usado por el pipeline de despliegue y el monitoreo. */
@RestController
@RequestMapping("/api/v1/estado")
public class EstadoController {

    private final DataSource dataSource;

    private final Environment environment;

    public EstadoController(DataSource dataSource, Environment environment) {
        this.dataSource = dataSource;
        this.environment = environment;
    }

    @GetMapping
    public ResponseEntity<Map<String, String>> estado() {
        Map<String, String> respuesta = new LinkedHashMap<>();
        respuesta.put("aplicacion", "AgroValle Connect");
        respuesta.put("perfil", perfilActivo());

        try (Connection conexion = dataSource.getConnection()) {
            if (conexion.isValid(2)) {
                respuesta.put("estado", "OK");
                respuesta.put("baseDatos", conexion.getMetaData().getDatabaseProductName()
                        + " Connected");
                return ResponseEntity.ok(respuesta);
            }
        } catch (SQLException excepcion) {
            respuesta.put("detalle", "No se pudo validar la conexion a la base de datos");
        }

        respuesta.put("estado", "ERROR");
        respuesta.put("baseDatos", "Unavailable");
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(respuesta);
    }

    private String perfilActivo() {
        String[] perfiles = environment.getActiveProfiles();
        if (perfiles.length == 0) {
            return "default";
        }
        return String.join(",", perfiles);
    }
}
