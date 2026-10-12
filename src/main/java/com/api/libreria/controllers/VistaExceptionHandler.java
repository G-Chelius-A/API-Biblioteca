package com.api.libreria.controllers;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.servlet.http.HttpServletRequest;
import com.api.libreria.utils.VistaExceptionHelper;

/**
 * Manejador global de excepciones para los controladores de vista MVC
 * ({@link LibroVistaController}, {@link UsuarioVistaController} y
 * {@link PrestamoVistaController}).
 * Captura cualquier excepción no controlada dentro de esos controladores,
 * extrae un mensaje legible mediante {@link VistaExceptionHelper} y redirige
 * al usuario a la vista correspondiente según la URI de la petición original.
 *
 * @author G-Chelius-A
 */
@ControllerAdvice(assignableTypes = {
    LibroVistaController.class,
    UsuarioVistaController.class,
    PrestamoVistaController.class
})
public class VistaExceptionHandler {

    /**
     * Intercepta cualquier {@link Exception} lanzada en los controladores de vista
     * cubiertos por este {@code @ControllerAdvice}. Determina la vista de destino
     * según la URI de la petición:
     * <ul>
     *   <li>Si la URI contiene {@code /usuarios} → redirige a
     *       {@code /vista/usuarios}.</li>
     *   <li>Si la URI contiene {@code /prestamos} → redirige a
     *       {@code /vista/prestamos}.</li>
     *   <li>En cualquier otro caso → redirige a {@code /vista/libros}.</li>
     * </ul>
     *
     * @param ex                la excepción capturada.
     * @param request           la petición HTTP original; puede ser {@code null}
     *                          en contextos de prueba.
     * @param redirectAttributes atributos flash para adjuntar el mensaje de error
     *                           a la vista destino tras la redirección.
     * @return cadena de redirección hacia la vista de destino correspondiente.
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        String errorMessage = VistaExceptionHelper.extractErrorMessage(ex);
        redirectAttributes.addFlashAttribute("error", errorMessage);

        String uri = request != null ? request.getRequestURI() : "";
        if (uri != null) {
            if (uri.contains("/usuarios")) {
                return "redirect:/vista/usuarios";
            } else if (uri.contains("/prestamos")) {
                return "redirect:/vista/prestamos";
            }
        }
        return "redirect:/vista/libros";
    }
}
