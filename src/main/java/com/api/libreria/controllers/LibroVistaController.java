package com.api.libreria.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.api.libreria.models.LibroModel;
import com.api.libreria.services.LibroService;

import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import com.api.libreria.utils.VistaExceptionHelper;

/**
 * Controlador MVC de vista para la gestión de libros desde la interfaz web.
 * Maneja las peticiones Thymeleaf bajo las rutas {@code /}, {@code /vista},
 * {@code /vista/libros} y sus sub-rutas de operación.
 * Delega la lógica de negocio en {@link LibroService} y utiliza
 * {@link VistaExceptionHelper} para extraer mensajes de error legibles.
 *
 * @author G-Chelius-A
 */
@Controller
public class LibroVistaController {

    /**
     * Servicio de libros utilizado para realizar las operaciones CRUD desde la
     * vista.
     */
    private final LibroService libroService;

    /**
     * Constructor para inyección de dependencias del servicio de libros.
     *
     * @param libroService el servicio de libros inyectado por Spring.
     */
    public LibroVistaController(LibroService libroService) {
        this.libroService = libroService;
    }

    /**
     * {@code GET /}, {@code GET /vista}, {@code GET /vista/libros} — Muestra la
     * vista principal de libros con la lista completa registrada y un formulario
     * vacío para el alta de un nuevo libro.
     *
     * @param model el modelo de Thymeleaf al que se añaden los atributos de vista.
     * @return el nombre de la plantilla Thymeleaf {@code libros}.
     */
    @GetMapping({ "/", "/vista", "/vista/libros" })
    public String mostrarLibros(Model model) {
        model.addAttribute("libros", libroService.obtenerLibros());
        if (!model.containsAttribute("libro")) {
            model.addAttribute("libro", new LibroModel());
        }
        return "libros";
    }

    /**
     * {@code POST /vista/libros/guardar} — Procesa el formulario de registro de
     * un nuevo libro. Si hay errores de validación, redirige con un mensaje de
     * error. En caso de éxito, redirige con un mensaje de confirmación.
     *
     * @param libro         el {@link LibroModel} enlazado desde el formulario,
     *                      validado con {@link Valid}.
     * @param bindingResult resultado de la validación del formulario.
     * @param atributos     atributos flash para pasar mensajes a la vista tras la
     *                      redirección.
     * @return redirección a {@code /vista/libros}.
     */
    @PostMapping("/vista/libros/guardar")
    public String guardarLibro(@Valid @ModelAttribute("libro") LibroModel libro,
            BindingResult bindingResult, RedirectAttributes atributos) {
        if (bindingResult.hasErrors()) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(bindingResult));
            return "redirect:/vista/libros";
        }
        try {
            libroService.guardarLibro(libro);
            atributos.addFlashAttribute("mensaje", "Libro registrado correctamente.");
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/libros";
    }

    /**
     * {@code POST /vista/libros/{isbn}/actualizar} — Procesa el formulario de
     * edición de un libro existente. Redirige con mensaje de confirmación o de
     * error según el resultado.
     *
     * @param isbn    el ISBN del libro a actualizar, extraído de la URL.
     * @param libro   el {@link LibroModel} con los nuevos datos, enlazado del
     *                formulario.
     * @param atributos atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/libros}.
     */
    @PostMapping("/vista/libros/{isbn}/actualizar")
    public String actualizarLibro(@PathVariable Long isbn, @ModelAttribute LibroModel libro,
            RedirectAttributes atributos) {
        try {
            libroService.actualizarLibro(libro, isbn);
            atributos.addFlashAttribute("mensaje", "Libro actualizado correctamente.");
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/libros";
    }

    /**
     * {@code POST /vista/libros/{isbn}/eliminar} — Procesa la solicitud de
     * eliminación de un libro. Redirige con mensaje de confirmación o de error
     * según el resultado.
     *
     * @param isbn    el ISBN del libro a eliminar, extraído de la URL.
     * @param atributos atributos flash para pasar mensajes a la vista.
     * @return redirección a {@code /vista/libros}.
     */
    @PostMapping("/vista/libros/{isbn}/eliminar")
    public String eliminarLibro(@PathVariable Long isbn, RedirectAttributes atributos) {
        try {
            if (libroService.eliminarLibro(isbn)) {
                atributos.addFlashAttribute("mensaje", "Libro eliminado correctamente.");
            } else {
                atributos.addFlashAttribute("error", "No se pudo eliminar el libro.");
            }
        } catch (Exception exception) {
            atributos.addFlashAttribute("error", VistaExceptionHelper.extractErrorMessage(exception));
        }
        return "redirect:/vista/libros";
    }
}