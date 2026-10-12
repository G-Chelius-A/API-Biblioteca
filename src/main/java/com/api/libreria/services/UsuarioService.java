package com.api.libreria.services;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.api.libreria.models.PrestamoModel;
import com.api.libreria.models.UsuarioModel;
import com.api.libreria.repositories.IPrestamoRepository;
import com.api.libreria.repositories.IUsuarioRepository;

/**
 * Servicio que centraliza la lógica de negocio para la entidad
 * {@link UsuarioModel}.
 * Actúa como intermediario entre {@link com.api.libreria.controllers.UsuarioController}
 * y {@link IUsuarioRepository}, aplicando validaciones y separando las operaciones
 * de actualización por responsabilidad (datos personales, contraseña, estado y rol).
 *
 * <p><b>Nota:</b> La {@code fechaCreacion} no se modifica en ninguna operación de
 * actualización, ya que es un valor fijo asignado en la persistencia inicial.</p>
 *
 * @author G-Chelius-A
 */
@Service
public class UsuarioService {

    /**
     * Repositorio de usuarios inyectado por Spring para acceder a la capa de
     * persistencia de {@link UsuarioModel}.
     */
    @Autowired
    private IUsuarioRepository usuarioRepository;

    /**
     * Repositorio de préstamos inyectado por Spring, utilizado para verificar
     * si un usuario tiene préstamos activos antes de eliminarlo.
     */
    @Autowired
    private IPrestamoRepository prestamoRepository;

