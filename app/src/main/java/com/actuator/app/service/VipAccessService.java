package com.actuator.app.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class VipAccessService {

    private final Counter unauthorizedVipAccessCounter;

    // 1. Inyección de MeterRegistry en el constructor
    public VipAccessService(MeterRegistry registry) {
        // Inicializa el contador con un nombre único y etiquetas descriptivas
        this.unauthorizedVipAccessCounter = Counter.builder("access.vip.unauthorized.total")
            .description("Cuenta intentos fallidos de acceso a recursos VIP")
            .tag("user_type", "unauthorized")
            .register(registry);
    }

    public boolean tryVipAccess(String userRole) {
        if (!"ADMIN".equalsIgnoreCase(userRole)) {
            // Incremento de la Métrica Personalizada al fallar
            unauthorizedVipAccessCounter.increment();
            return false;
        }
        return true;
    }
}
