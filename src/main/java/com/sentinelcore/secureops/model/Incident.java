package com.sentinelcore.secureops.model;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
@Entity
@Table(name="incidents", indexes={@Index(name="idx_incident_status",columnList="status"),@Index(name="idx_incident_severity",columnList="severity"),@Index(name="idx_incident_created",columnList="created_at")})
public class Incident {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=50) private String incidentId;
 @Column(nullable=false,length=200) private String title;
 @Column(columnDefinition="TEXT") private String description;
 @Column(nullable=false,length=20) private String severity="Medium";
 @Column(nullable=false,length=30) private String status="Open";
 private String assignedTeam, assignedTo, affectedAsset; private Integer slaHours; private LocalDateTime dueAt,createdAt,updatedAt,resolvedAt,closedAt;
 @Column(columnDefinition="TEXT") private String resolutionNotes; private String createdBy,updatedBy;
 public Incident(){}
 public String getSlaStatus(){if("Resolved".equalsIgnoreCase(status)||"Closed".equalsIgnoreCase(status))return "COMPLIED";if(dueAt==null)return "NOT_CONFIGURED";long m=Duration.between(LocalDateTime.now(),dueAt).toMinutes();if(m<0)return "BREACHED";if(m<=60)return "AT_RISK";return "ON_TRACK";}
 public Long getId(){return id;} public String getIncidentId(){return incidentId;} public String getTitle(){return title;} public String getDescription(){return description;} public String getSeverity(){return severity;} public String getStatus(){return status;} public String getAssignedTeam(){return assignedTeam;} public String getAssignedTo(){return assignedTo;} public String getAffectedAsset(){return affectedAsset;} public Integer getSlaHours(){return slaHours;} public LocalDateTime getDueAt(){return dueAt;} public LocalDateTime getCreatedAt(){return createdAt;} public LocalDateTime getUpdatedAt(){return updatedAt;} public LocalDateTime getResolvedAt(){return resolvedAt;} public LocalDateTime getClosedAt(){return closedAt;} public String getResolutionNotes(){return resolutionNotes;} public String getCreatedBy(){return createdBy;} public String getUpdatedBy(){return updatedBy;}
 public void setId(Long v){id=v;} public void setIncidentId(String v){incidentId=v;} public void setTitle(String v){title=v;} public void setDescription(String v){description=v;} public void setSeverity(String v){severity=v;} public void setStatus(String v){status=v;} public void setAssignedTeam(String v){assignedTeam=v;} public void setAssignedTo(String v){assignedTo=v;} public void setAffectedAsset(String v){affectedAsset=v;} public void setSlaHours(Integer v){slaHours=v;} public void setDueAt(LocalDateTime v){dueAt=v;} public void setCreatedAt(LocalDateTime v){createdAt=v;} public void setUpdatedAt(LocalDateTime v){updatedAt=v;} public void setResolvedAt(LocalDateTime v){resolvedAt=v;} public void setClosedAt(LocalDateTime v){closedAt=v;} public void setResolutionNotes(String v){resolutionNotes=v;} public void setCreatedBy(String v){createdBy=v;} public void setUpdatedBy(String v){updatedBy=v;}
}