    /**
     * Retorna la lista completa de usuarios registrados en la base de datos.
     *
     * @return lista de todos los {@link UsuarioModel} existentes.
     */
    public ArrayList<UsuarioModel> obtenerUsuarios() {
        return (ArrayList<UsuarioModel>) usuarioRepository.findAll();
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario el {@link UsuarioModel} a persistir.
     * @return el {@link UsuarioModel} guardado, con su ID asignado.
     */
    public UsuarioModel guardarUsuario(UsuarioModel usuario) {
        return usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por su identificador único.
     *
     * @param id el identificador único del usuario.
     * @return un {@link Optional} con el {@link UsuarioModel} si existe,
     *         o vacío si no se encontró.
     */
    public Optional<UsuarioModel> obtenerPorId(long id) {
        return usuarioRepository.findById(id);
    }

    /**
     * Actualiza los datos personales de un usuario existente: nombre, apellidos,
     * correo, dirección, teléfono y fecha de nacimiento.
     * Lanza {@link RuntimeException} si el usuario no existe con el ID dado.
     *
     * <p><b>Nota:</b> La {@code fechaCreacion} no se modifica en esta operación.</p>
     *
     * @param request objeto {@link UsuarioModel} con los nuevos valores a aplicar.
     * @param id      el identificador único del usuario a actualizar.
     * @return el {@link UsuarioModel} actualizado y persistido.
     */
    public UsuarioModel actualizarUsuario(UsuarioModel request, Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
        usuario.setNombre(request.getNombre());
        usuario.setApellidos(request.getApellidos());
        usuario.setCorreo(request.getCorreo());
        usuario.setDireccion(request.getDireccion());
        usuario.setTelefono(request.getTelefono());
        usuario.setFechaNacimiento(request.getFechaNacimiento());

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza únicamente la contraseña de un usuario existente.
     * Lanza {@link RuntimeException} si el usuario no existe con el ID dado.
     *
     * @param request objeto {@link UsuarioModel} que contiene la nueva contraseña.
     * @param id      el identificador único del usuario.
     * @return el {@link UsuarioModel} con la contraseña actualizada y persistido.
     */
    public UsuarioModel actualizarContrasena(UsuarioModel request, long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
        usuario.setContrasena(request.getContrasena());

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza únicamente el estado de la cuenta de un usuario existente.
     * Lanza {@link RuntimeException} si el usuario no existe con el ID dado.
     *
     * @param request objeto {@link UsuarioModel} que contiene el nuevo estado.
     * @param id      el identificador único del usuario.
     * @return el {@link UsuarioModel} con el estado actualizado y persistido.
     */
    public UsuarioModel actualizarEstado(UsuarioModel request, long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
        usuario.setEstado(request.getEstado());

        return usuarioRepository.save(usuario);
    }

    /**
     * Actualiza únicamente el rol de un usuario existente.
     * Lanza {@link RuntimeException} si el usuario no existe con el ID dado.
     *
     * @param request objeto {@link UsuarioModel} que contiene el nuevo rol.
     * @param id      el identificador único del usuario.
     * @return el {@link UsuarioModel} con el rol actualizado y persistido.
     */
    public UsuarioModel actualizarRol(UsuarioModel request, long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Usuario no encontrado con el ID: " + id));
        usuario.setRol(request.getRol());

        return usuarioRepository.save(usuario);
    }

    /**
     * Elimina un usuario de la base de datos por su ID.
     * Verifica que el usuario no tenga préstamos activos (sin fecha de devolución)
     * antes de proceder.
     * Lanza {@link ResponseStatusException} con:
     * <ul>
     *   <li>{@code 404 NOT_FOUND} si el usuario no existe.</li>
     *   <li>{@code 409 CONFLICT} si el usuario tiene préstamos activos.</li>
     * </ul>
     *
     * @param id el identificador único del usuario a eliminar.
     * @return {@code true} si el usuario fue eliminado exitosamente.
     */
    public boolean eliminarUsuario(Long id) {
        UsuarioModel usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Usuario no encontrado con el ID: " + id
            ));

        ArrayList<PrestamoModel> prestamo =
            prestamoRepository.findBySocioId(id);

        for (PrestamoModel p : prestamo) {
            if (p.getFechaDevolucion() == null) {
                throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar el usuario porque tiene préstamos activos"
                );
            }
        }

        usuarioRepository.delete(usuario);
        return true;
    }

    /**
     * Retorna todos los usuarios cuyo nombre coincida exactamente con el valor
     * indicado.
     *
     * @param nombre el nombre del usuario a buscar.
     * @return lista de {@link UsuarioModel} que coincidan con el nombre dado.
     */
    public ArrayList<UsuarioModel> obtenerPorNombre(String nombre) {
        return usuarioRepository.findByNombre(nombre);
    }

    /**
     * Retorna todos los usuarios cuyos apellidos coincidan exactamente con el
     * valor indicado.
     *
     * @param apellidos los apellidos del usuario a buscar.
     * @return lista de {@link UsuarioModel} que coincidan con los apellidos dados.
     */
    public ArrayList<UsuarioModel> obtenerPorApellidos(String apellidos) {
        return usuarioRepository.findByApellidos(apellidos);
    }

    /**
     * Retorna todos los usuarios cuyo correo electrónico coincida exactamente
     * con el valor indicado.
     *
     * @param correo el correo electrónico del usuario a buscar.
     * @return lista de {@link UsuarioModel} que coincidan con el correo dado.
     */
    public ArrayList<UsuarioModel> obtenerPorCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo);
    }

    /**
     * Retorna todos los usuarios que tengan el estado de cuenta indicado.
     *
     * @param estado el estado ({@link UsuarioModel.Estados}) por el que filtrar.
     * @return lista de {@link UsuarioModel} que se encuentren en el estado dado.
     */
    public ArrayList<UsuarioModel> obtenerPorEstado(UsuarioModel.Estados estado) {
        return usuarioRepository.findByEstado(estado);
    }

    /**
     * Retorna todos los usuarios que tengan el rol indicado.
     *
     * @param rol el rol ({@link UsuarioModel.Roles}) por el que filtrar.
     * @return lista de {@link UsuarioModel} que posean el rol dado.
     */
    public ArrayList<UsuarioModel> obtenerPorRol(UsuarioModel.Roles rol) {
        return usuarioRepository.findByRol(rol);
    }

}
