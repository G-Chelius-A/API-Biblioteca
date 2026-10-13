package com.api.libreria.utils;

import java.time.LocalDate;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Implementación del validador para la anotación {@link AnioNoFuturo}.
 *
 * <p>Verifica que el valor entero proporcionado no supere el año actual
 * obtenido de {@link LocalDate#now()}. Los valores {@code null} se
 * consideran válidos y son ignorados por este validador.</p>
 *
 * @author G-Chelius-A
 * @see AnioNoFuturo
 */
public class AnioNoFuturoValidator implements ConstraintValidator<AnioNoFuturo, Integer> {

    /**
     * Valida que el año proporcionado no sea posterior al año actual.
     *
     * @param value   el valor del año a validar; puede ser {@code null}.
     * @param context contexto del validador proporcionado por Bean Validation.
     * @return {@code true} si el valor es {@code null} o no supera el año actual;
     *         {@code false} si el año es posterior al año actual.
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // null es válido; usar @NotNull para obligatoriedad
        }
        int anioActual = LocalDate.now().getYear();
        return value <= anioActual;
    }
}
