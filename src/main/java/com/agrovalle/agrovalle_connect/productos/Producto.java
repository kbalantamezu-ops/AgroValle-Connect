package com.agrovalle.agrovalle_connect.productos;

import com.agrovalle.agrovalle_connect.agricultores.Agricultor;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Producto publicado por un agricultor (HU-02). */
@Entity
@Table(name = "productos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Producto {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "agricultor_id", nullable = false)
  private Agricultor agricultor;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "categoria_id", nullable = false)
  private Categoria categoria;

  @Column(nullable = false, length = 120)
  private String nombre;

  @Column(length = 500)
  private String descripcion;

  @Column(nullable = false, precision = 12, scale = 2)
  private BigDecimal precio;

  @Column(name = "cantidad_disponible", nullable = false)
  private Integer cantidadDisponible;

  @Column(nullable = false, length = 30)
  private String unidad;

  @Column(name = "foto_url", length = 500)
  private String fotoUrl;

  @Column(name = "fecha_publicacion", nullable = false)
  private LocalDateTime fechaPublicacion;
}
