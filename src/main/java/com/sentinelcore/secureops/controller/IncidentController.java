package com.sentinelcore.secureops.incident.controller;

import com.sentinelcore.secureops.incident.model.Incident;
import com.sentinelcore.secureops.incident.service.IncidentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents")
public class IncidentController {

  @Autowired
  private IncidentService incidentService;

  // Get all incidents — all authenticated roles with INCIDENT_VIEW
  @GetMapping
  @PreAuthorize("hasAuthority('INCIDENT_VIEW')")
  public List<Incident> getAllIncidents() {
    return incidentService.getAllIncidents();
  }

  // Create a new incident — roles that can create
  @PostMapping
  @PreAuthorize("hasAuthority('INCIDENT_CREATE')")
  public Incident createIncident(@RequestBody Incident incident) {
    return incidentService.createIncident(incident);
  }

  // Get incident by ID
  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('INCIDENT_VIEW')")
  public Incident getIncidentById(@PathVariable Long id) {
    return incidentService.getIncidentById(id);
  }

  // Update incident — roles that can manage/edit
  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('INCIDENT_MANAGE')")
  public Incident updateIncident(@PathVariable Long id,
                                 @RequestBody Incident incident) {
    return incidentService.updateIncident(id, incident);
  }

  // Delete incident — only roles with full delete permission
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('INCIDENT_DELETE')")
  public void deleteIncident(@PathVariable Long id) {
    incidentService.deleteIncident(id);
  }

  // Dashboard stats — any role that can view incidents
  @GetMapping("/dashboard")
  @PreAuthorize("hasAuthority('INCIDENT_VIEW')")
  public com.sentinelcore.secureops.dashboard.dto.IncidentStatusDTO getIncidentDashboard() {
    java.util.List<com.sentinelcore.secureops.dashboard.dto.StatusCount> counts = incidentService.getIncidentStatusCounts();
    com.sentinelcore.secureops.dashboard.dto.IncidentStatusDTO dto = new com.sentinelcore.secureops.dashboard.dto.IncidentStatusDTO();
    dto.setStatusCounts(counts != null ? counts : java.util.List.of());
    return dto;
  }
}
