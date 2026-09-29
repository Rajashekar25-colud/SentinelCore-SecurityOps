package com.sentinelcore.secureops.monitoring.dto;

public class MonitoringSummaryDTO {

    private long totalAssets;
    private long healthyAssets;
    private long warningAssets;
    private long criticalAssets;
    private long offlineAssets;
    private long activeAlerts;
    private double avgCpuUsage;
    private double avgMemoryUsage;
    private double avgDiskUsage;
    private double avgUptime;
    private boolean simulatedData; // true while TelemetrySimulatorService is the data source

    public MonitoringSummaryDTO() {}

    public MonitoringSummaryDTO(long totalAssets, long healthyAssets, long warningAssets,
                                 long criticalAssets, long offlineAssets, long activeAlerts,
                                 double avgCpuUsage, double avgMemoryUsage, double avgDiskUsage,
                                 double avgUptime, boolean simulatedData) {
        this.totalAssets = totalAssets;
        this.healthyAssets = healthyAssets;
        this.warningAssets = warningAssets;
        this.criticalAssets = criticalAssets;
        this.offlineAssets = offlineAssets;
        this.activeAlerts = activeAlerts;
        this.avgCpuUsage = avgCpuUsage;
        this.avgMemoryUsage = avgMemoryUsage;
        this.avgDiskUsage = avgDiskUsage;
        this.avgUptime = avgUptime;
        this.simulatedData = simulatedData;
    }

    public long getTotalAssets() { return totalAssets; }
    public void setTotalAssets(long totalAssets) { this.totalAssets = totalAssets; }

    public long getHealthyAssets() { return healthyAssets; }
    public void setHealthyAssets(long healthyAssets) { this.healthyAssets = healthyAssets; }

    public long getWarningAssets() { return warningAssets; }
    public void setWarningAssets(long warningAssets) { this.warningAssets = warningAssets; }

    public long getCriticalAssets() { return criticalAssets; }
    public void setCriticalAssets(long criticalAssets) { this.criticalAssets = criticalAssets; }

    public long getOfflineAssets() { return offlineAssets; }
    public void setOfflineAssets(long offlineAssets) { this.offlineAssets = offlineAssets; }

    public long getActiveAlerts() { return activeAlerts; }
    public void setActiveAlerts(long activeAlerts) { this.activeAlerts = activeAlerts; }

    public double getAvgCpuUsage() { return avgCpuUsage; }
    public void setAvgCpuUsage(double avgCpuUsage) { this.avgCpuUsage = avgCpuUsage; }

    public double getAvgMemoryUsage() { return avgMemoryUsage; }
    public void setAvgMemoryUsage(double avgMemoryUsage) { this.avgMemoryUsage = avgMemoryUsage; }

    public double getAvgDiskUsage() { return avgDiskUsage; }
    public void setAvgDiskUsage(double avgDiskUsage) { this.avgDiskUsage = avgDiskUsage; }

    public double getAvgUptime() { return avgUptime; }
    public void setAvgUptime(double avgUptime) { this.avgUptime = avgUptime; }

    public boolean isSimulatedData() { return simulatedData; }
    public void setSimulatedData(boolean simulatedData) { this.simulatedData = simulatedData; }
}
