package com.sentinelcore.secureops.repository;
import com.sentinelcore.secureops.model.IncidentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface IncidentHistoryRepository extends JpaRepository<IncidentHistory, Long> {
    List<IncidentHistory> findByIncidentIdOrderByChangedAtDesc(Long incidentId);
}
