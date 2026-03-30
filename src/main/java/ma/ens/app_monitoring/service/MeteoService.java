package ma.ens.app_monitoring.service;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Service simulant la récupération de données météo.
 * Inclut des métriques Micrometer personnalisées :
 *   - Compteur de requêtes météo réussies
 *   - Timer pour mesurer la durée du traitement
 *   - Compteur d'erreurs
 */
@Slf4j
@Service
public class MeteoService {

    private final Counter requetesReussies;
    private final Counter requetesEchouees;
    private final Timer tempsTraitement;
    private final Random random = new Random();

    // Données météo simulées pour différentes villes
    private static final Map<String, List<String>> METEO_DATA = Map.of(
            "casablanca", List.of("Ensoleillé 28°C", "Nuageux 22°C", "Pluvieux 18°C"),
            "rabat",      List.of("Partiellement nuageux 25°C", "Ensoleillé 30°C"),
            "marrakech",  List.of("Très chaud 38°C", "Chaud 35°C", "Ensoleillé 33°C"),
            "agadir",     List.of("Brumeux 20°C", "Ensoleillé 26°C")
    );

    /**
     * Injection du MeterRegistry pour enregistrer les métriques personnalisées.
     */
    public MeteoService(MeterRegistry registry) {
        // Compteur : nombre de requêtes météo réussies
        this.requetesReussies = Counter.builder("meteo.requetes.reussies")
                .description("Nombre total de requêtes météo réussies")
                .tag("service", "meteo")
                .register(registry);

        // Compteur : nombre de requêtes ayant échoué
        this.requetesEchouees = Counter.builder("meteo.requetes.echouees")
                .description("Nombre total d'erreurs lors du traitement météo")
                .tag("service", "meteo")
                .register(registry);

        // Timer : durée de traitement de chaque requête
        this.tempsTraitement = Timer.builder("meteo.traitement.duree")
                .description("Temps de traitement d'une requête météo")
                .tag("service", "meteo")
                .register(registry);
    }

    /**
     * Récupère les données météo pour une ville donnée.
     * Simule un délai réseau aléatoire (100–600ms).
     *
     * @param ville la ville ciblée (ex: casablanca, rabat...)
     * @return une chaîne décrivant la météo actuelle
     */
    public String getMeteo(String ville) {
        // On mesure le temps d'exécution avec le Timer
        return tempsTraitement.record(() -> {
            log.info("📡 Récupération météo pour la ville : {}", ville);

            // Simulation d'un délai réseau réaliste
            simulerDelaiReseau();

            String villeNorm = ville.toLowerCase().trim();

            if (!METEO_DATA.containsKey(villeNorm)) {
                requetesEchouees.increment();
                log.warn(" Ville inconnue : {}", ville);
                throw new IllegalArgumentException("Ville non trouvée : " + ville);
            }

            List<String> conditions = METEO_DATA.get(villeNorm);
            String resultat = conditions.get(random.nextInt(conditions.size()));

            requetesReussies.increment();
            log.info(" Météo retournée pour {} : {}", ville, resultat);
            return resultat;
        });
    }

    /**
     * Retourne la liste de toutes les villes disponibles.
     */
    public List<String> getVillesDisponibles() {
        log.debug("Listage des villes disponibles");
        return METEO_DATA.keySet().stream().sorted().toList();
    }

    /**
     * Simule un délai réseau aléatoire entre 100ms et 600ms.
     */
    private void simulerDelaiReseau() {
        try {
            long delai = 100 + random.nextInt(500);
            log.debug(" Simulation délai réseau : {}ms", delai);
            Thread.sleep(delai);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Thread interrompu pendant la simulation", e);
        }
    }
}