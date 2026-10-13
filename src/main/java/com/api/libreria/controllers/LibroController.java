package com.api.libreria.controllers;

import java.util.ArrayList;
import java.util.Optional;

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

import com.api.libreria.models.LibroModel;
import com.api.libreria.services.LibroService;
import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints de la API para la gestión de
 * libros bajo la ruta base {@code /libros}.
 * Delega toda la lógica de negocio en {@link LibroService}.
 *
 * @author G-Chelius-A
 */
@RestController
@RequestMapping("/libros")
public class LibroController {

    /**
     * Servicio de libros inyectado por Spring.
     */
    @Autowired
    private LibroService libroService;

    /**
     * {@code GET /libros} — Retorna la lista completa de libros registrados.
     *
     * @return lista de todos los {@link LibroModel} existentes.
     */
    @GetMapping
    public ArrayList<LibroModel> obtenerLibros() {
        return this.libroService.obtenerLibros();
    }

    /**
     * {@code POST /libros} — Registra un nuevo libro.
     * El cuerpo de la petición forma un objeto {@link LibroModel}.
     * Responde con {@code 201 Created} si el registro es exitoso.
     *
     * @param libro el {@link LibroModel} a guardar, deserializado del cuerpo JSON.
     * @return el {@link LibroModel} guardado con su ISBN asignado.
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public LibroModel gaurdarLibro(@Valid @RequestBody LibroModel libro) {
        return this.libroService.guardarLibro(libro);
    }

    /**
     * {@code GET /libros/{isbn}} — Busca un libro por su ISBN.
     *
     * @param isbn el ISBN del libro a buscar, extraído de la URL.
     * @return un {@link Optional} con el {@link LibroModel} si existe,
     *         o vacío si no se encontró.
     */
    @GetMapping(path = "/{isbn}")
    public Optional<LibroModel> obtenerPorId(@PathVariable("isbn") Long isbn) {
        return this.libroService.obtenerPorId(isbn);
    }

    /**
     * {@code PUT /libros/{isbn}} — Actualiza los datos de un libro existente.
     *
     * @param request los nuevos datos del libro, deserializados del cuerpo JSON.
     * @param isbn    el ISBN del libro a actualizar, extraído de la URL.
     * @return el {@link LibroModel} actualizado y persistido.
     */
    @PutMapping(path = "/{isbn}")
    public LibroModel actualizarLibro(@Valid @RequestBody LibroModel request, @PathVariable("isbn") Long isbn) {
        return this.libroService.actualizarLibro(request, isbn);
    }

    /**
     * {@code DELETE /libros/{isbn}} — Elimina un libro por su ISBN.
     * Responde con {@code 204 No Content} si la eliminación es exitosa.
     *
     * @param isbn el ISBN del libro a eliminar, extraído de la URL.
     * @return mensaje de confirmación o de error como cadena de texto.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{isbn}")
    public String eliminarPorId(@PathVariable("isbn") Long isbn) {
        boolean ok = this.libroService.eliminarLibro(isbn);
        if (ok) {
            return "Se eliminó el libro con ISBN: " + isbn;
        } else {
            return "No se pudo eliminar el libro con ISBN: " + isbn;
        }
    }

    /**
     * {@code GET /libros/autor/{autor}} — Busca libros por nombre de autor.
     * Título y autor no son campos primarios, por lo que se utiliza un patrón
     * de ruta específico en lugar del ID.
     *
     * @param autor el nombre del autor a buscar, extraído de la URL.
     * @return lista de {@link LibroModel} que coincidan con el autor dado.
     */
    @GetMapping(path = "/autor/{autor}")
    public ArrayList<LibroModel> buscarPorAutor(@PathVariable("autor") String autor) {
        return this.libroService.obtenerPorAutor(autor);
    }

    /**
     * {@code GET /libros/titulo/{titulo}} — Busca libros por título.
     *
     * @param titulo el título del libro a buscar, extraído de la URL.
     * @return lista de {@link LibroModel} que coincidan con el título dado.
     */
    @GetMapping(path = "/titulo/{titulo}")
    public ArrayList<LibroModel> buscarPorTitulo(@PathVariable("titulo") String titulo) {
        return this.libroService.obtenerPorTitulo(titulo);
    }
}
