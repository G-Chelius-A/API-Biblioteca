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

import com.api.libreria.models.UsuarioModel;
import com.api.libreria.services.UsuarioService;

/**
 * Controlador REST que expone los endpoints de la API para la gestión de
 * usuarios bajo la ruta base {@code /usuarios}.
 * Delega toda la lógica de negocio en {@link UsuarioService}.
 * Las operaciones de actualización se separan por responsabilidad: datos
 * personales, contraseña, estado y rol.
 *
 * @author G-Chelius-A
 */
@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    /**
     * Servicio de usuarios inyectado por Spring.
     */
    @Autowired
    private UsuarioService usuarioService;

    /**
     * {@code GET /usuarios} — Retorna la lista completa de usuarios registrados.
     *
     * @return lista de todos los {@link UsuarioModel} existentes.
     */
    @GetMapping
    public ArrayList<UsuarioModel> obtenerUsuarios() {
        return this.usuarioService.obtenerUsuarios();
    }

    /**
     * {@code POST /usuarios} — Registra un nuevo usuario.
     * Responde con {@code 201 Created} si el registro es exitoso.
     *
     * @param usuario el {@link UsuarioModel} a guardar, deserializado del cuerpo JSON.
     * @return el {@link UsuarioModel} guardado con su ID asignado.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioModel guardarUsuario(@RequestBody UsuarioModel usuario) {
        return this.usuarioService.guardarUsuario(usuario);
    }

    /**
     * {@code GET /usuarios/{id}} — Busca un usuario por su ID.
     *
     * @param id el identificador único del usuario, extraído de la URL.
     * @return un {@link Optional} con el {@link UsuarioModel} si existe,
     *         o vacío si no se encontró.
     */
    @GetMapping(path = "/{id}")
    public Optional<UsuarioModel> obtenerPorId(@PathVariable("id") Long id) {
        return this.usuarioService.obtenerPorId(id);
    }

    /**
     * {@code PUT /usuarios/{id}} — Actualiza los datos personales de un usuario
     * (nombre, apellidos, correo, dirección, teléfono y fecha de nacimiento).
     *
     * @param request los nuevos datos personales del usuario, deserializados del
     *                cuerpo JSON.
     * @param id      el identificador único del usuario, extraído de la URL.
     * @return el {@link UsuarioModel} actualizado y persistido.
     */
    @PutMapping(path = "/{id}")
    public UsuarioModel actualizarUsuario(@RequestBody UsuarioModel request, @PathVariable("id") Long id) {
        return this.usuarioService.actualizarUsuario(request, id);
    }

    /**
     * {@code PUT /usuarios/{id}/contrasena} — Actualiza únicamente la contraseña
     * de un usuario.
     *
     * @param request objeto {@link UsuarioModel} que contiene la nueva contraseña.
     * @param id      el identificador único del usuario, extraído de la URL.
     * @return el {@link UsuarioModel} con la contraseña actualizada.
     */
    @PutMapping(path = "/{id}/contrasena")
    public UsuarioModel actualizarContrasena(@RequestBody UsuarioModel request, @PathVariable("id") Long id) {
        return this.usuarioService.actualizarContrasena(request, id);
    }

    /**
     * {@code PUT /usuarios/{id}/estado} — Actualiza únicamente el estado de la
     * cuenta de un usuario.
     *
     * @param request objeto {@link UsuarioModel} que contiene el nuevo estado.
     * @param id      el identificador único del usuario, extraído de la URL.
     * @return el {@link UsuarioModel} con el estado actualizado.
     */
    @PutMapping(path = "/{id}/estado")
    public UsuarioModel actualizarEstado(@RequestBody UsuarioModel request, @PathVariable("id") Long id) {
        return this.usuarioService.actualizarEstado(request, id);
    }

    /**
     * {@code PUT /usuarios/{id}/rol} — Actualiza únicamente el rol de un usuario.
     *
     * @param request objeto {@link UsuarioModel} que contiene el nuevo rol.
     * @param id      el identificador único del usuario, extraído de la URL.
     * @return el {@link UsuarioModel} con el rol actualizado.
     */
    @PutMapping(path = "/{id}/rol")
    public UsuarioModel actualizarRol(@RequestBody UsuarioModel request, @PathVariable("id") Long id) {
        return this.usuarioService.actualizarRol(request, id);
    }

    /**
     * {@code DELETE /usuarios/{id}} — Elimina un usuario por su ID.
     * No se permite eliminar usuarios con préstamos activos.
     * Responde con {@code 204 No Content} si la eliminación es exitosa.
     *
     * @param id el identificador único del usuario, extraído de la URL.
     * @return mensaje de confirmación o de error como cadena de texto.
     */
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "/{id}")
    public String eliminarPorId(@PathVariable("id") Long id) {
        boolean ok = this.usuarioService.eliminarUsuario(id);
        if (ok) {
            return "Se eliminó el usuario con ID: " + id;
        } else {
            return "No se pudo eliminar el usuario con ID: " + id;
        }
    }

    /**
     * {@code GET /usuarios/nombre/{nombre}} — Busca usuarios por nombre.
     *
     * @param nombre el nombre del usuario a buscar, extraído de la URL.
     * @return lista de {@link UsuarioModel} que coincidan con el nombre dado.
     */
    @GetMapping(path = "/nombre/{nombre}")
    public ArrayList<UsuarioModel> buscarPorNombre(@PathVariable("nombre") String nombre) {
        return this.usuarioService.obtenerPorNombre(nombre);
    }

    /**
     * {@code GET /usuarios/apellidos/{apellidos}} — Busca usuarios por apellidos.
     *
     * @param apellidos los apellidos del usuario a buscar, extraídos de la URL.
     * @return lista de {@link UsuarioModel} que coincidan con los apellidos dados.
     */
    @GetMapping(path = "/apellidos/{apellidos}")
    public ArrayList<UsuarioModel> buscarPorApellidos(@PathVariable("apellidos") String apellidos) {
        return this.usuarioService.obtenerPorApellidos(apellidos);
    }

    /**
     * {@code GET /usuarios/correo/{correo}} — Busca usuarios por correo
     * electrónico.
     *
     * @param correo el correo electrónico del usuario a buscar, extraído de la URL.
     * @return lista de {@link UsuarioModel} que coincidan con el correo dado.
     */
    @GetMapping(path = "/correo/{correo}")
    public ArrayList<UsuarioModel> buscarPorCorreo(@PathVariable("correo") String correo) {
        return this.usuarioService.obtenerPorCorreo(correo);
    }

    /**
     * {@code GET /usuarios/estado/{estado}} — Busca usuarios por estado de cuenta.
     *
     * @param estado el estado ({@link UsuarioModel.Estados}) a filtrar,
     *               extraído de la URL.
     * @return lista de {@link UsuarioModel} que se encuentren en el estado dado.
     */
    @GetMapping(path = "/estado/{estado}")
    public ArrayList<UsuarioModel> buscarPorEstado(@PathVariable("estado") UsuarioModel.Estados estado) {
        return this.usuarioService.obtenerPorEstado(estado);
    }

    /**
     * {@code GET /usuarios/rol/{rol}} — Busca usuarios por rol.
     *
     * @param rol el rol ({@link UsuarioModel.Roles}) a filtrar, extraído de la URL.
     * @return lista de {@link UsuarioModel} que posean el rol dado.
     */
    @GetMapping(path = "/rol/{rol}")
    public ArrayList<UsuarioModel> buscarPorRol(@PathVariable("rol") UsuarioModel.Roles rol) {
        return this.usuarioService.obtenerPorRol(rol);
    }
}
