package com.sentinelcore.secureops.monitoring.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Random;

/**
 * Global (non-asset-specific) live telemetry widget used by the Infrastructure
 * page's rolling charts.
 *
 * [SIMULATED DATA] — every value below is randomly generated for demo purposes.
 * This is intentionally kept separate from per-asset monitoring (MonitoringController),
 * which now drives real Asset.cpuUsage/memoryUsage/diskUsage fields via
 * TelemetrySimulatorService and is what alerts/health are calculated from.
 */
@RestController
@RequestMapping("/api/infrastructure")
public class InfrastructureController {

    private final Random random = new Random();

    @GetMapping("/telemetry")
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public Map<String, Object> getTelemetry() {
        double cpu = 30.0 + random.nextDouble() * 25.0;
        double mem = 62.0 + random.nextDouble() * 10.0;
        double net = 1.0 + random.nextDouble() * 1.5;
        int dbPool = 8 + random.nextInt(12);

        return Map.of(
            "cpuCount", String.format("%.1f%%", cpu),
            "memoryPoolInfo", String.format("%.1f%%", mem),
            "networkIoRate", String.format("%.2f GB/s", net),
            "activeInstances", "3 Active",
            "vaultHsmStatus", "OK",
            "dbConnections", dbPool + "/100",
            "simulated", true
        );
    }
}
