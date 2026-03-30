package ma.ens.app_monitoring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Point d'entrée de l'application de monitoring météo.
 * Démontre l'intégration Actuator + Prometheus + Grafana.
 */
@SpringBootApplication
public class AppMonitoringApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppMonitoringApplication.class, args);
	}
}