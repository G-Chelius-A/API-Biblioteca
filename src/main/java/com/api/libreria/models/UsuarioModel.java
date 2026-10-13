package com.api.libreria.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
import jakarta.validation.constraints.Email;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Entidad JPA para el registro de usuarios (socios) de la biblioteca mediante
 * la tabla "usuarios".
 * Provee sus atributos personales y de acceso, incluyendo una llave primaria,
 * rol, estado y una lista de préstamos asociados.
 * Incluye validaciones de Bean Validation, métodos getters, setters y
 * diferentes constructores sobrecargados.
 *
 * @author G-Chelius-A
 */
@Entity
@Table(name = "usuarios")
public class UsuarioModel {

    /**
     * Llave primaria de la entidad. Se genera automáticamente con una estrategia
     * de autoincremento ({@link GenerationType#IDENTITY}).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre(s) del usuario. Campo obligatorio con una longitud máxima de 50
     * caracteres.
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Se requiere un nombre válido")
    private String nombre;

    /**
     * Apellidos del usuario. Campo obligatorio con una longitud máxima de 50
     * caracteres.
     */
    @Column(nullable = false, length = 50)
    @NotBlank(message = "Se requiere apellidos válidos")
    private String apellidos;

    /**
     * Correo electrónico del usuario. Debe ser único en el sistema y tener un
     * formato válido. Campo obligatorio.
     */
    @Email(message = "El email debe ser válido")
    @NotBlank(message = "El email no puede estar vacío")
    @Column(nullable = false, unique = true)
    private String correo;

    /**
     * Contraseña del usuario. Campo obligatorio con un mínimo de 8 caracteres.
     */
    @Column(nullable = false)
    @NotBlank(message = "Se requiere una contraseña de mínimo 8 caracteres")
    @Size(min = 8, message = "Se requiere una contraseña de mínimo 8 caracteres")
    private String contrasena;

    /**
     * Rol asignado al usuario dentro del sistema. Determina los permisos y
     * acciones disponibles. Se almacena como cadena de texto
     * ({@link EnumType#STRING}).
     *
     * @see Roles
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Roles rol;

    /**
     * Estado actual de la cuenta del usuario. Indica si el usuario puede o no
     * operar en el sistema. Se almacena como cadena de texto
     * ({@link EnumType#STRING}).
     *
     * @see Estados
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estados estado;

    /**
     * Fecha y hora de creación del registro del usuario. Se asigna
     * automáticamente al momento de persistir por primera vez.
     */
    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha de nacimiento del usuario. Campo obligatorio e inmutable después
     * de la creación del registro. No puede ser una fecha futura.
     */
    @Column(nullable = false, updatable = false)
    @NotNull(message = "Se requiere una fecha de nacimiento")
    @Past(message = "La fecha de nacimiento no puede ser en el futuro")
    private LocalDate fechaNacimiento;

