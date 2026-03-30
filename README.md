#  App Monitoring — Spring Boot + Prometheus + Grafana

Application de démonstration d'une chaîne complète d'observabilité avec Spring Boot.

##  Architecture
```
[Spring Boot App] ──scrape──▶ [Prometheus] ──datasource──▶ [Grafana]
      │                             │
  /actuator/prometheus          /targets
  /actuator/health              PromQL queries
```

##  Stack technique

| Outil | Rôle | Port |
|---|---|---|
| Spring Boot 3.2 | Application métier | 8080 |
| Spring Actuator | Endpoints de supervision | 8080/actuator |
| Micrometer | Collecte des métriques | — |
| Prometheus | Agrégation des métriques | 9090 |
| Grafana | Visualisation / Dashboards | 3000 |

##  Structure du projet
```
src/main/java/ma/ens/appmonitoring/
 ├── AppMonitoringApplication.java    # Point d'entrée
 ├── controller/MeteoController.java  # API REST Météo
 ├── service/MeteoService.java        # Logique métier + métriques custom
 └── config/MetricsConfig.java        # Configuration Micrometer
src/main/resources/
 └── application.properties           # Config Actuator, logs, Prometheus
prometheus.yml                        # Config scraping Prometheus
alerts.yml                            # Règles d'alertes
docker-compose.yml                    # Stack complète Docker
```

##  Lancement rapide

### Option 1 — Local (sans Docker)
```bash
# Lancer l'application
mvn spring-boot:run

# Lancer Prometheus (dans un autre terminal)
./prometheus --config.file=prometheus.yml

# Grafana accessible sur http://localhost:3000
```

### Option 2 — Docker Compose (recommandé)
```bash
# Builder et lancer toute la stack
mvn clean package -DskipTests
docker-compose up -d

# Vérifier l'état des services
docker-compose ps
```

##  Endpoints disponibles

### API Météo
| Méthode | URL | Description |
|---|---|---|
| GET | `/api/meteo/{ville}` | Météo d'une ville |
| GET | `/api/meteo/villes` | Liste des villes disponibles |
| GET | `/api/meteo/status` | Statut de l'application |

**Villes disponibles :** `casablanca`, `rabat`, `marrakech`, `agadir`
```bash
curl http://localhost:8080/api/meteo/casablanca
curl http://localhost:8080/api/meteo/villes
```

### Actuator (Supervision)
| URL | Description |
|---|---|
| `/actuator/health` | État de santé de l'application |
| `/actuator/info` | Infos de l'application |
| `/actuator/metrics` | Liste de toutes les métriques |
| `/actuator/prometheus` | Métriques au format Prometheus |

##  Métriques personnalisées

| Métrique | Type | Description |
|---|---|---|
| `meteo_requetes_reussies_total` | Counter | Nombre de requêtes réussies |
| `meteo_requetes_echouees_total` | Counter | Nombre d'erreurs |
| `meteo_traitement_duree_seconds` | Timer | Temps de traitement |

### Requêtes PromQL utiles
```promql
# Taux de requêtes par seconde
rate(http_server_requests_seconds_count[1m])

# Latence moyenne
rate(http_server_requests_seconds_sum[1m]) / rate(http_server_requests_seconds_count[1m])

# Mémoire heap utilisée (%)
jvm_memory_used_bytes{area="heap"} / jvm_memory_max_bytes{area="heap"} * 100

# Nos métriques personnalisées
meteo_requetes_reussies_total
```

##  Grafana

1. Ouvrir http://localhost:3000 (admin/admin123)
2. Ajouter Prometheus comme datasource : `http://prometheus:9090`
3. Importer le dashboard **ID 4701** (Spring Boot Statistics)
4. Ajouter des panels pour les métriques personnalisées

##  Logs

Les logs sont écrits dans `./logs/app-monitoring.log` avec rotation automatique (max 10MB, 7 fichiers).
```bash
# Suivre les logs en direct
tail -f logs/app-monitoring.log
```

##  Alertes configurées

| Alerte | Condition | Sévérité |
|---|---|---|
| `ApplicationIndisponible` | App DOWN > 10s | Critical |
| `LatenceElevee` | Latence moy > 1s pendant 30s | Warning |
| `MemoireHeapCritique` | Heap > 80% pendant 1min | Warning |

##  Références

- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/current/reference/html/actuator.html)
- [Micrometer](https://micrometer.io/docs)
- [Prometheus](https://prometheus.io/docs/)
- [Grafana](https://grafana.com/docs/)
- [Dashboard 4701](https://grafana.com/grafana/dashboards/4701)
