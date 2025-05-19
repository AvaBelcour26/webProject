package com.sistemaVeterinario.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Schema(description = "DTO para registrar o actualizar información del usuario")
public class UsuarioDTO {

    @Pattern(regexp = "^(?!\\s)([a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,45})(?<!\\s)$", message = "{error.name.invalid}")
    @Schema(description = "Nombre del usuario", example = "Tatiana")
    private String nombre;

    @Pattern(regexp = "^(?!\\s)([a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]{3,50})(?<!\\s)$", message = "{error.lastname.invalid}")
    @Schema(description = "Apellido del usuario", example = "Vega")
    private String apellido;

    @Pattern(regexp =
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$",
            message="{error.email.invalid}"
    )
    @Schema(description = "Correo electrónico del usuario", example = "tatiana@email.com")
    private String email;

    @Pattern(regexp = "^3\\d{9}$", message = "{error.phone.invalid}")
    @Schema(description = "Número de teléfono del usuario (10 dígitos, debe empezar con 3)", example = "3001234567")
    private String telefono;

    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[a-zA-Z\\d@_-]{8,}$", message = "{error.password.invalid}")
    @Schema(description = "Contraseña del usuario (mínimo 8 caracteres, incluyendo mayúsculas, minúsculas y números)", example = "Segura12345")
    private String contrasena;
}