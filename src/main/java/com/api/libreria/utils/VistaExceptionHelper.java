package com.api.libreria.utils;

import java.util.stream.Collectors;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.validation.BindingResult;
import org.springframework.web.server.ResponseStatusException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

/**
 * Clase utilitaria que centraliza la extracción de mensajes de error legibles
 * para el usuario a partir de distintos tipos de excepción o resultado de
 * validación.
 * Proporciona dos sobrecargas del método {@code extractErrorMessage}: una para
 * errores de enlace de formulario ({@link BindingResult}) y otra para
 * excepciones en tiempo de ejecución ({@link Throwable}).
 *
 * @author G-Chelius-A
 */
public class VistaExceptionHelper {

    /**
     * Extrae y concatena los mensajes de error de un {@link BindingResult},
     * separándolos con {@code ". "}.
     * Si el resultado no contiene errores o todos los mensajes están en blanco,
     * retorna un mensaje genérico de validación.
     *
     * @param bindingResult el resultado de la validación del formulario; puede
     *                      ser {@code null}.
     * @return cadena con los mensajes de error de validación, o un texto genérico
     *         si no se encontraron mensajes específicos.
     */
    public static String extractErrorMessage(BindingResult bindingResult) {
        if (bindingResult != null && bindingResult.hasErrors()) {
            String errors = bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .filter(msg -> msg != null && !msg.isBlank())
                    .collect(Collectors.joining(". "));
            if (!errors.isBlank()) {
                return errors;
            }
        }
        return "Error de validación en los datos ingresados.";
    }

    /**
     * Extrae un mensaje de error legible a partir de una excepción en tiempo de
     * ejecución. Recorre la cadena de causas para identificar el tipo de excepción
     * raíz y selecciona el mensaje más apropiado según la siguiente prioridad:
     * <ol>
     *   <li>{@link ConstraintViolationException} — mensajes de violación de
     *       restricciones Bean Validation.</li>
     *   <li>{@link ResponseStatusException} — razón asociada al estado HTTP.</li>
     *   <li>{@link DataIntegrityViolationException} — interpreta mensajes de
     *       violación de integridad de la BD (duplicados, claves foráneas, etc.)
     *       y retorna un texto amigable.</li>
     *   <li>Mensaje de la excepción original o de la causa raíz.</li>
     *   <li>Mensaje genérico si ninguno de los anteriores aplica.</li>
     * </ol>
     *
     * @param ex la excepción de la que se desea extraer el mensaje; puede ser
     *           {@code null}.
     * @return cadena con el mensaje de error legible para el usuario.
     */
    public static String extractErrorMessage(Throwable ex) {
        if (ex == null) {
            return "Ocurrió un error inesperado.";
        }

        Throwable root = ex;
        while (root.getCause() != null && root.getCause() != root) {
            if (root instanceof ConstraintViolationException) {
                break;
            }
            root = root.getCause();
        }

        if (root instanceof ConstraintViolationException cve) {
            String msg = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .filter(m -> m != null && !m.isBlank())
                    .collect(Collectors.joining(". "));
            if (!msg.isBlank()) {
                return msg;
            }
        }

        if (ex instanceof ConstraintViolationException cve) {
            String msg = cve.getConstraintViolations().stream()
                    .map(ConstraintViolation::getMessage)
                    .filter(m -> m != null && !m.isBlank())
                    .collect(Collectors.joining(". "));
            if (!msg.isBlank()) {
                return msg;
            }
        }

        if (ex instanceof ResponseStatusException rse) {
            if (rse.getReason() != null && !rse.getReason().isBlank()) {
                return rse.getReason();
            }
        }

        if (ex instanceof DataIntegrityViolationException || root instanceof DataIntegrityViolationException) {
            String rootMsg = root.getMessage() != null ? root.getMessage().toLowerCase() : "";
            if (rootMsg.contains("duplicate") || rootMsg.contains("unique")) {
                if (rootMsg.contains("correo")) {
                    return "El correo electrónico ya se encuentra registrado.";
                }
                if (rootMsg.contains("telefono")) {
                    return "El número de teléfono ya se encuentra registrado.";
                }
                return "Ya existe un registro con esos datos (valor duplicado).";
            }
            if (rootMsg.contains("foreign key") || rootMsg.contains("constraint")) {
                return "No se puede completar la operación debido a relaciones existentes con otros registros.";
            }
            return "Error de integridad de datos: verifica que los datos no estén duplicados y existan las relaciones necesarias.";
        }

        if (ex.getMessage() != null && !ex.getMessage().isBlank()) {
            return ex.getMessage();
        }
        if (root.getMessage() != null && !root.getMessage().isBlank()) {
            return root.getMessage();
        }
        return "Ocurrió un error al procesar la solicitud.";
    }
}
