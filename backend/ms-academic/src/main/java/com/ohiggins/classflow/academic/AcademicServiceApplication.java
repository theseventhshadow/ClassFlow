package com.ohiggins.classflow.academic;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio academico de ClassFlow.
 */
@SpringBootApplication
public class AcademicServiceApplication {

	/**
	 * Arranca la aplicacion del servicio academico.
	 *
	 * @param args argumentos de linea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(AcademicServiceApplication.class, args);
	}

}
