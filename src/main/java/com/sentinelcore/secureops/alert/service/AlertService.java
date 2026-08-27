package com.sentinelcore.secureops.alert.service;

import com.sentinelcore.secureops.alert.model.Alert;
import com.sentinelcore.secureops.alert.repository.AlertRepository;
import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.config.MonitoringThresholds;
import com.sentinelcore.secureops.monitoring.service.HealthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Milestone 1 — Alert Management.
 * Generates infrastructure alerts from telemetry breaches and manages the
 * OPEN -> ACKNOWLEDGED -> RESOLVED lifecycle.
 */
@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final MonitoringThresholds thresholds;

    public AlertService(AlertRepository alertRepository, MonitoringThresholds thresholds) {
        this.alertRepository = alertRepository;
        this.thresholds = thresholds;
    }

    /** Called after every telemetry update (real or simulated) for an asset. */
    public void evaluateAndGenerateAlerts(Asset asset, HealthService healthService) {
        evaluateMetric(asset, "HIGH_CPU", asset.getCpuUsage(), thresholds.getCpuWarning(),
                thresholds.getCpuCritical(), "CPU utilization");
        evaluateMetric(asset, "HIGH_MEMORY", asset.getMemoryUsage(), thresholds.getMemoryWarning(),
                thresholds.getMemoryCritical(), "Memory utilization");
        evaluateMetric(asset, "HIGH_DISK_USAGE", asset.getDiskUsage(), thresholds.getDiskWarning(),
                thresholds.getDiskCritical(), "Disk utilization");

        if (healthService.isOffline(asset)) {
            openIfAbsent(asset, "ASSET_OFFLINE", "CRITICAL",
                    asset.getAssetName() + " has not sent a heartbeat within the configured window.",
                    null, null);
        } else {
            autoResolveIfPresent(asset, "ASSET_OFFLINE");
        }
    }

    private void evaluateMetric(Asset asset, String type, int value, int warn, int crit, String label) {
        if (value >= crit) {
            openIfAbsent(asset, type, "CRITICAL",
                    label + " is critically high: " + value + "% (threshold " + crit + "%)",
                    (double) value, (double) crit);
        } else if (value >= warn) {
            openIfAbsent(asset, type, "MEDIUM",
                    label + " is elevated: " + value + "% (threshold " + warn + "%)",
                    (double) value, (double) warn);
        } else {
            autoResolveIfPresent(asset, type);
        }
    }

    private void openIfAbsent(Asset asset, String type, String severity, String description,
                               Double currentValue, Double threshold) {
        Optional<Alert> existing = alertRepository.findFirstByAsset_IdAndAlertTypeAndStatus(
                asset.getId(), type, "OPEN");
        if (existing.isPresent()) {
            return; // already open — don't spam duplicates
        }
        Optional<Alert> acked = alertRepository.findFirstByAsset_IdAndAlertTypeAndStatus(
                asset.getId(), type, "ACKNOWLEDGED");
        if (acked.isPresent()) {
            return; // condition hasn't cleared, don't reopen behind the operator's back
        }
        Alert alert = new Alert(asset, type, severity, description, currentValue, threshold);
        alertRepository.save(alert);
    }

    /** When a metric drops back under threshold, auto-resolve the alert it caused (documented demo behavior). */
    private void autoResolveIfPresent(Asset asset, String type) {
        alertRepository.findFirstByAsset_IdAndAlertTypeAndStatus(asset.getId(), type, "OPEN")
                .ifPresent(a -> resolveInternal(a, "system-auto-resolve"));
    }

    public List<Alert> getAll() {
        return alertRepository.findAll();
    }

    public List<Alert> getByStatus(String status) {
        return alertRepository.findByStatus(status);
    }

    public List<Alert> getByAsset(Long assetId) {
        return alertRepository.findByAsset_Id(assetId);
    }

    public long countByStatus(String status) {
        return alertRepository.countByStatus(status);
    }

    public Alert acknowledge(Long id, String username) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        if (!"OPEN".equals(alert.getStatus())) {
            throw new RuntimeException("Only OPEN alerts can be acknowledged.");
        }
        alert.setStatus("ACKNOWLEDGED");
        alert.setAcknowledgedAt(LocalDateTime.now());
        alert.setAcknowledgedBy(username);
        return alertRepository.save(alert);
    }

    public Alert resolve(Long id, String username) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found"));
        if ("RESOLVED".equals(alert.getStatus())) {
            throw new RuntimeException("Alert is already resolved.");
        }
        return resolveInternal(alert, username);
    }

    private Alert resolveInternal(Alert alert, String username) {
        alert.setStatus("RESOLVED");
        alert.setResolvedAt(LocalDateTime.now());
        alert.setResolvedBy(username);
        return alertRepository.save(alert);
    }
}
