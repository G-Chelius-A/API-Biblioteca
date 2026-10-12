package com.api.libreria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.libreria.models.UsuarioModel;
import com.api.libreria.services.UsuarioService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.api.libreria.utils.VistaExceptionHelper;

/**
 * Controlador MVC de vista para la gestión de usuarios desde la interfaz web.
 * Maneja las peticiones Thymeleaf bajo la ruta {@code /vista/usuarios} y sus
 * sub-rutas de operación (guardar, actualizar, eliminar).
 * La actualización desde la vista aplica en una sola operación todos los
 * cambios posibles: datos personales, contraseña, estado y rol.
 * Delega la lógica de negocio en {@link UsuarioService} y utiliza
 * {@link VistaExceptionHelper} para extraer mensajes de error legibles.
 *
 * @author G-Chelius-A
 */
@Controller
public class UsuarioVistaController {

    /**
     * Servicio de usuarios utilizado para realizar las operaciones CRUD desde la
     * vista.
     */
    private final UsuarioService usuarioService;

    /**
     * Constructor para inyección de dependencias del servicio de usuarios.
     *
     * @param usuarioService el servicio de usuarios inyectado por Spring.
     */
    public UsuarioVistaController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /**
     * {@code GET /vista/usuarios} — Muestra la vista principal de usuarios con la
     * lista completa registrada y un formulario vacío para el alta de un nuevo
     * usuario.
     *
     * @param model el modelo de Thymeleaf al que se añaden los atributos de vista.
     * @return el nombre de la plantilla Thymeleaf {@code usuarios}.
     */
    @GetMapping("/vista/usuarios")
    public String mostrarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.obtenerUsuarios());
        if (!model.containsAttribute("usuario")) {
            model.addAttribute("usuario", new UsuarioModel());
        }
        return "usuarios";
    }

    /**
     * {@code POST /vista/usuarios/guardar} — Procesa el formulario de registro
     * de un nuevo usuario. Si hay errores de validación, redirige con un mensaje
     * de error. En caso de éxito, redirige con un mensaje de confirmación.
     *
     * @param usuario       el {@link UsuarioModel} enlazado desde el formulario,
     *                      validado con {@link Valid}.
     * @param bindingResult resultado de la validación del formulario.
     * @param atributos     atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/usuarios}.
     */
    @PostMapping("/vista/usuarios/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuario") UsuarioModel usuario,
            BindingResult bindingResult, RedirectAttributes atributos) {
        if (bindingResult.hasErrors()) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(bindingResult));
            return "redirect:/vista/usuarios";
        }
        try {
            usuarioService.guardarUsuario(usuario);
            atributos.addFlashAttribute("mensaje", "Usuario registrado correctamente.");
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/usuarios";
    }

    /**
     * {@code POST /vista/usuarios/{id}/actualizar} — Procesa el formulario de
     * edición de un usuario existente. Aplica en secuencia la actualización de
     * datos personales, contraseña, estado y rol. Redirige con mensaje de
     * confirmación o de error según el resultado.
     *
     * @param id        el identificador único del usuario a actualizar, extraído
     *                  de la URL.
     * @param usuario   el {@link UsuarioModel} con los nuevos datos, enlazado del
     *                  formulario.
     * @param atributos atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/usuarios}.
     */
    @PostMapping("/vista/usuarios/{id}/actualizar")
    public String actualizarUsuario(@PathVariable Long id, @ModelAttribute UsuarioModel usuario,
            RedirectAttributes atributos) {
        try {
            usuarioService.actualizarUsuario(usuario, id);
            usuarioService.actualizarContrasena(usuario, id);
            usuarioService.actualizarEstado(usuario, id);
            usuarioService.actualizarRol(usuario, id);
            atributos.addFlashAttribute("mensaje", "Usuario actualizado correctamente.");
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/usuarios";
    }

    /**
     * {@code POST /vista/usuarios/{id}/eliminar} — Procesa la solicitud de
     * eliminación de un usuario. No se permite eliminar usuarios con préstamos
     * activos. Redirige con mensaje de confirmación o de error según el resultado.
     *
     * @param id        el identificador único del usuario a eliminar, extraído de
     *                  la URL.
     * @param atributos atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/usuarios}.
     */
    @PostMapping("/vista/usuarios/{id}/eliminar")
    public String eliminarUsuario(@PathVariable Long id, RedirectAttributes atributos) {
        try {
            if (usuarioService.eliminarUsuario(id)) {
                atributos.addFlashAttribute("mensaje", "Usuario eliminado correctamente.");
            } else {
                atributos.addFlashAttribute("error", "No se pudo eliminar el usuario.");
            }
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/usuarios";
    }
}