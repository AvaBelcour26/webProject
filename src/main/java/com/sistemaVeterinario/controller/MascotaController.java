package com.sistemaVeterinario.controller;

import com.sistemaVeterinario.models.Mascota;
import com.sistemaVeterinario.models.Usuario;
import com.sistemaVeterinario.service.MascotaService;
import com.sistemaVeterinario.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Controlador encargado de gestionar las mascotas del usuario autenticado.
 * Permite listar, registrar, editar, ver detalles y eliminar mascotas.
 */
@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    @Autowired
    private MascotaService mascotaService;

    @Autowired
    private UsuarioService usuarioService;

    /**
     * Lista las mascotas del usuario autenticado.
     *
     * @param model Modelo para enviar datos a la vista
     * @return Vista con la lista de mascotas
     */
    @GetMapping
    @Operation(summary = "Listar mascotas", description = "Muestra todas las mascotas asociadas al usuario autenticado.")
    public String listarMascotas(Model model) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Usuario> usuarioActual = usuarioService.findByEmail(email);

        List<Mascota> mascotas = mascotaService.findByPropietario(usuarioActual);
        model.addAttribute("mascotas", mascotas);
        return "user/listamascota";
    }

    /**
     * Muestra el formulario para registrar una nueva mascota.
     *
     * @param model Modelo para enviar datos a la vista
     * @return Vista del formulario de mascota
     */
    @GetMapping("/nueva")
    @Operation(summary = "Formulario nueva mascota", description = "Muestra el formulario para registrar una nueva mascota")
    public String mostrarFormularioNuevaMascota(Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("titulo", "Agregar Nueva Mascota");
        return "user/formmascota";
    }

    /**
     * Guarda una nueva mascota en la base de datos.
     *
     * @param mascota Mascota capturada desde el formulario
     * @param fechaNacimiento Fecha de nacimiento parseada desde el input
     * @param result Resultado de validación
     * @param redirectAttributes Atributos flash para mostrar mensajes
     * @return Redirección a la lista de mascotas
     */
    @PostMapping("/guardar")
    @Operation(summary = "Guardar mascota", description = "Guarda una nueva mascota asociada al usuario autenticado")
    public String guardarMascota(@ModelAttribute Mascota mascota,
                                 @RequestParam("fechaNacimientoStr") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaNacimiento,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "user/formmascota";
        }

        // Obtiene el usuario autenticado y lo asigna como propietario de una mascota
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Usuario> usuarioActual = usuarioService.findByEmail(email);

        if (!usuarioActual.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la mascota: usuario no autenticado");
            return "redirect:/mascotas";
        }

        mascota.setPropietario(usuarioActual.get());
        mascota.setFechaNacimiento(fechaNacimiento);

        mascotaService.save(mascota);
        redirectAttributes.addFlashAttribute("success", "Mascota guardada con éxito");
        return "redirect:/mascotas";
    }

    /**
     * Muestra el formulario para editar una mascota existente.
     *
     * @param id ID de la mascota
     * @param model Modelo para la vista
     * @param redirectAttributes Atributos flash para mensajes
     * @return Vista del formulario si tiene permiso, redirección si no
     */
    @GetMapping("/editar/{id}")
    @Operation(summary = "Editar mascota", description = "Muestra el formulario para editar una mascota si pertenece al usuario autenticado")
    public String mostrarFormularioEditarMascota(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        // Obtener el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Usuario> usuarioActual = usuarioService.findByEmail(email);

        Mascota mascota = mascotaService.findById(id);

        // Verificar que la mascota exista y pertenezca al usuario
        if (mascota == null) {
            redirectAttributes.addFlashAttribute("error", "La mascota no existe");
            return "redirect:/mascotas";
        }

        if (!usuarioActual.isPresent() || !mascota.getPropietario().getIdUsuario().equals(usuarioActual.get().getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para editar esta mascota");
            return "redirect:/mascotas";
        }

        model.addAttribute("mascota", mascota);
        model.addAttribute("titulo", "Editar Mascota");
        model.addAttribute("fechaNacimientoStr", mascota.getFechaNacimiento());
        return "user/formmascota";
    }

    /**
     * Elimina una mascota si pertenece al usuario autenticado.
     *
     * @param id ID de la mascota a eliminar
     * @param redirectAttributes Atributos flash para mensajes
     * @return Redirección a la lista de mascotas
     */
    @GetMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar mascota", description = "Elimina una mascota si pertenece al usuario autenticado")
    public String eliminarMascota(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        // Obtiene el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Usuario> usuarioActual = usuarioService.findByEmail(email);

        Mascota mascota = mascotaService.findById(id);

        // Verifica que la mascota exista y pertenezca al usuario
        if (mascota == null) {
            redirectAttributes.addFlashAttribute("error", "La mascota no existe");
            return "redirect:/mascotas";
        }

        if (!usuarioActual.isPresent() || !mascota.getPropietario().getIdUsuario().equals(usuarioActual.get().getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para eliminar esta mascota");
            return "redirect:/mascotas";
        }

        mascotaService.delete(id);
        redirectAttributes.addFlashAttribute("success", "Mascota eliminada con éxito");
        return "redirect:/mascotas";
    }

    /**
     * Muestra los detalles de una mascota si pertenece al usuario.
     *
     * @param id ID de la mascota
     * @param model Modelo para vista
     * @param redirectAttributes Atributos flash
     * @return Vista de detalles o redirección si no tiene acceso
     */
    @GetMapping("/detalles/{id}")
    @Operation(summary = "Ver detalles de mascota", description = "Muestra los detalles de una mascota si pertenece al usuario autenticado")
    public String verDetallesMascota(@PathVariable Integer id, Model model, RedirectAttributes redirectAttributes) {
        // Obtiene el usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        Optional<Usuario> usuarioActual = usuarioService.findByEmail(email);

        Mascota mascota = mascotaService.findById(id);

        // Verifica que la mascota exista y pertenezca al usuario
        if (mascota == null) {
            redirectAttributes.addFlashAttribute("error", "La mascota no existe");
            return "redirect:/mascotas";
        }

        if (!usuarioActual.isPresent() || !mascota.getPropietario().getIdUsuario().equals(usuarioActual.get().getIdUsuario())) {
            redirectAttributes.addFlashAttribute("error", "No tienes permiso para ver esta mascota");
            return "redirect:/mascotas";
        }

        model.addAttribute("mascota", mascota);
        return "user/detallesmascota";
    }
}