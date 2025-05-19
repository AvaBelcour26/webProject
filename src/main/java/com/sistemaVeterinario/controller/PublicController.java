package com.sistemaVeterinario.controller;

import com.sistemaVeterinario.dto.UsuarioDTO;
import com.sistemaVeterinario.models.Servicio;
import com.sistemaVeterinario.service.AuthService;
import com.sistemaVeterinario.service.ServicioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;


import java.util.List;

/**
 * Controlador para gestionar las páginas públicas del sistema,
 * como la página principal, "Sobre nosotros", login y registro.
 */
@Controller
@RequestMapping("/public")
public class PublicController {

    @Autowired
    private ServicioService servicioService;

    private final AuthService authService;
    private final MessageSource messageSource;

    public PublicController(AuthService authService, MessageSource messageSource) {
        this.authService = authService;
        this.messageSource = messageSource;
    }

    /**
     * Muestra la página de inicio con los servicios activos disponibles.
     *
     * @param model modelo para enviar datos a la vista
     * @return vista "public/home"
     */
    @Operation(summary = "Página principal pública", description = "Muestra la página de inicio con los servicios activos.")
    @ApiResponse(responseCode = "200", description = "Vista home renderizada correctamente")
    @GetMapping("/")
    public String home(Model model) {
        List<Servicio> servicios = servicioService.findAllActive();
        model.addAttribute("servicios", servicios);
        return "public/home";
    }

    /**
     * Muestra la página de información "Sobre nosotros".
     *
     * @return vista "public/aboutUs"
     */
    @Operation(summary = "Página sobre nosotros", description = "Muestra información acerca del sitio o empresa.")
    @ApiResponse(responseCode = "200", description = "Vista sobre nosotros renderizada correctamente")
    @GetMapping("/about-us")
    public String aboutUs() {
        return "public/aboutUs";
    }

    /**
     * Muestra el formulario de login.
     *
     * @return vista "public/loginForm"
     */
    @Operation(summary = "Mostrar formulario de login", description = "Muestra la vista del formulario de inicio de sesión.")
    @ApiResponse(responseCode = "200", description = "Vista de login renderizada correctamente")
    @GetMapping("/login")
    public String mostrarLogin() {
        return "public/loginForm";
    }

    /**
     * Muestra el formulario de registro de nuevos usuarios.
     *
     * @param model modelo para enviar datos a la vista
     * @return vista "public/registrationForm"
     */
    @Operation(summary = "Mostrar formulario de registro", description = "Muestra la vista del formulario de registro de usuarios.")
    @ApiResponse(responseCode = "200", description = "Vista de registro renderizada correctamente")
    @GetMapping("/register")
    public String mostrarRegistro(Model model) {
        model.addAttribute("usuario", new UsuarioDTO());
        return "/public/registrationForm";
    }

    /**
     * Procesa el formulario de registro de usuarios. Valida los datos,
     * intenta registrar un nuevo usuario y maneja errores si el correo o teléfono ya existen.
     *
     * @param usuarioDTO objeto con los datos del usuario
     * @param bindingResult resultado de las validaciones
     * @param model modelo para enviar mensajes de error a la vista
     * @return redirección a login si es exitoso, o vuelve al formulario de registro con errores
     */
    @Operation(summary = "Procesar formulario de registro", description = "Valida y registra un nuevo usuario.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "302", description = "Redirección a login si el registro es exitoso"),
            @ApiResponse(responseCode = "200", description = "Error de validación o usuario existente, retorna al formulario de registro",
                    content = @Content(mediaType = "text/html"))
    })
    @PostMapping("/register")
    public String procesarRegistro(
            @Valid @ModelAttribute("usuario") UsuarioDTO usuarioDTO,
            BindingResult bindingResult,
            Model model
    ) {
        // Si hay errores de validación redirige al formulario de registro
        if (bindingResult.hasErrors()) {
            return "public/registrationForm";
        }

        try {
            authService.registrarUsuario(usuarioDTO);
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            // Manejo de errores específicos
            String mensaje = e.getMessage();

            if (mensaje.contains("correo")) {
                model.addAttribute("emailError",
                        messageSource.getMessage("error.email.existing", null, LocaleContextHolder.getLocale()));
            } else if (mensaje.contains("teléfono")) {
                model.addAttribute("telefonoError",
                        messageSource.getMessage("error.phone.existing", null, LocaleContextHolder.getLocale()));
            }
            return "public/registrationForm";
        }
    }
}