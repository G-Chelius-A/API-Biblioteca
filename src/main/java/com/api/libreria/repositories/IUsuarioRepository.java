package com.api.libreria.repositories;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.api.libreria.models.UsuarioModel;

/**
 * Repositorio JPA para la entidad {@link UsuarioModel}.
 * Extiende {@link JpaRepository} para heredar las operaciones CRUD básicas
 * sobre la tabla "usuarios", e incorpora consultas derivadas personalizadas
 * por nombre, apellidos, correo, estado y rol.
 *
 * @author G-Chelius-A
 */
@Repository
public interface IUsuarioRepository extends JpaRepository<UsuarioModel, Long> {

    /**
     * Retorna todos los usuarios cuyo nombre coincida exactamente con el valor
     * indicado.
     *
     * @param nombre el nombre del usuario a buscar.
     * @return lista de {@link UsuarioModel} que coincidan con el nombre dado.
     */
    ArrayList<UsuarioModel> findByNombre(String nombre);

    /**
     * Retorna todos los usuarios cuyos apellidos coincidan exactamente con el
     * valor indicado.
     *
     * @param apellidos los apellidos del usuario a buscar.
     * @return lista de {@link UsuarioModel} que coincidan con los apellidos dados.
     */
    ArrayList<UsuarioModel> findByApellidos(String apellidos);

    /**
     * Retorna todos los usuarios cuyo correo electrónico coincida exactamente
     * con el valor indicado.
     *
     * @param correo el correo electrónico del usuario a buscar.
     * @return lista de {@link UsuarioModel} que coincidan con el correo dado.
     */
    ArrayList<UsuarioModel> findByCorreo(String correo);

    /**
     * Retorna todos los usuarios que tengan el estado de cuenta indicado.
     *
     * @param estado el estado de la cuenta ({@link UsuarioModel.Estados}) a filtrar.
     * @return lista de {@link UsuarioModel} que se encuentren en el estado dado.
     */
    ArrayList<UsuarioModel> findByEstado(UsuarioModel.Estados estado);

    /**
     * Retorna todos los usuarios que tengan el rol indicado.
     *
     * @param rol el rol ({@link UsuarioModel.Roles}) por el que filtrar.
     * @return lista de {@link UsuarioModel} que posean el rol dado.
     */
    ArrayList<UsuarioModel> findByRol(UsuarioModel.Roles rol);
}
