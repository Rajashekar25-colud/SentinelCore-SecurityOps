/**
 * monitoringService.js
 *
 * Purpose : Fleet-wide + per-asset health/metrics for the Infrastructure
 *           Monitoring screen (Milestone 1).
 * API     : /api/monitoring/**  and  /api/assets/{id}/health|metrics
 *           (MonitoringController.java)
 * RBAC    : ASSET_VIEW
 */

import axiosInstance from '../api/axios.js';

const monitoringService = {
    /** Fleet summary: totals by health status + active alert count */
    getSummary: () => axiosInstance.get('/api/monitoring/summary'),

    /** Per-asset metrics list, used for fleet-wide charts */
    getAllMetrics: () => axiosInstance.get('/api/monitoring/metrics'),

    /** Single asset's computed health */
    getAssetHealth: (id) => axiosInstance.get(`/api/assets/${id}/health`),

    /** Single asset's current metrics snapshot */
    getAssetMetrics: (id) => axiosInstance.get(`/api/assets/${id}/metrics`),
};

export default monitoringService;