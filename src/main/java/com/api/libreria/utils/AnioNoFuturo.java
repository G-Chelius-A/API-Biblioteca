package com.api.libreria.utils;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

/**
 * Restricción de Bean Validation que valida que un año entero no sea
 * posterior al año actual.
 *
 * <p>Se puede aplicar sobre campos de tipo {@link Integer} o {@code int}.
 * Los valores {@code null} se consideran válidos (usar {@code @NotNull}
 * de forma complementaria si el campo es obligatorio).</p>
 *
 * <p>Ejemplo de uso:</p>
 * <pre>
 *     &#64;AnioNoFuturo(message = "El año de publicación no puede ser en el futuro")
 *     private Integer anioPublicacion;
 * </pre>
 *
 * @author G-Chelius-A
 * @see AnioNoFuturoValidator
 */
@Documented
@Constraint(validatedBy = AnioNoFuturoValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface AnioNoFuturo {

    /** Mensaje de error a mostrar cuando la validación falla. */
    String message() default "El año no puede ser en el futuro";

    /** Grupos de validación de Bean Validation. */
    Class<?>[] groups() default {};

    /** Payload de Bean Validation para metadatos adicionales. */
    Class<? extends Payload>[] payload() default {};
}
