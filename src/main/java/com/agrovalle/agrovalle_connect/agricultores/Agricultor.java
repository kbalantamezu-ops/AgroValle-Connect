package com.agrovalle.agrovalle_connect.agricultores;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Agricultor que publica productos (HU-01). */
@Entity
@Table(name = "agricultores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agricultor {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(name = "ubicacion_valle", nullable = false, length = 120)
  private String ubicacionValle;

  @Column(nullable = false, unique = true, length = 10)
  private String cedula;

  @Column(nullable = false, unique = true, length = 150)
  private String email;

  @Column(name = "password_hash", nullable = false)
  private String passwordHash;
}
