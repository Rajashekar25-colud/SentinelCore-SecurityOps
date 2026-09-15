package com.sentinelcore.secureops.asset.service;

import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.asset.repository.AssetRepository;
import com.sentinelcore.secureops.monitoring.service.HealthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AssetService {

    private final AssetRepository assetRepository;
    private final HealthService healthService;

    public AssetService(AssetRepository assetRepository, HealthService healthService) {
        this.assetRepository = assetRepository;
        this.healthService = healthService;
    }

    public List<Asset> getAll() {
        return assetRepository.findAll();
    }

    public Optional<Asset> getById(Long id) {
        return assetRepository.findById(id);
    }

    public Asset create(Asset asset) {
        if (assetRepository.existsByIpAddress(asset.getIpAddress())) {
            throw new RuntimeException("IP Address already assigned to another asset.");
        }
        if (asset.getStatus() == null || asset.getStatus().isBlank()) {
            asset.setStatus(HealthService.HEALTHY);
        }
        if (asset.getLastHeartbeat() == null) {
            asset.setLastHeartbeat(LocalDateTime.now());
        }
        Asset saved = assetRepository.save(asset);
        saved.setStatus(healthService.calculateStatus(saved));
        return assetRepository.save(saved);
    }

    public Asset update(Long id, Asset updated) {
        return assetRepository.findById(id)
            .map(asset -> {
                if (assetRepository.existsByIpAddressAndIdNot(updated.getIpAddress(), id)) {
                    throw new RuntimeException("IP Address already assigned to another asset.");
                }
                asset.setAssetName(updated.getAssetName());
                asset.setIpAddress(updated.getIpAddress());
                asset.setAssetType(updated.getAssetType());
                asset.setHostname(updated.getHostname());
                asset.setOperatingSystem(updated.getOperatingSystem());
                asset.setEnvironment(updated.getEnvironment());
                asset.setOwnerTeam(updated.getOwnerTeam());
                asset.setCpuUsage(updated.getCpuUsage());
                asset.setMemoryUsage(updated.getMemoryUsage());
                asset.setDiskUsage(updated.getDiskUsage());
                asset.setNetworkUsage(updated.getNetworkUsage());
                asset.setUptime(updated.getUptime());
                asset.setLocation(updated.getLocation());

                asset.setStatus(healthService.calculateStatus(asset));
                return assetRepository.save(asset);
            })
            .orElseThrow(() -> new RuntimeException("Asset not found"));
    }

    /** Soft delete — deactivates rather than hard-deleting so alert/audit history stays intact. */
    public Asset deactivate(Long id) {
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setActive(false);
        asset.setStatus(HealthService.OFFLINE);
        return assetRepository.save(asset);
    }

    public void hardDelete(Long id) {
        assetRepository.deleteById(id);
    }

    public Asset recordHeartbeat(Long id, int cpu, int memory, int disk, int network) {
        Asset asset = assetRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Asset not found"));
        asset.setCpuUsage(cpu);
        asset.setMemoryUsage(memory);
        asset.setDiskUsage(disk);
        asset.setNetworkUsage(network);
        asset.setLastHeartbeat(LocalDateTime.now());
        asset.setStatus(healthService.calculateStatus(asset));
        return assetRepository.save(asset);
    }

    public List<Asset> searchByName(String keyword) {
        return assetRepository.findByAssetNameContainingIgnoreCase(keyword);
    }

    public List<Asset> filterByStatus(String status) {
        return assetRepository.findByStatus(status);
    }

    public List<Asset> filterByType(String type) {
        return assetRepository.findByAssetType(type);
    }

    public List<Asset> filterByEnvironment(String environment) {
        return assetRepository.findByEnvironment(environment);
    }

    public List<Asset> getActive() {
        return assetRepository.findByActiveTrue();
    }
}
