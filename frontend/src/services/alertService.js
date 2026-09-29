/**
 * alertService.js
 *
 * Purpose : Infrastructure alert lifecycle (OPEN -> ACKNOWLEDGED -> RESOLVED).
 * API     : /api/alerts/**  (AlertController.java)
 * RBAC    : ASSET_VIEW (read) / ASSET_EDIT (acknowledge, resolve)
 */

import axiosInstance from '../api/axios.js';

const alertService = {
    /** All alerts, optionally filtered by status: OPEN | ACKNOWLEDGED | RESOLVED */
    getAll: (status) => axiosInstance.get('/api/alerts', { params: status ? { status } : {} }),

    /** All alerts for a single asset */
    getForAsset: (assetId) => axiosInstance.get(`/api/alerts/asset/${assetId}`),

    /** { open, acknowledged, resolved } counts */
    getCounts: () => axiosInstance.get('/api/alerts/counts'),

    acknowledge: (id) => axiosInstance.put(`/api/alerts/${id}/acknowledge`),

    resolve: (id) => axiosInstance.put(`/api/alerts/${id}/resolve`),
};

export default alertService;