package com.sentinelcore.secureops.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Milestone 1 — configurable health/alert thresholds.
 * Bound from application.properties under "monitoring.thresholds.*" so
 * sensitivity can be retuned without code changes (spec section 3).
 */
@Component
@ConfigurationProperties(prefix = "monitoring.thresholds")
public class MonitoringThresholds {

    private int cpuWarning = 70;
    private int cpuCritical = 90;

    private int memoryWarning = 75;
    private int memoryCritical = 90;

    private int diskWarning = 80;
    private int diskCritical = 95;

    /** If an asset hasn't sent a heartbeat in this many minutes, it's OFFLINE. */
    private int heartbeatOfflineMinutes = 5;

    public int getCpuWarning() { return cpuWarning; }
    public void setCpuWarning(int cpuWarning) { this.cpuWarning = cpuWarning; }

    public int getCpuCritical() { return cpuCritical; }
    public void setCpuCritical(int cpuCritical) { this.cpuCritical = cpuCritical; }

    public int getMemoryWarning() { return memoryWarning; }
    public void setMemoryWarning(int memoryWarning) { this.memoryWarning = memoryWarning; }

    public int getMemoryCritical() { return memoryCritical; }
    public void setMemoryCritical(int memoryCritical) { this.memoryCritical = memoryCritical; }

    public int getDiskWarning() { return diskWarning; }
    public void setDiskWarning(int diskWarning) { this.diskWarning = diskWarning; }

    public int getDiskCritical() { return diskCritical; }
    public void setDiskCritical(int diskCritical) { this.diskCritical = diskCritical; }

    public int getHeartbeatOfflineMinutes() { return heartbeatOfflineMinutes; }
    public void setHeartbeatOfflineMinutes(int heartbeatOfflineMinutes) { this.heartbeatOfflineMinutes = heartbeatOfflineMinutes; }
}
