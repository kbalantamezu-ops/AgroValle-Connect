package com.agrovalle.agrovalle_connect.agricultores;

import com.agrovalle.agrovalle_connect.auth.AutorizacionService;
import com.agrovalle.agrovalle_connect.auth.UsuarioPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/agricultores")
public class AgricultorController {

    private final AgricultorService agricultorService;

    private final PanelAgricultorService panelAgricultorService;

    private final AutorizacionService autorizacionService;

    public AgricultorController(AgricultorService agricultorService,
            PanelAgricultorService panelAgricultorService,
            AutorizacionService autorizacionService) {
        this.agricultorService = agricultorService;
        this.panelAgricultorService = panelAgricultorService;
        this.autorizacionService = autorizacionService;
    }

    @PostMapping
    public ResponseEntity<AgricultorResponse> registrar(@Valid @RequestBody AgricultorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(agricultorService.registrar(request));
    }

    @GetMapping
    public List<AgricultorResponse> listar() {
        return agricultorService.listar();
    }

    @GetMapping("/{id}")
    public AgricultorResponse obtener(@PathVariable Long id) {
        return agricultorService.obtener(id);
    }

    @PutMapping("/{id}")
    public AgricultorResponse actualizar(@AuthenticationPrincipal UsuarioPrincipal principal,
            @PathVariable Long id, @Valid @RequestBody AgricultorRequest request) {
        autorizacionService.exigirPropietarioOAdmin(principal, id);
        return agricultorService.actualizar(id, request);
    }

    @GetMapping("/{id}/panel")
    public PanelAgricultorResponse panel(@AuthenticationPrincipal UsuarioPrincipal principal,
            @PathVariable Long id) {
        autorizacionService.exigirPropietarioOAdmin(principal, id);
        return panelAgricultorService.obtener(id);
    }
}
