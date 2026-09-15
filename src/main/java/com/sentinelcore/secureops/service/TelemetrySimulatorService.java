package com.sentinelcore.secureops.monitoring.service;

import com.sentinelcore.secureops.alert.service.AlertService;
import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.asset.repository.AssetRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

/**
 * Milestone 1 — SIMULATED / DEMO telemetry layer.
 *
 * IMPORTANT: This class does NOT collect real infrastructure metrics. It exists
 * so Milestone 1 can be demonstrated without provisioning real cloud infrastructure
 * or monitoring agents. Every value it writes is randomly generated.
 *
 * Do not treat Asset.cpuUsage/memoryUsage/diskUsage/networkUsage as real telemetry
 * unless this class is disabled and a real collector calls AssetService.recordHeartbeat()
 * instead.
 *
 * Toggle: monitoring.simulation.enabled=false
 */
@Service
public class TelemetrySimulatorService {

    private final AssetRepository assetRepository;
    private final HealthService healthService;
    private final AlertService alertService;
    private final Random random = new Random();

    @Value("${monitoring.simulation.enabled:true}")
    private boolean simulationEnabled;

    public TelemetrySimulatorService(AssetRepository assetRepository,
                                      HealthService healthService,
                                      AlertService alertService) {
        this.assetRepository = assetRepository;
        this.healthService = healthService;
        this.alertService = alertService;
    }

    @Scheduled(fixedDelayString = "${monitoring.simulation.interval-ms:15000}")
    public void tick() {
        if (!simulationEnabled) return;

        List<Asset> assets = assetRepository.findByActiveTrue();
        for (Asset asset : assets) {
            simulateAsset(asset);
        }
    }

    private void simulateAsset(Asset asset) {
        // Random-walk each metric around its current value so charts look organic
        // rather than jumping wildly between ticks. [SIMULATED DATA]
        asset.setCpuUsage(walk(asset.getCpuUsage(), 15, 2, 98));
        asset.setMemoryUsage(walk(asset.getMemoryUsage(), 10, 5, 97));
        asset.setDiskUsage(walk(asset.getDiskUsage(), 3, 10, 99));
        asset.setNetworkUsage(walk(asset.getNetworkUsage(), 20, 1, 100));
        asset.setUptime(Math.min(100.0, 99.5 + random.nextDouble() * 0.5));
        asset.setLastHeartbeat(LocalDateTime.now());

        asset.setStatus(healthService.calculateStatus(asset));
        assetRepository.save(asset);

        alertService.evaluateAndGenerateAlerts(asset, healthService);
    }

    private int walk(int current, int maxSwing, int min, int max) {
        int delta = random.nextInt(maxSwing * 2 + 1) - maxSwing;
        int next = current + delta;
        if (next < min) next = min;
        if (next > max) next = max;
        return next;
    }

    public boolean isSimulationEnabled() {
        return simulationEnabled;
    }
}
