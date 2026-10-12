package com.api.libreria.services;

import java.util.ArrayList;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api.libreria.models.PrestamoModel;
import com.api.libreria.models.LibroModel;
import com.api.libreria.models.UsuarioModel;
import com.api.libreria.repositories.ILibroRepository;
import com.api.libreria.repositories.IPrestamoRepository;
import com.api.libreria.repositories.IUsuarioRepository;

/**
 * Servicio que centraliza la lógica de negocio para la entidad
 * {@link PrestamoModel}.
 * Gestiona el ciclo de vida de los préstamos: registro, consulta, devolución
 * y eliminación, incluyendo la actualización del conteo de ejemplares disponibles
 * en {@link LibroModel}.
 *
 * @author G-Chelius-A
 */
@Service
public class PrestamoService {

    /**
     * Repositorio de préstamos inyectado por Spring para acceder a la capa de
     * persistencia de {@link PrestamoModel}.
     */
    @Autowired
    IPrestamoRepository prestamoRepository;

    /**
     * Repositorio de libros inyectado por Spring, utilizado para validar la
     * existencia del libro y gestionar los ejemplares disponibles.
     */
    @Autowired
    ILibroRepository libroRepository;

    /**
     * Repositorio de usuarios inyectado por Spring, utilizado para validar la
     * existencia del socio al registrar un préstamo.
     */
    @Autowired
    IUsuarioRepository usuarioRepository;

    /**
     * Retorna todos los préstamos realizados por un socio dado su ID.
     *
     * @param socioId el identificador único del socio.
     * @return lista de {@link PrestamoModel} asociados al socio indicado.
     */
    public ArrayList<PrestamoModel> obtenerPrestamosPorSocio(Long socioId) {
        return prestamoRepository.findBySocioId(socioId);
    }

    /**
     * Registra un nuevo préstamo en la base de datos.
     * Valida que el libro y el socio existan, que haya ejemplares disponibles
     * y descuenta uno del conteo al confirmar el préstamo.
     * Lanza {@link ResponseStatusException} con:
     * <ul>
     *   <li>{@code 400 BAD_REQUEST} si no se indica libro o socio.</li>
     *   <li>{@code 400 BAD_REQUEST} si no hay ejemplares disponibles.</li>
     *   <li>{@code 404 NOT_FOUND} si el libro o el socio no existen.</li>
     * </ul>
     *
     * @param prestamo el {@link PrestamoModel} a persistir (debe contener
     *                 referencias a libro y socio con sus respectivos IDs).
     * @return el {@link PrestamoModel} guardado con todos sus datos completos.
     */
    public PrestamoModel guardarPrestamo(PrestamoModel prestamo) {

        if (prestamo.getLibro() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Debe indicar un libro"
            );
        }

        Long isbn = prestamo.getLibro().getIsbn();

        LibroModel libro = libroRepository.findById(isbn)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Libro no encontrado"
            ));

        if (libro.getEjemplaresDisponibles() <= 0) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "No hay suficientes ejemplares disponibles"
            );
        }

        prestamo.setLibro(libro);

        libro.setEjemplaresDisponibles(
            libro.getEjemplaresDisponibles() - 1
        );

        if (prestamo.getSocio() == null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "Debe indicar un socio"
            );
        }

        UsuarioModel socio = usuarioRepository
            .findById(prestamo.getSocio().getId())
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Socio no encontrado"
            ));

        prestamo.setSocio(socio);
        prestamo.setFechaDevolucion(null);

        return prestamoRepository.save(prestamo);
    }

    /**
     * Busca un préstamo por su identificador único.
     * Lanza {@link ResponseStatusException} con {@code 404 NOT_FOUND} si no
     * se encontró el préstamo.
     *
     * @param id el identificador único del préstamo.
     * @return el {@link PrestamoModel} encontrado.
     */
    public PrestamoModel obtenerPrestamoPorId(Long id) {
        return prestamoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado con ID: " + id));
    }

    /**
     * Retorna todos los préstamos asociados a un libro dado su ISBN.
     *
     * @param libroIsbn el ISBN del libro.
     * @return lista de {@link PrestamoModel} vinculados al libro indicado.
     */
    public ArrayList<PrestamoModel> obtenerPrestamosPorLibro(Long libroIsbn) {
        return prestamoRepository.findByLibroIsbn(libroIsbn);
    }

    /**
     * Retorna la lista completa de préstamos registrados en la base de datos.
     *
     * @return lista de todos los {@link PrestamoModel} existentes.
     */
    public ArrayList<PrestamoModel> obtenerTodosLosPrestamos() {
        return (ArrayList<PrestamoModel>) prestamoRepository.findAll();
    }

    /**
     * Actualiza el libro y el socio de un préstamo existente identificado por
     * su ID.
     * Lanza {@link ResponseStatusException} con {@code 404 NOT_FOUND} si el
     * préstamo, el libro o el usuario no existen.
     *
     * @param request objeto {@link PrestamoModel} con los nuevos valores.
     * @param id      el identificador único del préstamo a actualizar.
     * @return el {@link PrestamoModel} actualizado y persistido.
     */
    public PrestamoModel actualizarPrestamo(PrestamoModel request, Long id) {
        PrestamoModel prestamo = prestamoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado con ID: " + id));

        LibroModel libro = libroRepository.findById(request.getLibro().getIsbn())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado."));
        UsuarioModel socio = usuarioRepository.findById(request.getSocio().getId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado."));

        prestamo.setLibro(libro);
        prestamo.setSocio(socio);
        return prestamoRepository.save(prestamo);
    }

    /**
     * Registra la devolución de un préstamo activo: asigna la fecha y hora
     * actuales como {@code fechaDevolucion} e incrementa en uno los ejemplares
     * disponibles del libro correspondiente.
     * Lanza {@link ResponseStatusException} con:
     * <ul>
     *   <li>{@code 404 NOT_FOUND} si el préstamo no existe.</li>
     *   <li>{@code 400 BAD_REQUEST} si el préstamo ya fue devuelto anteriormente.</li>
     * </ul>
     *
     * @param id el identificador único del préstamo a devolver.
     * @return el {@link PrestamoModel} actualizado con la fecha de devolución.
     */
    public PrestamoModel devolverPrestamo(Long id) {
        PrestamoModel prestamo = prestamoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Préstamo no encontrado con ID: " + id));

        if (prestamo.getFechaDevolucion() != null) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST, "El préstamo ya está devuelto"
            );
        }
        prestamo.setFechaDevolucion(LocalDateTime.now());
        prestamo.getLibro().setEjemplaresDisponibles(prestamo.getLibro().getEjemplaresDisponibles() + 1);
        return prestamoRepository.save(prestamo);
    }

    /**
     * Elimina un préstamo de la base de datos por su ID.
     * Solo se permite eliminar préstamos ya devueltos (con {@code fechaDevolucion}
     * no nula).
     * Lanza {@link ResponseStatusException} con:
     * <ul>
     *   <li>{@code 404 NOT_FOUND} si el préstamo no existe.</li>
     *   <li>{@code 409 CONFLICT} si el préstamo aún está activo.</li>
     * </ul>
     *
     * @param id el identificador único del préstamo a eliminar.
     * @return {@code true} si el préstamo fue eliminado exitosamente.
     */
    public boolean eliminarPrestamo(Long id) {
        PrestamoModel prestamo = prestamoRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Préstamo no encontrado"
            ));

        if (prestamo.getFechaDevolucion() == null) {
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "No se puede eliminar un préstamo activo"
            );
        }

        prestamoRepository.delete(prestamo);
        return true;
    }

}
