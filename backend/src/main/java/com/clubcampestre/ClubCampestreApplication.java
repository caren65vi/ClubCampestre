package com.clubcampestre;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del backend del Club Campestre Villa Katy.
 * Inicia Spring Boot con el servidor Tomcat embebido y la API REST en el puerto 8080.
 */
@SpringBootApplication
public class ClubCampestreApplication {

	/** Arranca la aplicación Spring Boot. */
	public static void main(String[] args) {
		SpringApplication.run(ClubCampestreApplication.class, args);
	}

}
