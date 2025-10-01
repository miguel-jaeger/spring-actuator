package com.actuator.app.controlador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);

    @GetMapping("/api/stock/{id}")
    public String getStock(@PathVariable String id) throws InterruptedException {
        long startTime = System.currentTimeMillis();

        // Simulación de latencia
        Thread.sleep(100);

        long latency = System.currentTimeMillis() - startTime;

        // Registro de INFO Estructurado (Clave=Valor)
        log.info("event=StockCheckSuccess, item_id={}, status=200, latency_ms={}", id, latency);

        return "Stock check successful for " + id + ". Latency: " + latency + "ms";
    }

    @GetMapping("/api/stock/critical/{id}")
    public String criticalCheck(@PathVariable String id) {
        if (id.startsWith("CRIT")) {
            // Simulación de fallo solo para IDs críticos
            try {
                // Generar un error simulado
                throw new java.sql.SQLException("Timeout occurred while connecting to primary DB.");
            } catch (Exception e) {
                // Registro de ERROR (es vital pasar 'e' para obtener el stack trace)
                log.error("event=CriticalDBFailure, item_id={}, code=500, msg='Fallo de conexión crítico'", id, e);
                
                // Lanza una excepción para que Actuator registre el error 500
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR, "Service Unavailable", e);
            }
        }
        return "Critical Check Passed for " + id;
    }

}
