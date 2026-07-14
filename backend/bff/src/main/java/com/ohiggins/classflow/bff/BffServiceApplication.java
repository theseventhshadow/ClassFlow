package com.ohiggins.classflow.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del servicio BFF de ClassFlow.
 */
@SpringBootApplication
public class BffServiceApplication {

    /**
     * Arranca la aplicacion del BFF.
     *
     * @param args argumentos de linea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(BffServiceApplication.class, args);
        System.out.println("=== BFF SERVICE ===");
        System.out.println("✅ Puerto: 8086");
        System.out.println("📋 Agrega un dashboard agregado sobre auth, academic, assistance, message y notification");
        System.out.println("===================");
    }
}
