package com.sentinelcore.secureops.asset.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.sentinelcore.secureops.common.aop.Auditable;
import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.asset.service.AssetService;

@RestController
@RequestMapping("/api/assets")
@CrossOrigin(origins = "*")
public class AssetController {

  private final AssetService assetService;

  public AssetController(AssetService assetService) {
    this.assetService = assetService;
  }

  @GetMapping("/test")
  public String test() {
    return "Asset Controller Working!";
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ASSET_VIEW')")
  public List<Asset> getAllAssets() {
    return assetService.getAll();
  }

  @GetMapping("/active")
  @PreAuthorize("hasAuthority('ASSET_VIEW')")
  public List<Asset> getActiveAssets() {
    return assetService.getActive();
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasAuthority('ASSET_VIEW')")
  public Optional<Asset> getAssetById(@PathVariable Long id) {
    return assetService.getById(id);
  }

  @PostMapping
  @PreAuthorize("hasAuthority('ASSET_CREATE')")
  @Auditable(action = "Create Asset")
  public Asset createAsset(@RequestBody Asset asset) {
    return assetService.create(asset);
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasAuthority('ASSET_EDIT')")
  @Auditable(action = "Edit Asset")
  public Asset updateAsset(@PathVariable Long id, @RequestBody Asset updatedAsset) {
    return assetService.update(id, updatedAsset);
  }

  /** Soft delete (deactivate). Spec section 1 asks for "Delete/deactivate asset". */
  @PutMapping("/{id}/deactivate")
  @PreAuthorize("hasAuthority('ASSET_DELETE')")
  @Auditable(action = "Deactivate Asset")
  public Asset deactivateAsset(@PathVariable Long id) {
    return assetService.deactivate(id);
  }

  /** Hard delete kept for admin cleanup use — prefer /deactivate for normal operations. */
  @DeleteMapping("/{id}")
  @PreAuthorize("hasAuthority('ASSET_DELETE')")
  @Auditable(action = "Delete Asset")
  public String deleteAsset(@PathVariable Long id) {
    assetService.hardDelete(id);
    return "Asset deleted successfully!";
  }

  @GetMapping("/search")
  @PreAuthorize("hasAuthority('ASSET_VIEW')")
  public List<Asset> searchAssets(@RequestParam String keyword) {
    return assetService.searchByName(keyword);
  }

  @GetMapping("/status/{status}")
  @PreAuthorize("hasAuthority('ASSET_VIEW')")
  public List<Asset> getAssetsByStatus(@PathVariable String status) {
    return assetService.filterByStatus(status);
  }

  @GetMapping("/type/{type}")
  @PreAuthorize("hasAuthority('ASSET_VIEW')")
  public List<Asset> getAssetsByType(@PathVariable String type) {
    return assetService.filterByType(type);
  }

  @GetMapping("/environment/{environment}")
  @PreAuthorize("hasAuthority('ASSET_VIEW')")
  public List<Asset> getAssetsByEnvironment(@PathVariable String environment) {
    return assetService.filterByEnvironment(environment);
  }

  /** Manual heartbeat/telemetry push endpoint — for when a real agent replaces the simulator. */
  @PostMapping("/{id}/heartbeat")
  @PreAuthorize("hasAuthority('ASSET_EDIT')")
  public Asset heartbeat(@PathVariable Long id,
                         @RequestParam int cpu,
                         @RequestParam int memory,
                         @RequestParam int disk,
                         @RequestParam int network) {
    return assetService.recordHeartbeat(id, cpu, memory, disk, network);
  }
}
