package com.sentinelcore.secureops.controller;
import com.sentinelcore.secureops.aop.Auditable; import com.sentinelcore.secureops.dto.*; import com.sentinelcore.secureops.model.*; import com.sentinelcore.secureops.service.IncidentService;
import jakarta.validation.Valid; import org.springframework.http.ResponseEntity; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.List;
@RestController @RequestMapping("/api/incidents") public class IncidentController {
 private final IncidentService service; public IncidentController(IncidentService service){this.service=service;}
 @GetMapping @PreAuthorize("hasAuthority('INCIDENT_VIEW')") public List<Incident> all(){return service.getAllIncidents();}
 @PostMapping @PreAuthorize("hasAuthority('INCIDENT_CREATE')") @Auditable(action="Create Incident") public Incident create(@RequestBody Incident i,Authentication a){return service.createIncident(i,a.getName());}
 @GetMapping("/{id}") @PreAuthorize("hasAuthority('INCIDENT_VIEW')") public Incident one(@PathVariable Long id){return service.getIncidentById(id);}
 @PutMapping("/{id}") @PreAuthorize("hasAuthority('INCIDENT_MANAGE')") @Auditable(action="Update Incident") public Incident update(@PathVariable Long id,@RequestBody Incident i,Authentication a){return service.updateIncident(id,i,a.getName());}
 @DeleteMapping("/{id}") @PreAuthorize("hasAuthority('INCIDENT_DELETE')") @Auditable(action="Delete Incident") public ResponseEntity<Void> delete(@PathVariable Long id,Authentication a){service.deleteIncident(id,a.getName());return ResponseEntity.noContent().build();}
 @PostMapping("/{id}/status") @PreAuthorize("hasAuthority('INCIDENT_MANAGE')") @Auditable(action="Change Incident Status") public Incident status(@PathVariable Long id,@Valid @RequestBody IncidentStatusRequest r,Authentication a){return service.changeStatus(id,r,a.getName());}
 @PostMapping("/{id}/assign") @PreAuthorize("hasAuthority('INCIDENT_MANAGE')") @Auditable(action="Assign Incident") public Incident assign(@PathVariable Long id,@Valid @RequestBody IncidentAssignmentRequest r,Authentication a){return service.assign(id,r,a.getName());}
 @PostMapping("/{id}/resolve") @PreAuthorize("hasAuthority('INCIDENT_RESOLVE')") @Auditable(action="Resolve Incident") public Incident resolve(@PathVariable Long id,@RequestBody(required=false) IncidentStatusRequest r,Authentication a){return service.resolve(id,r==null?null:r.getResolutionNotes(),a.getName());}
 @GetMapping("/{id}/sla") @PreAuthorize("hasAuthority('INCIDENT_VIEW')") public IncidentSlaDTO sla(@PathVariable Long id){return service.getSla(id);}
 @GetMapping("/{id}/history") @PreAuthorize("hasAuthority('INCIDENT_VIEW')") public List<IncidentHistory> history(@PathVariable Long id){return service.getHistory(id);}
 @GetMapping("/dashboard") @PreAuthorize("hasAuthority('INCIDENT_VIEW')") public IncidentStatusDTO dashboard(){IncidentStatusDTO d=new IncidentStatusDTO();d.setStatusCounts(service.getIncidentStatusCounts());return d;}
}
