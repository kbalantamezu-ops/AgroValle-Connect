package com.agrovalle.agrovalle_connect.usuarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Cuenta de acceso a la plataforma. Puede ser comprador, agricultor o administrador. */
@Entity
@Table(name = "usuarios")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;

  @Column(length = 20)
  private String telefono;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @Builder.Default
  private Rol rol = Rol.COMPRADOR;

  @Column(name = "agricultor_id", unique = true)
  private Long agricultorId;
}
