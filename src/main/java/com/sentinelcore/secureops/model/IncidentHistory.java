package com.sentinelcore.secureops.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_history", indexes = {
        @Index(name = "idx_incident_history_incident", columnList = "incident_id"),
        @Index(name = "idx_incident_history_time", columnList = "changed_at")
})
public class IncidentHistory {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "incident_id", nullable = false) private Long incidentId;
    @Column(name = "incident_code", nullable = false, length = 50) private String incidentCode;
    @Column(name = "changed_by", nullable = false, length = 100) private String changedBy;
    @Column(name = "action", nullable = false, length = 80) private String action;
    @Column(name = "from_value", columnDefinition = "TEXT") private String fromValue;
    @Column(name = "to_value", columnDefinition = "TEXT") private String toValue;
    @Column(name = "changed_at", nullable = false) private LocalDateTime changedAt = LocalDateTime.now();
    public IncidentHistory() {}
    public IncidentHistory(Long incidentId, String incidentCode, String changedBy, String action, String fromValue, String toValue) {
        this.incidentId=incidentId; this.incidentCode=incidentCode; this.changedBy=changedBy; this.action=action; this.fromValue=fromValue; this.toValue=toValue;
    }
    public Long getId(){return id;} public Long getIncidentId(){return incidentId;} public String getIncidentCode(){return incidentCode;} public String getChangedBy(){return changedBy;} public String getAction(){return action;} public String getFromValue(){return fromValue;} public String getToValue(){return toValue;} public LocalDateTime getChangedAt(){return changedAt;}
    public void setId(Long v){id=v;} public void setIncidentId(Long v){incidentId=v;} public void setIncidentCode(String v){incidentCode=v;} public void setChangedBy(String v){changedBy=v;} public void setAction(String v){action=v;} public void setFromValue(String v){fromValue=v;} public void setToValue(String v){toValue=v;} public void setChangedAt(LocalDateTime v){changedAt=v;}
}
