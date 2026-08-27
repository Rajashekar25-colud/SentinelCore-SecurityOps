package com.sentinelcore.secureops.monitoring.dto;

import java.time.LocalDateTime;

public class AssetMetricsDTO {

    private Long assetId;
    private int cpuUsage;
    private int memoryUsage;
    private int diskUsage;
    private int networkUsage;
    private double uptime;
    private LocalDateTime capturedAt;
    private boolean simulated; // always true until a real telemetry collector is wired in

    public AssetMetricsDTO() {}

    public AssetMetricsDTO(Long assetId, int cpuUsage, int memoryUsage, int diskUsage,
                            int networkUsage, double uptime, LocalDateTime capturedAt, boolean simulated) {
        this.assetId = assetId;
        this.cpuUsage = cpuUsage;
        this.memoryUsage = memoryUsage;
        this.diskUsage = diskUsage;
        this.networkUsage = networkUsage;
        this.uptime = uptime;
        this.capturedAt = capturedAt;
        this.simulated = simulated;
    }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

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

    public LocalDateTime getCapturedAt() { return capturedAt; }
    public void setCapturedAt(LocalDateTime capturedAt) { this.capturedAt = capturedAt; }

    public boolean isSimulated() { return simulated; }
    public void setSimulated(boolean simulated) { this.simulated = simulated; }
}
