package com.sentinelcore.secureops.monitoring.dto;

import java.time.LocalDateTime;

public class AssetHealthDTO {

    private Long assetId;
    private String assetName;
    private String status;       // Healthy, Warning, Critical, Offline
    private boolean offline;
    private LocalDateTime lastHeartbeat;
    private int cpuUsage;
    private int memoryUsage;
    private int diskUsage;
    private int networkUsage;
    private double uptime;

    public AssetHealthDTO() {}

    public AssetHealthDTO(Long assetId, String assetName, String status, boolean offline,
                           LocalDateTime lastHeartbeat, int cpuUsage, int memoryUsage,
                           int diskUsage, int networkUsage, double uptime) {
        this.assetId = assetId;
        this.assetName = assetName;
        this.status = status;
        this.offline = offline;
        this.lastHeartbeat = lastHeartbeat;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.networkUsage = networkUsage;
        this.uptime = uptime;
    }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public boolean isOffline() { return offline; }
    public void setOffline(boolean offline) { this.offline = offline; }

    public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
    public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

    public int getCpuUsage() { return cpuUsage; }
    public void setCpuUsage(int cpuUsage) { this.cpuUsage = cpuUsage; }

    public int getMemoryUsage() { return memoryUsage; }
    public void setMemoryUsage(int memoryUsage) { this.memoryUsage = memoryUsage; }

    public int getDiskUsage() { return diskUsage; }
    public void setDiskUsage(int diskUsage) { this.diskUsage = diskUsage; }

    public int getNetworkUsage() { return networkUsage; }
    public void setNetworkUsage(int networkUsage) { this.networkUsage = networkUsage; }

    public double getUptime() { return uptime; }
    public void setUptime(double uptime) { this.uptime = uptime; }
}
