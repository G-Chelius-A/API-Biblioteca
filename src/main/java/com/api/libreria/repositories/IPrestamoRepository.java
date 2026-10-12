package com.api.libreria.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.libreria.models.PrestamoModel;

/**
 * Repositorio JPA para la entidad {@link PrestamoModel}.
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD básicas
 * sobre la tabla "prestamos", e incorpora consultas derivadas personalizadas
 * por socio y por libro.
 *
 * @author G-Chelius-A
 */
@Repository
public interface IPrestamoRepository extends JpaRepository<PrestamoModel, Long> {

    /**
     * Retorna todos los préstamos asociados a un socio (usuario) dado su ID.
     *
     * @param socioId el identificador único del socio.
     * @return lista de {@link PrestamoModel} que pertenezcan al socio indicado.
     */
    ArrayList<PrestamoModel> findBySocioId(Long socioId);

    /**
     * Retorna todos los préstamos asociados a un libro dado su ISBN.
     *
     * @param libroIsbn el ISBN del libro.
     * @return lista de {@link PrestamoModel} que estén vinculados al libro indicado.
     */
    ArrayList<PrestamoModel> findByLibroIsbn(Long libroIsbn);
}
