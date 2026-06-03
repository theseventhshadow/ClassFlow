package gateway_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServiceApplication.class, args);
        System.out.println("=== API GATEWAY ===");
        System.out.println("✅ Puerto: 8080");
        System.out.println("   → /api/bff/*         → BFF (8086)");
        System.out.println("📋 Enrutando a los microservicios y al BFF:");
        System.out.println("   → /api/auth/*         → Auth (8081)");
        System.out.println("   → /api/courses/*      → Academic (8082)");
        System.out.println("   → /api/subjects/*     → Academic (8082)");
        System.out.println("   → /api/evaluations/*  → Academic (8082)");
        System.out.println("   → /api/grades/*       → Academic (8082)");
        System.out.println("   → /api/attendance/*   → Asistencia (8083)");
        System.out.println("   → /api/annotations/*  → Asistencia (8083)");
        System.out.println("   → /api/messages/*     → Mensajeria (8084)");
        System.out.println("   → /api/announcements/*→ Mensajeria (8084)");
        System.out.println("   → /api/notifications/*→ Notificaciones (8085)");
        System.out.println("================================");
    }
}