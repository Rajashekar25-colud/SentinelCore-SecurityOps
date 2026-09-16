package com.sentinelcore.secureops.incident;

import com.sentinelcore.secureops.dto.IncidentStatusRequest;
import com.sentinelcore.secureops.model.Incident;
import com.sentinelcore.secureops.repository.IncidentHistoryRepository;
import com.sentinelcore.secureops.repository.IncidentRepository;
import com.sentinelcore.secureops.service.IncidentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class IncidentServiceTest {
    private IncidentRepository repo;
    private IncidentHistoryRepository history;
    private IncidentService service;

    @BeforeEach
    void setUp() {
        repo = Mockito.mock(IncidentRepository.class);
        history = Mockito.mock(IncidentHistoryRepository.class);
        service = new IncidentService(repo, history);
        Mockito.when(repo.count()).thenReturn(0L);
    }

    @Test
    void createAssignsIdAndDefaultSla() {
        Incident input = new Incident();
        input.setTitle("Failed login attempts");
        input.setSeverity("High");
        Mockito.when(repo.save(Mockito.any(Incident.class))).thenAnswer(inv -> inv.getArgument(0));

        Incident result = service.createIncident(input, "admin");

        assertEquals("INC-1001", result.getIncidentId());
        assertEquals("Open", result.getStatus());
        assertEquals(2, result.getSlaHours());
        assertNotNull(result.getDueAt());
        Mockito.verify(history).save(Mockito.any());
    }

    @Test
    void workflowCannotMoveBackwards() {
        Incident i = new Incident();
        i.setId(1L); i.setIncidentId("INC-1"); i.setTitle("Test"); i.setSeverity("Medium"); i.setStatus("Investigating");
        Mockito.when(repo.findById(1L)).thenReturn(Optional.of(i));
        IncidentStatusRequest r = new IncidentStatusRequest(); r.setStatus("Open");
        assertThrows(IllegalArgumentException.class, () -> service.changeStatus(1L, r, "admin"));
    }
}
