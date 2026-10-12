package com.api.libreria.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api.libreria.repositories.ILibroRepository;
import com.api.libreria.repositories.IPrestamoRepository;
import com.api.libreria.models.LibroModel;
import com.api.libreria.models.PrestamoModel;

/**
 * Servicio que centraliza la lógica de negocio para la entidad
 * {@link LibroModel}.
 * Actúa como intermediario entre {@link com.api.libreria.controllers.LibroController}
 * y {@link ILibroRepository}, aplicando validaciones y reglas de negocio antes
 * de persistir o recuperar datos.
 *
 * @author G-Chelius-A
 */
@Service
public class LibroService {

    /**
     * Repositorio de libros inyectado por Spring para acceder a la capa de
     * persistencia de {@link LibroModel}.
     */
    @Autowired
    ILibroRepository libroRepository;

    /**
     * Repositorio de préstamos inyectado por Spring, utilizado para verificar
     * si un libro tiene préstamos activos antes de eliminarlo.
     */
    @Autowired
    IPrestamoRepository prestamoRepository;

    /**
     * Retorna la lista completa de libros registrados en la base de datos.
     *
     * @return lista de todos los {@link LibroModel} existentes.
     */
    public ArrayList<LibroModel> obtenerLibros() {
        return (ArrayList<LibroModel>) libroRepository.findAll();
    }

    /**
     * Guarda un nuevo libro en la base de datos. Si {@code ejemplaresDisponibles}
     * no fue proporcionado, lo inicializa con el valor de {@code numeroEjemplares}
     * (o {@code 0} si este también es nulo).
     *
     * @param libro el {@link LibroModel} a persistir.
     * @return el {@link LibroModel} guardado, con su ISBN asignado.
     */
    public LibroModel guardarLibro(LibroModel libro) {
        if (libro.getEjemplaresDisponibles() == null) {
            libro.setEjemplaresDisponibles(libro.getNumeroEjemplares() != null ? libro.getNumeroEjemplares() : 0);
        }
        return libroRepository.save(libro);
    }

    /**
     * Busca un libro por su ISBN.
     *
     * @param isbn el ISBN del libro a buscar.
     * @return un {@link Optional} con el {@link LibroModel} si existe,
     *         o vacío si no se encontró.
     */
    public Optional<LibroModel> obtenerPorId(Long isbn) {
        return libroRepository.findById(isbn);
    }

    /**
     * Actualiza los datos de un libro existente identificado por su ISBN.
     * Actualiza título, autor, año de publicación, número de ejemplares y,
     * opcionalmente, los ejemplares disponibles.
     * Lanza {@link ResponseStatusException} con {@code 404 NOT FOUND} si el
     * libro no existe.
     *
     * @param request objeto {@link LibroModel} con los nuevos valores a aplicar.
     * @param isbn    el ISBN del libro a actualizar.
     * @return el {@link LibroModel} actualizado y persistido.
     */
    public LibroModel actualizarLibro(LibroModel request, Long isbn) {
        LibroModel libro = libroRepository.findById(isbn)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Libro no encontrado con ISBN: " + isbn));
        libro.setTitulo(request.getTitulo());
        libro.setAutor(request.getAutor());
        libro.setAnioPublicacion(request.getAnioPublicacion());
        libro.setNumeroEjemplares(request.getNumeroEjemplares());
        if (request.getEjemplaresDisponibles() != null) {
            libro.setEjemplaresDisponibles(request.getEjemplaresDisponibles());
        }

        return libroRepository.save(libro);
    }

    /**
     * Elimina un libro de la base de datos por su ISBN.
     * Lanza {@link ResponseStatusException} con {@code 404 NOT FOUND} si el libro
     * no existe, o con {@code 409 CONFLICT} si el libro tiene préstamos activos
     * (sin fecha de devolución).
     *
     * @param isbn el ISBN del libro a eliminar.
     * @return {@code true} si el libro fue eliminado exitosamente.
     */
    public boolean eliminarLibro(Long isbn) {
        LibroModel libro = libroRepository.findById(isbn)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Libro no encontrado con ISBN: " + isbn
            ));

        ArrayList<PrestamoModel> prestamo =
            prestamoRepository.findByLibroIsbn(isbn);

        for (PrestamoModel p : prestamo) {
            if (p.getFechaDevolucion() == null) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar el libro porque tiene préstamos activos"
                );
            }
        }

        libroRepository.delete(libro);
        return true;
    }

    /**
     * Retorna todos los libros cuyo autor coincida exactamente con el valor
     * indicado.
     *
     * @param autor el nombre del autor a buscar.
     * @return lista de {@link LibroModel} que coincidan con el autor dado.
     */
    public ArrayList<LibroModel> obtenerPorAutor(String autor) {
        return libroRepository.findByAutor(autor);
    }

    /**
     * Retorna todos los libros cuyo título coincida exactamente con el valor
     * indicado.
     *
     * @param titulo el título del libro a buscar.
     * @return lista de {@link LibroModel} que coincidan con el título dado.
     */
    public ArrayList<LibroModel> obtenerPorTitulo(String titulo) {
        return libroRepository.findByTitulo(titulo);
    }

}
