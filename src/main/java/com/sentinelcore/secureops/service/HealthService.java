package com.sentinelcore.secureops.monitoring.service;

import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.config.MonitoringThresholds;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Milestone 1 — Health Monitoring.
 * Rules (worst-of wins):
 *   No heartbeat within window  -> OFFLINE
 *   Any metric >= *Critical     -> Critical
 *   Any metric >= *Warning      -> Warning
 *   otherwise                   -> Healthy
 */
@Service
public class HealthService {

    public static final String HEALTHY = "Healthy";
    public static final String WARNING = "Warning";
    public static final String CRITICAL = "Critical";
    public static final String OFFLINE = "Offline";

    private final MonitoringThresholds thresholds;

    public HealthService(MonitoringThresholds thresholds) {
        this.thresholds = thresholds;
    }

    public String calculateStatus(Asset asset) {
        if (isOffline(asset)) {
            return OFFLINE;
        }

        boolean critical =
            asset.getCpuUsage() >= thresholds.getCpuCritical() ||
            asset.getMemoryUsage() >= thresholds.getMemoryCritical() ||
            asset.getDiskUsage() >= thresholds.getDiskCritical();

        if (critical) return CRITICAL;

        boolean warning =
            asset.getCpuUsage() >= thresholds.getCpuWarning() ||
            asset.getMemoryUsage() >= thresholds.getMemoryWarning() ||
            asset.getDiskUsage() >= thresholds.getDiskWarning();

        if (warning) return WARNING;

        return HEALTHY;
    }

    public boolean isOffline(Asset asset) {
        if (asset.getLastHeartbeat() == null) {
            return asset.getCreatedAt() != null &&
                   asset.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(thresholds.getHeartbeatOfflineMinutes()));
        }
        long minutesSince = ChronoUnit.MINUTES.between(asset.getLastHeartbeat(), LocalDateTime.now());
        return minutesSince >= thresholds.getHeartbeatOfflineMinutes();
    }

    public MonitoringThresholds getThresholds() {
        return thresholds;
    }
}
