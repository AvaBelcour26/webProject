package com.sistemaVeterinario.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="roles")
@Schema(description = "Entidad que representa un rol dentro del sistema")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol")
    @Schema(description = "ID único del rol", example = "1")
    private Integer idRol;

    @Column(name = "nombre_rol", length = 50, nullable = false, unique = true)
    @Schema(description = "Nombre del rol", example = "ADMIN")
    private String nombreRol;

    @Column(columnDefinition = "TEXT")
    @Schema(description = "Descripción del rol", example = "Este rol tiene acceso a todas las funciones administrativas del sistema.")
    private String descripcion;

    @ManyToMany(mappedBy = "roles")
    @Schema(description = "Usuarios que tienen asignado este rol")
    private Set<Usuario> usuarios =new HashSet<>();
}