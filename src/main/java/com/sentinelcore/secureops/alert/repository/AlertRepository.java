package com.sentinelcore.secureops.alert.repository;

import com.sentinelcore.secureops.alert.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findTop10ByOrderByTimestampDesc();

    List<Alert> findByStatus(String status);

    List<Alert> findByAsset_Id(Long assetId);

    List<Alert> findByAsset_IdAndStatus(Long assetId, String status);

    long countByStatus(String status);

    Optional<Alert> findFirstByAsset_IdAndAlertTypeAndStatus(Long assetId, String alertType, String status);
}
