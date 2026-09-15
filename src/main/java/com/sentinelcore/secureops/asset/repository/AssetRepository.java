package com.sentinelcore.secureops.asset.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sentinelcore.secureops.asset.model.Asset;

public interface AssetRepository extends JpaRepository<Asset, Long> {

  List<Asset> findByAssetNameContainingIgnoreCase(String assetName);

  List<Asset> findByStatus(String status);

  long countByStatus(String status);

  long count();

  Optional<Asset> findByIpAddress(String ipAddress);

  boolean existsByIpAddressAndIdNot(String ipAddress, Long id);

  boolean existsByIpAddress(String ipAddress);

  List<Asset> findByAssetType(String assetType);

  List<Asset> findByEnvironment(String environment);

  List<Asset> findByActiveTrue();

  List<Asset> findByActiveFalse();

  long countByActiveTrue();
}
