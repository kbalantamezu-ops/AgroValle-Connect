package com.agrovalle.agrovalle_connect.auth;

import com.agrovalle.agrovalle_connect.comun.ApiException;
import com.agrovalle.agrovalle_connect.pedidos.ItemPedido;
import com.agrovalle.agrovalle_connect.pedidos.Pedido;
import com.agrovalle.agrovalle_connect.usuarios.Rol;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/** Reglas de propiedad reutilizables por controladores y servicios. */
@Service
public class AutorizacionService {

    public void exigirAutenticado(UsuarioPrincipal principal) {
        if (principal == null) {
            throw new ApiException(HttpStatus.UNAUTHORIZED,
                    "Debes iniciar sesion para acceder a este recurso");
        }
    }

    public void exigirPropietarioOAdmin(UsuarioPrincipal principal, Long agricultorId) {
        exigirAutenticado(principal);
        if (principal.esAdmin()) {
            return;
        }
        if (!principal.esAgricultor() || !Objects.equals(principal.agricultorId(), agricultorId)) {
            throw new ApiException(HttpStatus.FORBIDDEN,
                    "Solo puedes gestionar tu propio perfil de agricultor");
        }
    }

    public void exigirAccesoPedido(UsuarioPrincipal principal, Pedido pedido) {
        exigirAutenticado(principal);
        if (principal.esAdmin()) {
            return;
        }
        if (principal.rol() == Rol.COMPRADOR
                && Objects.equals(pedido.getUsuarioId(), principal.id())) {
            return;
        }
        if (principal.esAgricultor() && esPedidoDelAgricultor(principal, pedido)) {
            return;
        }
        throw new ApiException(HttpStatus.FORBIDDEN,
                "No tienes permisos para consultar este pedido");
    }

    public void exigirGestionPedido(UsuarioPrincipal principal, Pedido pedido) {
        exigirAutenticado(principal);
        if (principal.esAdmin()) {
            return;
        }
        if (principal.esAgricultor() && esPedidoDelAgricultor(principal, pedido)) {
            return;
        }
        throw new ApiException(HttpStatus.FORBIDDEN,
                "Solo el agricultor del pedido o un administrador pueden cambiar su estado");
    }

    private boolean esPedidoDelAgricultor(UsuarioPrincipal principal, Pedido pedido) {
        if (principal.agricultorId() == null) {
            return false;
        }
        return pedido.getItems().stream()
                .map(ItemPedido::getProducto)
                .anyMatch(producto -> Objects.equals(
                        producto.getAgricultor().getId(), principal.agricultorId()));
    }
}
