package com.agrovalle.agrovalle_connect.auth;

import com.agrovalle.agrovalle_connect.usuarios.Rol;
import java.security.Principal;

/** Identidad autenticada extraida del token JWT. */
public record UsuarioPrincipal(Long id, String email, Rol rol, Long agricultorId)
        implements Principal {

    @Override
    public String getName() {
        return email;
    }

    public boolean esAdmin() {
        return rol == Rol.ADMIN;
    }

    public boolean esAgricultor() {
        return rol == Rol.AGRICULTOR;
    }
}
