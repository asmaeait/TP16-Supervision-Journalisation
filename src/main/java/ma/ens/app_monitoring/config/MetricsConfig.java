package ma.ens.app_monitoring.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration des métriques Micrometer supplémentaires.
 * Active la collecte des métriques JVM (GC, mémoire) et CPU.
 * Ces données seront disponibles dans Prometheus et visibles dans Grafana.
 */
@Configuration
public class MetricsConfig {

    /**
     * Collecte des métriques de Garbage Collection JVM.
     * Ex : jvm_gc_pause_seconds_*
     */

    /*
    @Bean
    public JvmGcMetrics jvmGcMetrics() {
        return new JvmGcMetrics();
    }

     */

    /**
     * Collecte des métriques mémoire JVM.
     * Ex : jvm_memory_used_bytes, jvm_memory_max_bytes
     */
    /*
    @Bean
    public JvmMemoryMetrics jvmMemoryMetrics() {
        return new JvmMemoryMetrics();
    }

     */

    /**
     * Collecte des métriques CPU du système.
     * Ex : system_cpu_usage, process_cpu_usage
     */
   /*
    @Bean
    public ProcessorMetrics processorMetrics() {
        return new ProcessorMetrics();
    }

    */

}