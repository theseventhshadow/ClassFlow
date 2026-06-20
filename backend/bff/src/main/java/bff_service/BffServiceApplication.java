package com.ohiggins.classflow.bff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BffServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BffServiceApplication.class, args);
        System.out.println("=== BFF SERVICE ===");
        System.out.println("✅ Puerto: 8086");
        System.out.println("📋 Agrega un dashboard agregado sobre auth, academic, assistance, message y notification");
        System.out.println("===================");
    }
}
