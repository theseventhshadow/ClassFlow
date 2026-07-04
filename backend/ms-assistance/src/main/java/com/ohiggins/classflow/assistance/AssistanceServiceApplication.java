package com.ohiggins.classflow.assistance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio de asistencia de ClassFlow.
 */
@SpringBootApplication
public class AssistanceServiceApplication {

	/**
	 * Arranca la aplicacion del servicio de asistencia.
	 *
	 * @param args argumentos de linea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(AssistanceServiceApplication.class, args);
	}

}
