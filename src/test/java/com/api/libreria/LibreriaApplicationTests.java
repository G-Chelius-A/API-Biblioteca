package com.api.libreria;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
class LibreriaApplicationTests {

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}



	@Test
	void testRaizSinError500() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(view().name("libros"));
	}

	@Test
	void testVistaSinError500() throws Exception {
		mockMvc.perform(get("/vista"))
				.andExpect(status().isOk())
				.andExpect(view().name("libros"));
	}

	@Test
	void testVistaLibrosSinError500() throws Exception {
		mockMvc.perform(get("/vista/libros"))
				.andExpect(status().isOk())
				.andExpect(view().name("libros"));
	}

	@Test
	void testVistaUsuariosSinError500() throws Exception {
		mockMvc.perform(get("/vista/usuarios"))
				.andExpect(status().isOk())
				.andExpect(view().name("usuarios"));
	}

	@Test
	void testVistaPrestamosSinError500() throws Exception {
		mockMvc.perform(get("/vista/prestamos"))
				.andExpect(status().isOk())
				.andExpect(view().name("prestamos"));
	}

	@Test
	void testValidacionUsuarioMuestraMensajeErrorYNo500() throws Exception {
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/vista/usuarios/guardar")
				.param("nombre", "")
				.param("apellidos", "")
				.param("correo", "correo-invalido")
				.param("contrasena", "123"))
				.andExpect(status().is3xxRedirection())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/vista/usuarios"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("error"));
	}

	@Test
	void testValidacionPrestamoMuestraMensajeErrorYNo500() throws Exception {
		mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/vista/prestamos/guardar")
				.param("libro.isbn", "99999999")
				.param("socio.id", "99999999"))
				.andExpect(status().is3xxRedirection())
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl("/vista/prestamos"))
				.andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash().attributeExists("error"));
	}

}

