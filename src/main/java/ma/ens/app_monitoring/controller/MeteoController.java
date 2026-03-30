package ma.ens.app_monitoring.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ma.ens.app_monitoring.service.MeteoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST exposant les endpoints de l'API Météo.
 * Ces endpoints seront suivis automatiquement par Actuator et Prometheus
 * (métriques http_server_requests_seconds_*).
 */
@Slf4j
@RestController
@RequestMapping("/api/meteo")
@RequiredArgsConstructor
public class MeteoController {

    private final MeteoService meteoService;

    /**
     * GET /api/meteo/{ville}
     * Retourne la météo actuelle pour une ville.
     *
     * Test : curl http://localhost:8080/api/meteo/casablanca
     */
    @GetMapping("/{ville}")
    public ResponseEntity<Map<String, Object>> getMeteo(@PathVariable String ville) {
        log.info(" Requête météo reçue pour : {}", ville);

        String meteo = meteoService.getMeteo(ville);

        Map<String, Object> reponse = Map.of(
                "ville", ville,
                "meteo", meteo,
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "source", "MeteoService v1.0"
        );

        return ResponseEntity.ok(reponse);
    }

    /**
     * GET /api/meteo/villes
     * Liste toutes les villes disponibles.
     *
     * Test : curl http://localhost:8080/api/meteo/villes
     */
    @GetMapping("/villes")
    public ResponseEntity<Map<String, Object>> getVilles() {
        log.info(" Requête liste des villes");

        List<String> villes = meteoService.getVillesDisponibles();

        return ResponseEntity.ok(Map.of(
                "villes", villes,
                "total", villes.size()
        ));
    }

    /**
     * GET /api/meteo/status
     * Endpoint de statut personnalisé (distinct de /actuator/health).
     *
     * Test : curl http://localhost:8080/api/meteo/status
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        log.debug("Vérification du statut applicatif");
        return ResponseEntity.ok(Map.of(
                "statut", "OPERATIONNEL",
                "version", "1.0.0",
                "heure", LocalDateTime.now().toString()
        ));
    }

    /**
     * Gestion des erreurs liées aux villes inconnues.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleVilleInconnue(IllegalArgumentException ex) {
        log.error(" Ville non trouvée : {}", ex.getMessage());
        return ResponseEntity.badRequest().body(Map.of(
                "erreur", ex.getMessage(),
                "conseil", "Utilisez GET /api/meteo/villes pour voir les villes disponibles"
        ));
    }
}