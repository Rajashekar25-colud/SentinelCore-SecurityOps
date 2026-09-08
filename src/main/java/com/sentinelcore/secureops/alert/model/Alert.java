package com.sentinelcore.secureops.alert.model;

import com.sentinelcore.secureops.asset.model.Asset;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "alerts")
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── Legacy-compatible display fields ────────────────────────────────────
    private String title;
    private String severity;      // INFO, LOW, MEDIUM, HIGH, CRITICAL
    private String source;        // human-readable origin label, e.g. "DB-SRV-12"
    private LocalDateTime timestamp;

    // ── Milestone 1 lifecycle fields ────────────────────────────────────────
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    private String alertType;     // HIGH_CPU, HIGH_MEMORY, HIGH_DISK_USAGE, NETWORK_FAILURE, ASSET_OFFLINE, HEALTH_CHECK_FAILURE

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double currentValue;
    private Double threshold;

    @Column(nullable = false)
    private String status = "OPEN"; // OPEN, ACKNOWLEDGED, RESOLVED

    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private String acknowledgedBy;
    private String resolvedBy;

    public Alert() {
        this.timestamp = LocalDateTime.now();
    }

    /** Legacy constructor — kept for seed/demo data that isn't tied to a specific asset. */
    public Alert(String title, String severity, String source, LocalDateTime timestamp) {
        this.title = title;
        this.severity = severity;
        this.source = source;
        this.timestamp = timestamp;
        this.status = "OPEN";
    }

    /** Milestone 1 constructor — used by AlertService when generating infrastructure alerts. */
    public Alert(Asset asset, String alertType, String severity, String description,
                 Double currentValue, Double threshold) {
        this.asset = asset;
        this.alertType = alertType;
        this.severity = severity;
        this.description = description;
        this.currentValue = currentValue;
        this.threshold = threshold;
        this.title = alertType.replace('_', ' ');
        this.source = asset != null ? asset.getAssetName() : "unknown";
        this.timestamp = LocalDateTime.now();
        this.status = "OPEN";
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public Asset getAsset() { return asset; }
    public void setAsset(Asset asset) { this.asset = asset; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCurrentValue() { return currentValue; }
    public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
}
