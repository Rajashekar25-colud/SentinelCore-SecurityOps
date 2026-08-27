package com.sentinelcore.secureops.alert;

import com.sentinelcore.secureops.alert.model.Alert;
import com.sentinelcore.secureops.alert.repository.AlertRepository;
import com.sentinelcore.secureops.alert.service.AlertService;
import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.config.MonitoringThresholds;
import com.sentinelcore.secureops.monitoring.service.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    private AlertService alertService;
    private HealthService healthService;

    @BeforeEach
    void setUp() {
        MonitoringThresholds thresholds = new MonitoringThresholds();
        healthService = new HealthService(thresholds);
        alertService = new AlertService(alertRepository, thresholds);
    }

    @Test
    void criticalCpuCreatesAlert() {
        Asset asset = asset(1L, 95, 30, 40);
        when(alertRepository.findFirstByAsset_IdAndAlertTypeAndStatus(1L, "HIGH_CPU", "OPEN"))
                .thenReturn(Optional.empty());
        when(alertRepository.findFirstByAsset_IdAndAlertTypeAndStatus(1L, "HIGH_CPU", "ACKNOWLEDGED"))
                .thenReturn(Optional.empty());

        alertService.evaluateAndGenerateAlerts(asset, healthService);

        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void openAlertIsNotDuplicated() {
        Asset asset = asset(1L, 95, 30, 40);
        Alert existing = new Alert();
        existing.setStatus("OPEN");
        when(alertRepository.findFirstByAsset_IdAndAlertTypeAndStatus(1L, "HIGH_CPU", "OPEN"))
                .thenReturn(Optional.of(existing));

        alertService.evaluateAndGenerateAlerts(asset, healthService);

        verify(alertRepository, never()).save(any(Alert.class));
    }

    @Test
    void acknowledgeMovesOpenAlertToAcknowledged() {
        Alert alert = new Alert();
        alert.setId(10L);
        alert.setStatus("OPEN");
        when(alertRepository.findById(10L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        Alert result = alertService.acknowledge(10L, "operator");

        assertEquals("ACKNOWLEDGED", result.getStatus());
        assertEquals("operator", result.getAcknowledgedBy());
        assertNotNull(result.getAcknowledgedAt());
    }

    private Asset asset(Long id, int cpu, int memory, int disk) {
        Asset asset = new Asset();
        asset.setId(id);
        asset.setAssetName("TEST-ASSET");
        asset.setCpuUsage(cpu);
        asset.setMemoryUsage(memory);
        asset.setDiskUsage(disk);
        asset.setNetworkUsage(10);
        asset.setLastHeartbeat(LocalDateTime.now());
        return asset;
    }
}
