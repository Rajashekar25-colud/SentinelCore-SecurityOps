package com.prashanth.dashboard.controller;

import com.prashanth.dashboard.model.Incident;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class IncidentControllerTest {

    @Autowired
    private IncidentController incidentController;

    @Test
    @WithMockUser(username = "incident-test", authorities = {
            "INCIDENT_CREATE", "INCIDENT_VIEW", "INCIDENT_MANAGE", "INCIDENT_DELETE"
    })
    void incidentLifecycleAssignsIdAndUpdatesFields() {
        Incident incident = new Incident();
        incident.setTitle("Suspicious login activity");
        incident.setDescription("Multiple failed logins were detected.");
        incident.setSeverity("HIGH");
        incident.setStatus("OPEN");
        incident.setAssignedTeam("SOC");
        incident.setSlaHours(4);

        Incident created = incidentController.createIncident(incident);

        assertNotNull(created.getId());
        assertTrue(created.getIncidentId().matches("INC-\\d+"));
        assertNotNull(created.getCreatedAt());

        Incident update = new Incident();
        update.setTitle("Suspicious login activity - triaged");
        update.setDescription("The source account has been temporarily locked.");
        update.setSeverity("CRITICAL");
        update.setStatus("INVESTIGATING");
        update.setAssignedTeam("SOC");
        update.setAssignedTo("analyst-1");
        update.setSlaHours(2);

        Incident updated = incidentController.updateIncident(created.getId(), update);

        assertEquals(created.getIncidentId(), updated.getIncidentId());
        assertEquals("Suspicious login activity - triaged", updated.getTitle());
        assertEquals("CRITICAL", updated.getSeverity());
        assertEquals("INVESTIGATING", updated.getStatus());
        assertEquals("analyst-1", updated.getAssignedTo());

        Incident fetched = incidentController.getIncidentById(created.getId());
        assertEquals(updated.getTitle(), fetched.getTitle());
        assertEquals(updated.getStatus(), fetched.getStatus());

        incidentController.deleteIncident(created.getId());
    }
}