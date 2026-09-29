package com.sentinelcore.secureops.alert.controller;

import com.sentinelcore.secureops.alert.dto.AlertDTO;
import com.sentinelcore.secureops.alert.model.Alert;
import com.sentinelcore.secureops.alert.service.AlertService;
import com.sentinelcore.secureops.common.aop.Auditable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public List<AlertDTO> getAlerts(@RequestParam(required = false) String status) {
        List<Alert> alerts = (status != null && !status.isBlank())
                ? alertService.getByStatus(status.toUpperCase())
                : alertService.getAll();
        return alerts.stream().map(AlertDTO::from).toList();
    }

    @GetMapping("/asset/{assetId}")
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public List<AlertDTO> getAlertsForAsset(@PathVariable Long assetId) {
        return alertService.getByAsset(assetId).stream().map(AlertDTO::from).toList();
    }

    @GetMapping("/counts")
    @PreAuthorize("hasAuthority('ASSET_VIEW')")
    public Object getCounts() {
        return new Object() {
            public final long open = alertService.countByStatus("OPEN");
            public final long acknowledged = alertService.countByStatus("ACKNOWLEDGED");
            public final long resolved = alertService.countByStatus("RESOLVED");
        };
    }

    @PutMapping("/{id}/acknowledge")
    @PreAuthorize("hasAuthority('ASSET_EDIT')")
    @Auditable(action = "Acknowledge Alert")
    public AlertDTO acknowledge(@PathVariable Long id, Authentication auth) {
        String username = auth != null ? auth.getName() : "system";
        return AlertDTO.from(alertService.acknowledge(id, username));
    }

    @PutMapping("/{id}/resolve")
    @PreAuthorize("hasAuthority('ASSET_EDIT')")
    @Auditable(action = "Resolve Alert")
    public AlertDTO resolve(@PathVariable Long id, Authentication auth) {
        String username = auth != null ? auth.getName() : "system";
        return AlertDTO.from(alertService.resolve(id, username));
    }
}
