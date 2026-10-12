package com.api.libreria.repositories;

import java.util.ArrayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.api.libreria.models.LibroModel;

/**
 * Repositorio JPA para la entidad {@link LibroModel}.
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD básicas
 * sobre la tabla "libros", e incorpora consultas derivadas personalizadas.
 *
 * @author G-Chelius-A
 */
@Repository
public interface ILibroRepository extends JpaRepository<LibroModel, Long> {

    /**
     * Retorna todos los libros cuyo autor coincida exactamente con el valor
     * indicado.
     *
     * @param autor el nombre del autor a buscar.
     * @return lista de {@link LibroModel} que coincidan con el autor dado.
     */
    ArrayList<LibroModel> findByAutor(String autor);

    /**
     * Retorna todos los libros cuyo título coincida exactamente con el valor
     * indicado.
     *
     * @param titulo el título del libro a buscar.
     * @return lista de {@link LibroModel} que coincidan con el título dado.
     */
    ArrayList<LibroModel> findByTitulo(String titulo);
}
