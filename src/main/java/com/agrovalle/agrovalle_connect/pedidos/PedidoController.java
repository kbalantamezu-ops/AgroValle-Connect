package com.agrovalle.agrovalle_connect.pedidos;

import com.agrovalle.agrovalle_connect.auth.UsuarioPrincipal;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;

    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }

    @PostMapping
    public ResponseEntity<PedidoResponse> crear(@AuthenticationPrincipal UsuarioPrincipal principal,
            @Valid @RequestBody PedidoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.crear(request, principal));
    }

    @GetMapping
    public List<PedidoResponse> listar(@AuthenticationPrincipal UsuarioPrincipal principal) {
        return pedidoService.listar(principal);
    }

    @GetMapping("/{id}")
    public PedidoResponse obtener(@AuthenticationPrincipal UsuarioPrincipal principal,
            @PathVariable Long id) {
        return pedidoService.obtener(id, principal);
    }

    @PatchMapping("/{id}/estado")
    public PedidoResponse cambiarEstado(@AuthenticationPrincipal UsuarioPrincipal principal,
            @PathVariable Long id, @Valid @RequestBody EstadoRequest request) {
        return pedidoService.cambiarEstado(id, request, principal);
    }

    @GetMapping("/{id}/notificaciones")
    public List<NotificacionResponse> listarNotificaciones(
            @AuthenticationPrincipal UsuarioPrincipal principal, @PathVariable Long id) {
        return pedidoService.listarNotificaciones(id, principal);
    }
}
