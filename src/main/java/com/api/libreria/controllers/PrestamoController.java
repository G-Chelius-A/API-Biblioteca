package com.api.libreria.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.api.libreria.models.PrestamoModel;
import com.api.libreria.services.PrestamoService;

/**
 * Controlador REST que expone los endpoints de la API para la gestión de
 * préstamos bajo la ruta base {@code /prestamos}.
 * Delega toda la lógica de negocio en {@link PrestamoService}.
 *
 * @author G-Chelius-A
 */
@RestController
@RequestMapping("/prestamos")
public class PrestamoController {

    /**
     * Servicio de préstamos inyectado por Spring.
     */
    @Autowired
    private PrestamoService prestamoService;

    /**
     * {@code GET /prestamos} — Retorna la lista completa de préstamos registrados.
     *
     * @return lista de todos los {@link PrestamoModel} existentes.
     */
    @GetMapping
    public ArrayList<PrestamoModel> obtenerPrestamos() {
        return prestamoService.obtenerTodosLosPrestamos();
    }

    /**
     * {@code POST /prestamos} — Registra un nuevo préstamo.
     * Responde con {@code 201 Created} si el registro es exitoso.
     *
     * @param prestamo el {@link PrestamoModel} a guardar, deserializado del cuerpo JSON.
     * @return el {@link PrestamoModel} guardado con sus datos completos.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public PrestamoModel guardarPrestamo(@RequestBody PrestamoModel prestamo) {
        return prestamoService.guardarPrestamo(prestamo);
    }

    /**
     * {@code GET /prestamos/socio/{socioId}} — Retorna todos los préstamos de
     * un socio dado su ID.
     *
     * @param socioId el identificador único del socio, extraído de la URL.
     * @return lista de {@link PrestamoModel} asociados al socio indicado.
     */
    @GetMapping(path = "/socio/{socioId}")
    public ArrayList<PrestamoModel> obtenerPrestamosPorSocio(@PathVariable Long socioId) {
        return prestamoService.obtenerPrestamosPorSocio(socioId);
    }

    /**
     * {@code GET /prestamos/libro/{libroIsbn}} — Retorna todos los préstamos de
     * un libro dado su ISBN.
     *
     * @param libroIsbn el ISBN del libro, extraído de la URL.
     * @return lista de {@link PrestamoModel} vinculados al libro indicado.
     */
    @GetMapping(path = "/libro/{libroIsbn}")
    public ArrayList<PrestamoModel> obtenerPrestamosPorLibro(@PathVariable Long libroIsbn) {
        return prestamoService.obtenerPrestamosPorLibro(libroIsbn);
    }

    /**
     * {@code PUT /prestamos/{id}} — Actualiza el libro y el socio de un
     * préstamo existente.
     *
     * @param request los nuevos datos del préstamo, deserializados del cuerpo JSON.
     * @param id      el identificador único del préstamo, extraído de la URL.
     * @return el {@link PrestamoModel} actualizado y persistido.
     */
    @PutMapping(path = "/{id}")
    public PrestamoModel actualizarPrestamo(@RequestBody PrestamoModel request,
                                            @PathVariable Long id) {
        return prestamoService.actualizarPrestamo(request, id);
    }

    /**
     * {@code PUT /prestamos/{id}/devolver} — Registra la devolución de un
     * préstamo activo, actualizando la fecha de devolución y los ejemplares
     * disponibles del libro.
     *
     * @param id el identificador único del préstamo a devolver, extraído de la URL.
     * @return el {@link PrestamoModel} actualizado con la fecha de devolución.
     */
    @PutMapping(path = "/{id}/devolver")
    public PrestamoModel devolverPrestamo(@PathVariable Long id) {
        return prestamoService.devolverPrestamo(id);
    }

    /**
     * {@code DELETE /prestamos/{id}} — Elimina un préstamo por su ID.
     * Solo se permite eliminar préstamos ya devueltos.
     * Responde con {@code 204 No Content} si la eliminación es exitosa.
     *
     * @param id el identificador único del préstamo a eliminar, extraído de la URL.
     * @return mensaje de confirmación o de error como cadena de texto.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{id}")
    public String eliminarPrestamo(@PathVariable Long id) {
        boolean ok = prestamoService.eliminarPrestamo(id);
        if (ok) {
            return "Se eliminó el préstamo con ID: " + id;
        } else {
            return "No se pudo eliminar el préstamo con ID: " + id;
        }
    }

}
