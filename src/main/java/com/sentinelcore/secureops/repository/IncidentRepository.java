package com.sentinelcore.secureops.repository;
import com.sentinelcore.secureops.dto.*; import com.sentinelcore.secureops.model.Incident; import org.springframework.data.jpa.repository.*; import org.springframework.data.repository.query.Param; import java.time.LocalDateTime; import java.util.List;
public interface IncidentRepository extends JpaRepository<Incident,Long>{
 long countByStatusIn(List<String> statuses); long countBySeverityAndStatusIn(String severity,List<String> statuses);
 @Query("SELECT new com.sentinelcore.secureops.dto.StatusCount(i.status, COUNT(i)) FROM Incident i GROUP BY i.status") List<StatusCount> getIncidentStatusCounts();
 @Query("SELECT new com.sentinelcore.secureops.dto.SeverityCount(i.severity, COUNT(i)) FROM Incident i GROUP BY i.severity") List<SeverityCount> getIncidentSeverityCounts();
 @Query("SELECT new com.sentinelcore.secureops.dto.TrendPoint(CAST(i.createdAt AS DATE), COUNT(i)) FROM Incident i WHERE i.createdAt >= :since GROUP BY CAST(i.createdAt AS DATE) ORDER BY CAST(i.createdAt AS DATE)") List<TrendPoint> getIncidentTrend(@Param("since") LocalDateTime since);
 @Query("SELECT new com.sentinelcore.secureops.dto.RecentIncidentDTO(i.incidentId, i.title, i.severity, i.status, i.assignedTeam, i.createdAt) FROM Incident i ORDER BY i.createdAt DESC") List<RecentIncidentDTO> findRecentIncidents();
 List<Incident> findAllByOrderByCreatedAtDesc(); default long countActiveIncidents(){return countByStatusIn(List.of("Open","Investigating"));} default long countCriticalIncidents(){return countBySeverityAndStatusIn("Critical",List.of("Open","Investigating"));}
}
