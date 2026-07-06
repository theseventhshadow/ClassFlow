package com.ohiggins.classflow.notification;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio de notificaciones de ClassFlow.
 */
@SpringBootApplication
public class NotificationServiceApplication {

	/**
	 * Arranca la aplicacion del servicio de notificaciones.
	 *
	 * @param args argumentos de linea de comandos.
	 */
	public static void main(String[] args) {
		SpringApplication.run(NotificationServiceApplication.class, args);
	}

}
