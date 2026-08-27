package com.sentinelcore.secureops.monitoring;

import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.config.MonitoringThresholds;
import com.sentinelcore.secureops.monitoring.service.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class HealthServiceTest {

    private HealthService healthService;

    @BeforeEach
    void setUp() {
        MonitoringThresholds thresholds = new MonitoringThresholds();
        // defaults: cpuWarning=70, cpuCritical=90, memWarning=75, memCritical=90,
        //           diskWarning=80, diskCritical=95, heartbeatOfflineMinutes=5
        healthService = new HealthService(thresholds);
    }

    private Asset assetWith(int cpu, int mem, int disk, LocalDateTime heartbeat) {
        Asset a = new Asset();
        a.setCpuUsage(cpu);
        a.setMemoryUsage(mem);
        a.setDiskUsage(disk);
        a.setNetworkUsage(10);
        a.setLastHeartbeat(heartbeat);
        return a;
    }

    @Test
    void healthyWhenAllMetricsBelowWarningThreshold() {
        Asset asset = assetWith(20, 30, 40, LocalDateTime.now());
        assertEquals(HealthService.HEALTHY, healthService.calculateStatus(asset));
    }

    @Test
    void warningWhenCpuAtWarningThreshold() {
        Asset asset = assetWith(70, 30, 40, LocalDateTime.now());
        assertEquals(HealthService.WARNING, healthService.calculateStatus(asset));
    }

    @Test
    void criticalWhenMemoryAtCriticalThreshold() {
        Asset asset = assetWith(20, 90, 40, LocalDateTime.now());
        assertEquals(HealthService.CRITICAL, healthService.calculateStatus(asset));
    }

    @Test
    void criticalTakesPriorityOverWarning() {
        // CPU is warning-level, disk is critical-level -> overall must be Critical
        Asset asset = assetWith(75, 30, 96, LocalDateTime.now());
        assertEquals(HealthService.CRITICAL, healthService.calculateStatus(asset));
    }

    @Test
    void offlineWhenNoHeartbeatWithinWindow() {
        Asset asset = assetWith(20, 30, 40, LocalDateTime.now().minusMinutes(10));
        assertEquals(HealthService.OFFLINE, healthService.calculateStatus(asset));
    }

    @Test
    void offlineOverridesEvenHealthyMetrics() {
        Asset asset = assetWith(5, 5, 5, LocalDateTime.now().minusMinutes(30));
        assertEquals(HealthService.OFFLINE, healthService.calculateStatus(asset));
        assertTrue(healthService.isOffline(asset));
    }

    @Test
    void notOfflineWhenHeartbeatIsRecent() {
        Asset asset = assetWith(20, 20, 20, LocalDateTime.now().minusMinutes(1));
        assertFalse(healthService.isOffline(asset));
    }
}