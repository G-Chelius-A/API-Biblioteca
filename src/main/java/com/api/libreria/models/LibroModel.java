package com.api.libreria.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;

/**
 * Entidad JPA para el control de libros en la biblioteca mediante la tabla
 * "libros".
 * Provee sus atributos, incluyendo una llave primaria y una lista de préstamos.
 * Incluye métodos getters, setters y diferentes constructores sobrecargados.
 * 
 * @author G-Chelius-A
 * 
 */
@Entity
@Table(name = "libros")
public class LibroModel {

    /**
     * Llave primaria de la entidad. Se genera automáticamente con una estrategia
     * de autoincremento ({@link GenerationType#IDENTITY}).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long isbn;

    @Column(name = "titulo", nullable = false, length = 100)
    private String titulo;

    @Column(name = "autor", length = 100)
    private String autor;

    @Column(name = "anio_publicacion", length = 100)
    private Integer anioPublicacion;

    @Min(1) // Mínimo 1 ejemplar
    @Column(name = "numero_ejemplares", nullable = false)
    private Integer numeroEjemplares;

    @Min(0) //Mínimo 0 ejemplares disponibles
    @Column(name = "ejemplares_disponibles", nullable = false)
    private Integer ejemplaresDisponibles;

    /**
     * Lista de préstamos asociados al libro. Relación bidireccional
     * {@code Libro (1) <-> (N) Préstamo}. La carga es perezosa (no se cargan de
     * inmediato los objetos) ({@link FetchType#LAZY})
     * y se ignora la referencia inversa al serializar a JSON para evitar recursión.
     */
    @OneToMany(mappedBy = "libro", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("libro")
    private List<PrestamoModel> prestamo;

    /**
     * Método invocado automáticamente antes de persistir la entidad por primera
     * vez.
     * Si {@code ejemplaresDisponibles} no fue especificado, lo inicializa con el
     * valor de {@code numeroEjemplares}.
     */
    @PrePersist
    private void asignarDisponibles() {
        if (ejemplaresDisponibles == null) {
            ejemplaresDisponibles = numeroEjemplares;
        }
    }

    /**
     * Retorna el valor del ISBN del libro.
     * 
     * @return el ISBN de tipo long.
     */
    public Long getIsbn() {
        return isbn;
    }

    /**
     * Asigna el ISBN del libro.
     * 
     * @param isbn el ISBN tipo Long del libro.
     */
    public void setIsbn(Long isbn) {
        this.isbn = isbn;
    }

    /**
     * Retorna el valor del título del libro.
     * 
     * @return el título de tipo String.
     */
    public String getTitulo() {
        return titulo;
    }

    /**
     * Asigna el título del libro.
     * 
     * @param titulo el título de tipo String del libro.
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /**
     * Retorna el valor del autor del libro.
     * 
     * @return el nombre del autor de tipo String.
     */
    public String getAutor() {
        return autor;
    }

    /**
     * Asigna el autor del libro.
     * 
     * @param autor el autor de tipo String del libro.
     */
    public void setAutor(String autor) {
        this.autor = autor;
    }

    /**
     * Retorna el año de publicación del libro.
     * 
     * @return el año de publicación de tipo Integer.
     */
    public Integer getAnioPublicacion() {
        return anioPublicacion;
    }

    /**
     * Asigna el año de publicación del libro
     * 
     * @param anioPublicacion el año de publicación de tipo Integer del libro.
     */
    public void setAnioPublicacion(Integer anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    /**
     * Retorna el número total de ejemplares del libro en la biblioteca.
     *
     * @return el número total de ejemplares de tipo {@link Integer}.
     */
    public Integer getNumeroEjemplares() {
        return numeroEjemplares;
    }

    /**
     * Asigna el número total de ejemplares del libro.
     *
     * @param numeroEjemplares el número total de ejemplares de tipo
     *                         {@link Integer}.
     */
    public void setNumeroEjemplares(Integer numeroEjemplares) {
        this.numeroEjemplares = numeroEjemplares;
    }

    /**
     * Retorna el número de ejemplares actualmente disponibles para préstamo.
     *
     * @return los ejemplares disponibles de tipo {@link Integer}.
     */
    public Integer getEjemplaresDisponibles() {
        return ejemplaresDisponibles;
    }

    /**
     * Asigna el número de ejemplares disponibles para préstamo.
     *
     * @param ejemplaresDisponibles los ejemplares disponibles de tipo
     *                              {@link Integer}.
     */
    public void setEjemplaresDisponibles(Integer ejemplaresDisponibles) {
        this.ejemplaresDisponibles = ejemplaresDisponibles;
    }

    /**
     * Retorna la lista de préstamos asociados al libro.
     *
     * @return la lista de {@link PrestamoModel} vinculados a este libro.
     */
    public List<PrestamoModel> getPrestamo() {
        return prestamo;
    }

    /**
     * Asigna la lista de préstamos asociados al libro.
     *
     * @param prestamo la lista de {@link PrestamoModel} a vincular con este libro.
     */
    public void setPrestamo(List<PrestamoModel> prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * Método constructor sin argumentos.
     */

    public LibroModel() {
    }

    /**
     * Método constructor con todos los argumentos posibles según la clase.
     *
     * @param isbn                  el identificador único (ISBN) del libro.
     * @param titulo                el título del libro.
     * @param autor                 el nombre del autor del libro.
     * @param anioPublicacion       el año de publicación del libro.
     * @param numeroEjemplares      el número total de ejemplares disponibles.
     * @param ejemplaresDisponibles el número de ejemplares actualmente disponibles.
     * @param prestamo              la lista de préstamos asociados al libro.
     */
    public LibroModel(Long isbn, String titulo, String autor, Integer anioPublicacion, Integer numeroEjemplares,
            Integer ejemplaresDisponibles,
            List<PrestamoModel> prestamo) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.autor = autor;
        this.anioPublicacion = anioPublicacion;
        this.numeroEjemplares = numeroEjemplares;
        this.ejemplaresDisponibles = ejemplaresDisponibles;
        this.prestamo = prestamo;
    }

    /**
     * Método constructor con los argumentos obligatorios de la entidad.
     *
     * @param isbn                  el identificador único (ISBN) del libro.
     * @param titulo                el título del libro.
     * @param numeroEjemplares      el número total de ejemplares disponibles.
     * @param ejemplaresDisponibles el número de ejemplares actualmente disponibles.
     * @param prestamo              la lista de préstamos asociados al libro.
     */
    public LibroModel(Long isbn, String titulo, Integer numeroEjemplares, Integer ejemplaresDisponibles,
            List<PrestamoModel> prestamo) {
        this.isbn = isbn;
        this.titulo = titulo;
        this.numeroEjemplares = numeroEjemplares;
        this.ejemplaresDisponibles = ejemplaresDisponibles;
        this.prestamo = prestamo;
    }

}
