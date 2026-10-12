package com.api.libreria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.libreria.models.LibroModel;
import com.api.libreria.models.PrestamoModel;
import com.api.libreria.models.UsuarioModel;
import com.api.libreria.services.PrestamoService;

import org.springframework.validation.BindingResult;
import com.api.libreria.utils.VistaExceptionHelper;

/**
 * Controlador MVC de vista para la gestión de préstamos desde la interfaz web.
 * Maneja las peticiones Thymeleaf bajo la ruta {@code /vista/prestamos} y sus
 * sub-rutas de operación (guardar, actualizar, devolver, eliminar).
 * Delega la lógica de negocio en {@link PrestamoService} y utiliza
 * {@link VistaExceptionHelper} para extraer mensajes de error legibles.
 *
 * @author G-Chelius-A
 */
@Controller
public class PrestamoVistaController {

    /**
     * Servicio de préstamos utilizado para realizar las operaciones CRUD desde la
     * vista.
     */
    private final PrestamoService prestamoService;

    /**
     * Constructor para inyección de dependencias del servicio de préstamos.
     *
     * @param prestamoService el servicio de préstamos inyectado por Spring.
     */
    public PrestamoVistaController(PrestamoService prestamoService) {
        this.prestamoService = prestamoService;
    }

    /**
     * {@code GET /vista/prestamos} — Muestra la vista principal de préstamos con
     * la lista completa registrada y un formulario vacío para el registro de un
     * nuevo préstamo. Inicializa las referencias de libro y socio para evitar
     * errores de puntero nulo en el formulario.
     *
     * @param model el modelo de Thymeleaf al que se añaden los atributos de vista.
     * @return el nombre de la plantilla Thymeleaf {@code prestamos}.
     */
    @GetMapping("/vista/prestamos")
    public String mostrarPrestamos(Model model) {
        model.addAttribute("prestamos", prestamoService.obtenerTodosLosPrestamos());
        if (!model.containsAttribute("prestamo")) {
            PrestamoModel prestamo = new PrestamoModel();
            prestamo.setLibro(new LibroModel());
            prestamo.setSocio(new UsuarioModel());
            model.addAttribute("prestamo", prestamo);
        }
        return "prestamos";
    }

    /**
     * {@code POST /vista/prestamos/guardar} — Procesa el formulario de registro
     * de un nuevo préstamo. Si hay errores de validación, redirige con un mensaje
     * de error. En caso de éxito, redirige con un mensaje de confirmación.
     *
     * @param prestamo      el {@link PrestamoModel} enlazado desde el formulario.
     * @param bindingResult resultado de la validación del formulario.
     * @param atributos     atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/prestamos}.
     */
    @PostMapping("/vista/prestamos/guardar")
    public String guardarPrestamo(@ModelAttribute PrestamoModel prestamo, BindingResult bindingResult,
            RedirectAttributes atributos) {
        if (bindingResult.hasErrors()) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(bindingResult));
            return "redirect:/vista/prestamos";
        }
        try {
            prestamoService.guardarPrestamo(prestamo);
            atributos.addFlashAttribute("mensaje", "Préstamo registrado correctamente.");
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/prestamos";
    }

    /**
     * {@code POST /vista/prestamos/{id}/actualizar} — Procesa el formulario de
     * edición de un préstamo existente. Redirige con mensaje de confirmación o de
     * error según el resultado.
     *
     * @param prestamo  el {@link PrestamoModel} con los nuevos datos, enlazado del
     *                  formulario.
     * @param id        el identificador único del préstamo a actualizar, extraído
     *                  de la URL.
     * @param atributos atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/prestamos}.
     */
    @PostMapping("/vista/prestamos/{id}/actualizar")
    public String actualizarPrestamo(@ModelAttribute PrestamoModel prestamo, @PathVariable Long id,
            RedirectAttributes atributos) {
        try {
            prestamoService.actualizarPrestamo(prestamo, id);
            atributos.addFlashAttribute("mensaje", "Préstamo actualizado correctamente.");
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/prestamos";
    }

    /**
     * {@code POST /vista/prestamos/{id}/devolver} — Procesa la devolución de un
     * préstamo activo. Redirige con mensaje de confirmación o de error según el
     * resultado.
     *
     * @param id        el identificador único del préstamo a devolver, extraído de
     *                  la URL.
     * @param atributos atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/prestamos}.
     */
    @PostMapping("/vista/prestamos/{id}/devolver")
    public String devolverPrestamo(@PathVariable Long id, RedirectAttributes atributos) {
        try {
            prestamoService.devolverPrestamo(id);
            atributos.addFlashAttribute("mensaje", "Préstamo devuelto correctamente.");
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/prestamos";
    }

    /**
     * {@code POST /vista/prestamos/{id}/eliminar} — Procesa la solicitud de
     * eliminación de un préstamo. Solo se permite eliminar préstamos ya devueltos.
     * Redirige con mensaje de confirmación o de error según el resultado.
     *
     * @param id        el identificador único del préstamo a eliminar, extraído de
     *                  la URL.
     * @param atributos atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/prestamos}.
     */
    @PostMapping("/vista/prestamos/{id}/eliminar")
    public String eliminarPrestamo(@PathVariable Long id, RedirectAttributes atributos) {
        try {
            if (prestamoService.eliminarPrestamo(id)) {
                atributos.addFlashAttribute("mensaje", "Préstamo eliminado correctamente.");
            } else {
                atributos.addFlashAttribute("error", "No se pudo eliminar el préstamo.");
            }
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/prestamos";
    }
}
