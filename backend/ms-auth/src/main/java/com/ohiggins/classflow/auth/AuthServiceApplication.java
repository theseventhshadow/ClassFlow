package com.ohiggins.classflow.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio de autenticacion de ClassFlow.
 */
@SpringBootApplication
public class AuthServiceApplication {

	/**
	 * Arranca la aplicacion del servicio de autenticacion.
	 *
	 * @param args argumentos de linea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(AuthServiceApplication.class, args);
	}

}
