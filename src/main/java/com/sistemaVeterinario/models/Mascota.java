package com.sistemaVeterinario.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "mascotas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una mascota registrada en el sistema")
public class Mascota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID único de la mascota", example = "1")
    @Column(name = "id_mascota")
    private Integer idMascota;

    @Column(length = 45, nullable = false)
    @Schema(description = "Nombre de la mascota", example = "Felix")
    private String nombre;

    @Column(length = 45, nullable = false)
    @Schema(description = "Especie de la mascota", example = "Gato")
    private String especie;

    @Column(length = 45, nullable = false)
    @Schema(description = "Raza de la mascota", example = "Persa")
    private String raza;

    @Column(name = "fecha_nacimiento", nullable = false)
    @Schema(description = "Fecha de nacimiento de la mascota", example = "2020-08-15")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexo", nullable = false)
    @Schema(description = "Sexo de la mascota", example = "Macho")
    private SexoMascota sexo;

    @Schema(description = "Enum que representa el sexo de la mascota")
    public enum SexoMascota {
        Macho, Hembra
    }

    @ManyToOne
    @JoinColumn(name = "id_propietario", nullable = false)
    @Schema(description = "Usuario propietario de la mascota")
    private Usuario propietario;

    @OneToMany(mappedBy = "mascota", cascade = CascadeType.ALL)
    @Schema(description = "Citas asociadas a esta mascota")
    private Set<Cita> citas = new HashSet<>();

}