package com.sentinelcore.secureops.asset.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "asset")
public class Asset {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String assetName;

  @Column(unique = true)
  private String ipAddress;

  private String assetType;   // Server, Database, Network Device, Cloud Resource, Application, Container
  private String status;      // Healthy, Warning, Critical, Offline

  // --- Milestone 1 fields ---
  private String hostname;
  private String operatingSystem;
  private String environment;   // Production, Staging, Development, QA
  private String ownerTeam;

  @Column(nullable = false)
  private boolean active = true; // soft delete / deactivate instead of hard delete

  private LocalDateTime lastHeartbeat;

  @Column(updatable = false)
  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;
  // --------------------------------

  private int cpuUsage;
  private int memoryUsage;
  private int diskUsage;
  private int networkUsage;

  private double uptime;

  private String location;

  public Asset() {
  }

  public Asset(Long id, String assetName, String ipAddress, String assetType, String status,
               int cpuUsage, int memoryUsage, int diskUsage,
               int networkUsage, double uptime, String location) {
    this.id = id;
    this.assetName = assetName;
    this.ipAddress = ipAddress;
    this.assetType = assetType;
    this.status = status;
    this.cpuUsage = cpuUsage;
    this.memoryUsage = memoryUsage;
    this.diskUsage = diskUsage;
    this.networkUsage = networkUsage;
    this.uptime = uptime;
    this.location = location;
  }

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = this.createdAt;
    if (this.status == null) this.status = "Healthy";
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getAssetName() { return assetName; }
  public void setAssetName(String assetName) { this.assetName = assetName; }

  public String getIpAddress() { return ipAddress; }
  public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

  public String getAssetType() { return assetType; }
  public void setAssetType(String assetType) { this.assetType = assetType; }

  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }

  public String getHostname() { return hostname; }
  public void setHostname(String hostname) { this.hostname = hostname; }

  public String getOperatingSystem() { return operatingSystem; }
  public void setOperatingSystem(String operatingSystem) { this.operatingSystem = operatingSystem; }

  public String getEnvironment() { return environment; }
  public void setEnvironment(String environment) { this.environment = environment; }

  public String getOwnerTeam() { return ownerTeam; }
  public void setOwnerTeam(String ownerTeam) { this.ownerTeam = ownerTeam; }

  public boolean isActive() { return active; }
  public void setActive(boolean active) { this.active = active; }

  public LocalDateTime getLastHeartbeat() { return lastHeartbeat; }
  public void setLastHeartbeat(LocalDateTime lastHeartbeat) { this.lastHeartbeat = lastHeartbeat; }

  public LocalDateTime getCreatedAt() { return createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }

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

  public String getLocation() { return location; }
  public void setLocation(String location) { this.location = location; }
}
