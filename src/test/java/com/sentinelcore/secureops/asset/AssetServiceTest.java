package com.sentinelcore.secureops.asset;

import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.asset.repository.AssetRepository;
import com.sentinelcore.secureops.asset.service.AssetService;
import com.sentinelcore.secureops.config.MonitoringThresholds;
import com.sentinelcore.secureops.monitoring.service.HealthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssetServiceTest {

    @Mock
    private AssetRepository assetRepository;

    private AssetService assetService;

    @BeforeEach
    void setUp() {
        assetService = new AssetService(assetRepository, new HealthService(new MonitoringThresholds()));
    }

    @Test
    void createInitializesHeartbeatAndHealthStatus() {
        Asset asset = new Asset();
        asset.setAssetName("WEB-01");
        asset.setIpAddress("10.0.0.10");
        asset.setCpuUsage(20);
        asset.setMemoryUsage(30);
        asset.setDiskUsage(40);
        when(assetRepository.existsByIpAddress("10.0.0.10")).thenReturn(false);
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

        Asset saved = assetService.create(asset);

        assertNotNull(saved.getLastHeartbeat());
        assertEquals(HealthService.HEALTHY, saved.getStatus());
        verify(assetRepository, times(2)).save(asset);
    }

    @Test
    void createRejectsDuplicateIpAddress() {
        Asset asset = new Asset();
        asset.setIpAddress("10.0.0.10");
        when(assetRepository.existsByIpAddress("10.0.0.10")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> assetService.create(asset));
        verify(assetRepository, never()).save(any());
    }

    @Test
    void recordHeartbeatUpdatesMetricsAndStatus() {
        Asset asset = new Asset();
        asset.setId(1L);
        asset.setLastHeartbeat(LocalDateTime.now());
        when(assetRepository.findById(1L)).thenReturn(Optional.of(asset));
        when(assetRepository.save(any(Asset.class))).thenAnswer(inv -> inv.getArgument(0));

        Asset updated = assetService.recordHeartbeat(1L, 95, 30, 40, 10);

        assertEquals(95, updated.getCpuUsage());
        assertEquals(HealthService.CRITICAL, updated.getStatus());
        assertNotNull(updated.getLastHeartbeat());
    }
}
