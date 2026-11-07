package com.agrovalle.agrovalle_connect.agricultores;

<<<<<<< HEAD
import com.agrovalle.agrovalle_connect.catalogo.Municipio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
=======
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
>>>>>>> origin/develop
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
<<<<<<< HEAD
import java.time.LocalDateTime;

@Entity
@Table(name = "agricultores")
public class Agricultor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false, unique = true, length = 20)
    private String cedula;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Municipio municipio;

    @Column(length = 20)
    private String telefono;

    @Column(length = 120)
    private String email;

    @Column(length = 120)
    private String finca;

    @Column(nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    protected Agricultor() {
    }

    public Agricultor(String nombre, String cedula, Municipio municipio, String telefono,
            String email, String finca) {
        this.nombre = nombre;
        this.cedula = cedula;
        this.municipio = municipio;
        this.telefono = telefono;
        this.email = email;
        this.finca = finca;
        this.fechaRegistro = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCedula() {
        return cedula;
    }

    public Municipio getMunicipio() {
        return municipio;
    }

    public void setMunicipio(Municipio municipio) {
        this.municipio = municipio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFinca() {
        return finca;
    }

    public void setFinca(String finca) {
        this.finca = finca;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }
=======
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
>>>>>>> origin/develop
}
