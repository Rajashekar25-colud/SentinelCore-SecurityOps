package com.sentinelcore.secureops.alert.dto;

import java.time.LocalDateTime;

public class AlertDTO {

    private Long id;
    private Long assetId;
    private String assetName;
    private String alertType;
    private String title;
    private String severity;
    private String description;
    private Double currentValue;
    private Double threshold;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime acknowledgedAt;
    private LocalDateTime resolvedAt;
    private String acknowledgedBy;
    private String resolvedBy;

    public AlertDTO() {}

    public static AlertDTO from(com.sentinelcore.secureops.alert.model.Alert a) {
        AlertDTO dto = new AlertDTO();
        dto.id = a.getId();
        dto.assetId = a.getAsset() != null ? a.getAsset().getId() : null;
        dto.assetName = a.getAsset() != null ? a.getAsset().getAssetName() : a.getSource();
        dto.alertType = a.getAlertType();
        dto.title = a.getTitle();
        dto.severity = a.getSeverity();
        dto.description = a.getDescription();
        dto.currentValue = a.getCurrentValue();
        dto.threshold = a.getThreshold();
        dto.status = a.getStatus();
        dto.createdAt = a.getTimestamp();
        dto.acknowledgedAt = a.getAcknowledgedAt();
        dto.resolvedAt = a.getResolvedAt();
        dto.acknowledgedBy = a.getAcknowledgedBy();
        dto.resolvedBy = a.getResolvedBy();
        return dto;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getAssetId() { return assetId; }
    public void setAssetId(Long assetId) { this.assetId = assetId; }

    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }

    public String getAlertType() { return alertType; }
    public void setAlertType(String alertType) { this.alertType = alertType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCurrentValue() { return currentValue; }
    public void setCurrentValue(Double currentValue) { this.currentValue = currentValue; }

    public Double getThreshold() { return threshold; }
    public void setThreshold(Double threshold) { this.threshold = threshold; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }

    public LocalDateTime getResolvedAt() { return resolvedAt; }
    public void setResolvedAt(LocalDateTime resolvedAt) { this.resolvedAt = resolvedAt; }

    public String getAcknowledgedBy() { return acknowledgedBy; }
    public void setAcknowledgedBy(String acknowledgedBy) { this.acknowledgedBy = acknowledgedBy; }

    public String getResolvedBy() { return resolvedBy; }
    public void setResolvedBy(String resolvedBy) { this.resolvedBy = resolvedBy; }
}
