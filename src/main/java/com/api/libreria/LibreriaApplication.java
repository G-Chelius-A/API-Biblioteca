package com.api.libreria;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class LibreriaApplication {

	public static void main(String[] args) {
		SpringApplication.run(LibreriaApplication.class, args);
	}

}

/* Llenar para libro

{
    "autor": "Juan Gomez",
    "añoPublicacion": "2025",
    "numeroEjemplares": 2,
    "titulo": "La Odisea"
}

Llenar para usuario

{
    "nombre": "Pedro",
    "apellidos": "Picapiedra",
    "correo": "picapiedra@gmail.com",
    "contrasena": "pass1234",
    "rol": "Lector",
    "estado": "Activo",
    "fechaCreacion": "04-09-2026",
    "fechaNacimiento": "01-01-1250",
    "telefono": "+5211111111",
    "direccion": "Dinosario 123"
}

*/
