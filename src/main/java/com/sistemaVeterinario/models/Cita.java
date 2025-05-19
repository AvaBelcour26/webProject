package com.sistemaVeterinario.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "citas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Entidad que representa una cita veterinaria para una mascota")
public class Cita {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    @Schema(description = "ID único de la cita", example = "1")
    private Integer idCita;

    @ManyToOne
    @JoinColumn(name = "id_mascota", nullable = false)
    @Schema(description = "Mascota a la que pertenece la cita")
    private Mascota mascota;

    @ManyToOne
    @JoinColumn(name = "id_servicio", nullable = false)
    @Schema(description = "Servicio veterinario asociado a la cita")
    private Servicio servicio;

    @Column(name = "fecha_hora", nullable = false)
    @Schema(description = "Fecha y hora programada para la cita")
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    @Schema(description = "Estado actual de la cita", example = "Programada")
    private EstadoCita estado = EstadoCita.Programada;

    @Schema(description = "Estados posibles de una cita")
    public enum EstadoCita {
        Programada, Completada, Cancelada
    }
}