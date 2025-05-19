package com.sistemaVeterinario.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="usuarios")
@Schema(description = "Entidad que representa a un usuario del sistema")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    @Schema(description = "ID único del usuario", example = "1")
    private Integer idUsuario;

    @Column(length = 50, nullable = false)
    @Schema(description = "Nombre del usuario", example = "Tatiana")
    private String nombre;

    @Column(length = 50, nullable = false)
    @Schema(description = "Apellido del usuario", example = "Vega")
    private String apellido;

    @Column(length = 255, nullable = false, unique = true)
    @Schema(description = "Correo electrónico del usuario", example = "tatiana@example.com")
    private String email;

    @Column(length = 10, nullable = false, unique = true)
    @Schema(description = "Número de teléfono del usuario", example = "3001234567")
    private String telefono;

    @Column(length = 255, nullable = false)
    @Schema(description = "Contraseña del usuario")
    private String contrasena;

    @Column(name = "fecha_registro")
    @Schema(description = "Fecha y hora de registro del usuario")
    private LocalDateTime fechaRegistro;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol")
    )
    @Schema(description = "Roles asignados al usuario")
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "propietario", cascade = CascadeType.ALL)
    @Schema(description = "Mascotas registradas a nombre del usuario")
    private Set<Mascota> mascotas = new HashSet<>();

    @PrePersist
    public void prePersist(){
        fechaRegistro = LocalDateTime.now();
    }
}