    /**
     * Número de teléfono del usuario en formato internacional.
     * Debe ser único en el sistema y cumplir el patrón {@code +?[1-9]\d{1,14}}.
     * Ejemplo válido: {@code +522291234567}.
     */
    @NotBlank(message = "El número de teléfono es obligatorio")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "El número de teléfono debe tener un formato internacional válido (ej: +522291234567)")
    @Column(nullable = false, unique = true)
    private String telefono;

    /**
     * Dirección física del usuario. Campo opcional.
     */
    @Column
    private String direccion;

    /**
     * Método invocado automáticamente antes de persistir la entidad por primera
     * vez.
     * Si {@code fechaCreacion} no fue especificada, la inicializa con la fecha y
     * hora
     * actuales ({@link LocalDateTime#now()}).
     */
    @PrePersist
    private void asignarFechaCreacion() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    /**
     * Enumeración de los roles disponibles para los usuarios del sistema.
     *
     * <ul>
     * <li>{@link #ADMIN} - Acceso total al sistema.</li>
     * <li>{@link #BIBLIOTECARIO} - Gestión de libros y préstamos.</li>
     * <li>{@link #LECTOR} - Consulta del catálogo y préstamos propios.</li>
     * </ul>
     */
    public enum Roles {
        ADMIN,
        BIBLIOTECARIO,
        LECTOR
    }

    /**
     * Enumeración de los estados posibles de la cuenta de un usuario.
     *
     * <ul>
     * <li>{@link #ACTIVO} - El usuario puede operar con normalidad.</li>
     * <li>{@link #INACTIVO} - La cuenta está desactivada.</li>
     * <li>{@link #SUSPENDIDO} - La cuenta ha sido suspendida temporalmente.</li>
     * </ul>
     */
    public enum Estados {
        ACTIVO,
        INACTIVO,
        SUSPENDIDO
    }

    /**
     * Lista de préstamos realizados por este usuario (socio). Relación
     * bidireccional
     * {@code Usuario (1) ↔ (N) Préstamo}. La carga es perezosa
     * ({@link FetchType#LAZY})
     * y se ignora la referencia inversa al serializar a JSON para evitar recursión.
     */
    @OneToMany(mappedBy = "socio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("socio")
    private List<PrestamoModel> prestamo;

    /**
     * Retorna el identificador único del usuario.
     *
     * @return el id del usuario de tipo {@link Long}.
     */
    public Long getId() {
        return id;
    }

    /**
     * Asigna el identificador único del usuario.
     *
     * @param id el id del usuario de tipo {@link Long}.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Retorna el nombre del usuario.
     *
     * @return el nombre de tipo {@link String}.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Asigna el nombre del usuario.
     *
     * @param nombre el nombre de tipo {@link String} del usuario.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Retorna los apellidos del usuario.
     *
     * @return los apellidos de tipo {@link String}.
     */
    public String getApellidos() {
        return apellidos;
    }

    /**
     * Asigna los apellidos del usuario.
     *
     * @param apellidos los apellidos de tipo {@link String} del usuario.
     */
    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    /**
     * Retorna el correo electrónico del usuario.
     *
     * @return el correo electrónico de tipo {@link String}.
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * Asigna el correo electrónico del usuario.
     *
     * @param correo el correo electrónico de tipo {@link String} del usuario.
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * Retorna la contraseña del usuario.
     *
     * @return la contraseña de tipo {@link String}.
     */
    public String getContrasena() {
        return contrasena;
    }

    /**
     * Asigna la contraseña del usuario.
     *
     * @param contrasena la contraseña de tipo {@link String} del usuario.
     */
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    /**
     * Retorna el rol del usuario en el sistema.
     *
     * @return el rol de tipo {@link Roles}.
     */
    public Roles getRol() {
        return rol;
    }

    /**
     * Asigna el rol del usuario en el sistema.
     *
     * @param rol el rol de tipo {@link Roles} a asignar al usuario.
     */
    public void setRol(Roles rol) {
        this.rol = rol;
    }

    /**
     * Retorna el estado actual de la cuenta del usuario.
     *
     * @return el estado de tipo {@link Estados}.
     */
    public Estados getEstado() {
        return estado;
    }

    /**
     * Asigna el estado de la cuenta del usuario.
     *
     * @param estado el estado de tipo {@link Estados} a asignar al usuario.
     */
    public void setEstado(Estados estado) {
        this.estado = estado;
    }

    /**
     * Retorna la fecha y hora de creación del registro del usuario.
     *
     * @return la fecha de creación de tipo {@link LocalDateTime}.
     */
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    /**
     * Asigna la fecha y hora de creación del registro del usuario.
     *
     * @param fechaCreacion la fecha de creación de tipo {@link LocalDateTime}.
     */
    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    /**
     * Retorna la fecha de nacimiento del usuario.
     *
     * @return la fecha de nacimiento de tipo {@link LocalDate}.
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Asigna la fecha de nacimiento del usuario.
     *
     * @param fechaNacimiento la fecha de nacimiento de tipo {@link LocalDate}.
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Retorna el número de teléfono del usuario.
     *
     * @return el número de teléfono de tipo {@link String} en formato
     *         internacional.
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * Asigna el número de teléfono del usuario.
     *
     * @param telefono el número de teléfono de tipo {@link String} en formato
     *                 internacional (ej: {@code +522291234567}).
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * Retorna la dirección física del usuario.
     *
     * @return la dirección de tipo {@link String}, o {@code null} si no fue
     *         proporcionada.
     */
    public String getDireccion() {
        return direccion;
    }

    /**
     * Asigna la dirección física del usuario.
     *
     * @param direccion la dirección de tipo {@link String} del usuario.
     */
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    /**
     * Retorna la lista de préstamos realizados por este usuario.
     *
     * @return la lista de {@link PrestamoModel} vinculados a este usuario.
     */
    public List<PrestamoModel> getPrestamo() {
        return prestamo;
    }

    /**
     * Asigna la lista de préstamos realizados por este usuario.
     *
     * @param prestamo la lista de {@link PrestamoModel} a vincular con el usuario.
     */
    public void setPrestamo(List<PrestamoModel> prestamo) {
        this.prestamo = prestamo;
    }

    /**
     * Método constructor sin argumentos.
     */
    public UsuarioModel() {
    }

    /**
     * Método constructor con todos los argumentos posibles según la clase,
     * incluyendo la lista de préstamos y la dirección.
     *
     * @param id              el identificador único del usuario.
     * @param nombre          el nombre del usuario.
     * @param apellidos       los apellidos del usuario.
     * @param correo          el correo electrónico del usuario.
     * @param contrasena      la contraseña del usuario.
     * @param rol             el rol del usuario en el sistema.
     * @param estado          el estado de la cuenta del usuario.
     * @param fechaCreacion   la fecha y hora de creación del registro.
     * @param fechaNacimiento la fecha de nacimiento del usuario.
     * @param telefono        el número de teléfono en formato internacional.
     * @param direccion       la dirección física del usuario.
     * @param prestamo        la lista de préstamos asociados al usuario.
     */
    public UsuarioModel(Long id, String nombre, String apellidos,
            @Email(message = "El email debe ser válido") @NotBlank(message = "El email no puede estar vacío") String correo,
            String contrasena, Roles rol, Estados estado, LocalDateTime fechaCreacion, LocalDate fechaNacimiento,
            @NotBlank(message = "El número de teléfono es obligatorio") @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "El número de teléfono debe tener un formato internacional válido (ej: +522291234567)") String telefono,
            String direccion, List<PrestamoModel> prestamo) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.direccion = direccion;
        this.prestamo = prestamo;
    }

    /**
     * Método constructor con los argumentos obligatorios de la entidad,
     * sin incluir la dirección ni la lista de préstamos.
     *
     * @param id              el identificador único del usuario.
     * @param nombre          el nombre del usuario.
     * @param apellidos       los apellidos del usuario.
     * @param correo          el correo electrónico del usuario.
     * @param contrasena      la contraseña del usuario.
     * @param rol             el rol del usuario en el sistema.
     * @param estado          el estado de la cuenta del usuario.
     * @param fechaCreacion   la fecha y hora de creación del registro.
     * @param fechaNacimiento la fecha de nacimiento del usuario.
     * @param telefono        el número de teléfono en formato internacional.
     */
    public UsuarioModel(Long id, String nombre, String apellidos,
            @Email(message = "El email debe ser válido") @NotBlank(message = "El email no puede estar vacío") String correo,
            String contrasena, Roles rol, Estados estado, LocalDateTime fechaCreacion, LocalDate fechaNacimiento,
            @NotBlank(message = "El número de teléfono es obligatorio") @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "El número de teléfono debe tener un formato internacional válido (ej: +522291234567)") String telefono) {
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.estado = estado;
        this.fechaCreacion = fechaCreacion;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
    }

}
