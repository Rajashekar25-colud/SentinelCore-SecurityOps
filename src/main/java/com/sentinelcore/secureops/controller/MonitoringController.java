package com.sentinelcore.secureops.monitoring.controller;

import com.sentinelcore.secureops.alert.service.AlertService;
import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.asset.repository.AssetRepository;
import com.sentinelcore.secureops.monitoring.dto.AssetHealthDTO;
import com.sentinelcore.secureops.monitoring.dto.AssetMetricsDTO;
import com.sentinelcore.secureops.monitoring.dto.MonitoringSummaryDTO;
import com.sentinelcore.secureops.monitoring.service.HealthService;
import com.sentinelcore.secureops.monitoring.service.TelemetrySimulatorService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Milestone 1 — Infrastructure Monitoring aggregate endpoints.
 * Owns cross-asset summary + per-asset health/metrics derivation.
 * CRUD for the Asset resource itself stays in AssetController.
 */
@RestController
@CrossOrigin(origins = "*")
public class MonitoringController {

    private final AssetRepository assetRepository;
    private final HealthService healthService;
    private final AlertService alertService;
    private final TelemetrySimulatorService telemetrySimulatorService;

    public MonitoringController(AssetRepository assetRepository,
                                 HealthService healthService,
                                 AlertService alertService,
                                 TelemetrySimulatorService telemetrySimulatorService) {
        this.assetRepository = assetRepository;
        this.healthService = healthService;
        this.alertService = alertService;
        this.telemetrySimulatorService = telemetrySimulatorService;
    }

    @GetMapping("/api/monitoring/summary")
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public MonitoringSummaryDTO getSummary() {
        List<Asset> assets = assetRepository.findByActiveTrue();

        long healthy = assets.stream().filter(a -> HealthService.HEALTHY.equals(a.getStatus())).count();
        long warning = assets.stream().filter(a -> HealthService.WARNING.equals(a.getStatus())).count();
        long critical = assets.stream().filter(a -> HealthService.CRITICAL.equals(a.getStatus())).count();
        long offline = assets.stream().filter(a -> HealthService.OFFLINE.equals(a.getStatus())).count();

        double avgCpu = assets.stream().mapToInt(Asset::getCpuUsage).average().orElse(0);
        double avgMem = assets.stream().mapToInt(Asset::getMemoryUsage).average().orElse(0);
        double avgDisk = assets.stream().mapToInt(Asset::getDiskUsage).average().orElse(0);
        double avgUptime = assets.stream().mapToDouble(Asset::getUptime).average().orElse(0);

        long activeAlerts = alertService.countByStatus("OPEN") + alertService.countByStatus("ACKNOWLEDGED");

        return new MonitoringSummaryDTO(
                assets.size(), healthy, warning, critical, offline, activeAlerts,
                round(avgCpu), round(avgMem), round(avgDisk), round(avgUptime),
                telemetrySimulatorService.isSimulationEnabled()
        );
    }

    /** Fleet-wide metrics list — used by the dashboard's overview chart. */
    @GetMapping("/api/monitoring/metrics")
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public List<AssetMetricsDTO> getAllMetrics() {
        return assetRepository.findByActiveTrue().stream()
                .map(a -> toMetricsDTO(a))
                .toList();
    }

    @GetMapping("/api/assets/{id}/health")
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public AssetHealthDTO getAssetHealth(@PathVariable Long id) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        return new AssetHealthDTO(
                asset.getId(), asset.getAssetName(), asset.getStatus(),
                healthService.isOffline(asset), asset.getLastHeartbeat(),
                asset.getCpuUsage(), asset.getMemoryUsage(), asset.getDiskUsage(),
                asset.getNetworkUsage(), asset.getUptime()
        );
    }

    @GetMapping("/api/assets/{id}/metrics")
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public AssetMetricsDTO getAssetMetrics(@PathVariable Long id) {
        Asset asset = assetRepository.findById(id).orElseThrow(() -> new RuntimeException("Asset not found"));
        return toMetricsDTO(asset);
    }

    @GetMapping("/api/assets/{id}/alerts")
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public Object getAssetAlerts(@PathVariable Long id) {
        return alertService.getByAsset(id);
    }

    private AssetMetricsDTO toMetricsDTO(Asset a) {
        return new AssetMetricsDTO(
                a.getId(), a.getCpuUsage(), a.getMemoryUsage(), a.getDiskUsage(),
                a.getNetworkUsage(), a.getUptime(), LocalDateTime.now(), true
        );
    }

    private double round(double v) {
        return Math.round(v * 10.0) / 10.0;
    }
}
