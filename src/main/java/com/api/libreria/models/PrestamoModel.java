package com.api.libreria.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

/**
 * Entidad JPA para el registro de préstamos en la biblioteca mediante la tabla
 * "prestamos".
 * Modela la relación entre un {@link LibroModel} y un {@link UsuarioModel}
 * (socio), registrando la fecha de préstamo y, opcionalmente, la fecha de
 * devolución.
 * Incluye métodos getters, setters y diferentes constructores sobrecargados.
 *
 * @author G-Chelius-A
 */
@Entity
@Table(name = "prestamos")
public class PrestamoModel {

    /**
     * Llave primaria de la entidad. Se genera automáticamente con una estrategia
     * de autoincremento ({@link GenerationType#IDENTITY}).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Libro asociado a este préstamo. Relación bidireccional
     * {@code Préstamo (N) <-> (1) Libro}. La carga es perezosa
     * ({@link FetchType#LAZY})
     * y el libro es obligatorio (no puede ser nulo).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "libro_isbn", nullable = false)
    private LibroModel libro;

    /**
     * Socio (usuario) asociado a este préstamo. Relación bidireccional
     * {@code Préstamo (N) <-> (1) Usuario}. La carga es perezosa
     * ({@link FetchType#LAZY})
     * y el socio es obligatorio (no puede ser nulo).
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "socio_id", nullable = false)
    private UsuarioModel socio;

    /**
     * Fecha y hora en que se realizó el préstamo. Este campo es obligatorio
     * e inmutable después de la creación del registro.
     */
    @Column(name = "fecha_prestamo", nullable = false, updatable = false)
    private LocalDateTime fechaPrestamo;

    /**
     * Fecha y hora de devolución del libro. Es {@code null} mientras el préstamo
     * esté activo y se actualiza al momento de la devolución.
     */
    @Column(name = "fecha_devolucion", updatable = true)
    private LocalDateTime fechaDevolucion;

    /**
     * Método invocado automáticamente antes de persistir la entidad por primera
     * vez.
     * Si {@code fechaPrestamo} no fue especificada, la inicializa con la fecha y
     * hora
     * actuales ({@link LocalDateTime#now()}).
     */
    @PrePersist
    private void asignarFechaPrestamo() {
        if (fechaPrestamo == null) {
            fechaPrestamo = LocalDateTime.now();
        }
    }

    /**
     * Retorna el identificador único del préstamo.
     *
     * @return el id del préstamo de tipo {@link Long}.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador único del préstamo.
     *
     * @param id el id del préstamo de tipo {@link Long}.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el libro asociado a este préstamo.
     *
     * @return el {@link LibroModel} vinculado al préstamo.
     */
    public LibroModel getLibro() {
        return libro;
    }

    /**
     * Asigna el libro asociado a este préstamo.
     *
     * @param libro el {@link LibroModel} a vincular con el préstamo.
     */
    public void setLibro(LibroModel libro) {
        this.libro = libro;
    }

    /**
     * Retorna el socio (usuario) que realizó el préstamo.
     *
     * @return el {@link UsuarioModel} vinculado al préstamo.
     */
    public UsuarioModel getSocio() {
        return socio;
    }

    /**
     * Asigna el socio (usuario) que realiza el préstamo.
     *
     * @param socio el {@link UsuarioModel} a vincular con el préstamo.
     */
    public void setSocio(UsuarioModel socio) {
        this.socio = socio;
    }

    /**
     * Retorna la fecha y hora en que se realizó el préstamo.
     *
     * @return la fecha de préstamo de tipo {@link LocalDateTime}.
     */
    public LocalDateTime getFechaPrestamo() {
        return fechaPrestamo;
    }

    /**
     * Asigna la fecha y hora en que se realizó el préstamo.
     *
     * @param fechaPrestamo la fecha de préstamo de tipo {@link LocalDateTime}.
     */
    public void setFechaPrestamo(LocalDateTime fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    /**
     * Retorna la fecha y hora de devolución del libro.
     * Puede ser {@code null} si el préstamo aún está activo.
     *
     * @return la fecha de devolución de tipo {@link LocalDateTime}, o {@code null}.
     */
    public LocalDateTime getFechaDevolucion() {
        return fechaDevolucion;
    }

    /**
     * Asigna la fecha y hora de devolución del libro.
     *
     * @param fechaDevolucion la fecha de devolución de tipo {@link LocalDateTime}.
     */
    public void setFechaDevolucion(LocalDateTime fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    /**
     * Método constructor sin argumentos.
     */
    public PrestamoModel() {
    }

    /**
     * Método constructor con todos los argumentos posibles según la clase,
     * incluyendo la fecha de devolución.
     *
     * @param id              el identificador único del préstamo.
     * @param libro           el libro asociado al préstamo.
     * @param socio           el socio (usuario) que realiza el préstamo.
     * @param fechaPrestamo   la fecha y hora en que se realizó el préstamo.
     * @param fechaDevolucion la fecha y hora de devolución del libro.
     */
    public PrestamoModel(Long id, LibroModel libro, UsuarioModel socio, LocalDateTime fechaPrestamo,
            LocalDateTime fechaDevolucion) {
        this.id = id;
        this.libro = libro;
        this.socio = socio;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucion = fechaDevolucion;
    }

    /**
     * Método constructor con los argumentos obligatorios de la entidad,
     * sin incluir la fecha de devolución (préstamo activo).
     *
     * @param id            el identificador único del préstamo.
     * @param libro         el libro asociado al préstamo.
     * @param socio         el socio (usuario) que realiza el préstamo.
     * @param fechaPrestamo la fecha y hora en que se realizó el préstamo.
     */
    public PrestamoModel(Long id, LibroModel libro, UsuarioModel socio, LocalDateTime fechaPrestamo) {
        this.id = id;
        this.libro = libro;
        this.socio = socio;
        this.fechaPrestamo = fechaPrestamo;
    }

}