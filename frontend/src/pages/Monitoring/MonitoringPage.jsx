/**
 * MonitoringPage.jsx
 *
 * Milestone 1 — Infrastructure Monitoring screen.
 * Combines: summary cards, fleet averages, per-asset health table, an alert
 * management panel (acknowledge/resolve), and an asset detail drawer.
 *
 * Data note: metrics shown here come from TelemetrySimulatorService on the
 * backend (SIMULATED DATA) until a real telemetry collector replaces it.
 */

import { useState, useEffect, useCallback } from 'react';
import DashboardLayout from '../../layouts/DashboardLayout.jsx';
import Loader from '../../components/common/Loader/Loader.jsx';
import { useToast } from '../../components/common/Toast/Toast.jsx';
import { useAuth } from '../../context/AuthContext.jsx';
import assetService from '../../services/assetService.js';
import monitoringService from '../../services/monitoringService.js';
import alertService from '../../services/alertService.js';

const POLL_MS = 10000;

function miniBar(pct) {
    const p = Number(pct) || 0;
    const cls = p >= 90 ? 'mini-red' : p >= 70 ? 'mini-orange' : 'mini-green';
    return (
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 6, minWidth: 90 }}>
            <span className="mini-bar-wrap"><span className={`mini-bar ${cls}`} style={{ width: `${Math.min(p, 100)}%` }} /></span>
            {p}%
        </span>
    );
}

function healthBadge(status) {
    const colors = { Healthy: '#16a34a', Warning: '#ca8a04', Critical: '#dc2626', Offline: '#6b7280' };
    const bg = colors[status] || '#6b7280';
    return <span style={{ background: `${bg}22`, color: bg, border: `1px solid ${bg}55`, padding: '2px 10px', borderRadius: 40, fontSize: '0.72rem', fontWeight: 700 }}>{status || 'Unknown'}</span>;
}

function severityBadge(sev) {
    const colors = { CRITICAL: '#dc2626', HIGH: '#dc2626', MEDIUM: '#ca8a04', LOW: '#16a34a', INFO: '#0277bd' };
    const bg = colors[(sev || '').toUpperCase()] || '#6b7280';
    return <span style={{ background: `${bg}22`, color: bg, border: `1px solid ${bg}55`, padding: '2px 8px', borderRadius: 40, fontSize: '0.7rem', fontWeight: 700 }}>{sev}</span>;
}

function lifecycleBadge(status) {
    const cls = { OPEN: 'badge-status active', ACKNOWLEDGED: 'badge-status', RESOLVED: 'badge-status resolved' };
    return <span className={cls[status] || 'badge-status'}>{status}</span>;
}

export default function MonitoringPage() {
    const { hasPermission } = useAuth();
    const showToast = useToast();
    const canEdit = hasPermission('ASSET_EDIT');

    const [summary, setSummary] = useState(null);
    const [assets, setAssets] = useState([]);
    const [alerts, setAlerts] = useState([]);
    const [loading, setLoading] = useState(true);

    const [search, setSearch] = useState('');
    const [statusFilter, setStatusFilter] = useState('');

    const [selectedAsset, setSelectedAsset] = useState(null);
    const [assetAlerts, setAssetAlerts] = useState([]);
    const [drawerLoading, setDrawerLoading] = useState(false);

    const load = useCallback(async (showSpinner = false) => {
        if (showSpinner) setLoading(true);
        try {
            const [summaryRes, assetsRes, alertsRes] = await Promise.all([
                monitoringService.getSummary(),
                assetService.getAll(),
                alertService.getAll(),
            ]);
            setSummary(summaryRes.data);
            setAssets(assetsRes.data || []);
            setAlerts((alertsRes.data || []).filter(a => a.status !== 'RESOLVED')
                .sort((a, b) => (a.status === 'OPEN' ? -1 : 1)));
        } catch {
            showToast('Failed to load monitoring data', 'error');
        } finally {
            setLoading(false);
        }
    }, [showToast]);

    useEffect(() => {
        load(true);
        const interval = setInterval(() => load(false), POLL_MS);
        return () => clearInterval(interval);
    }, [load]);

    async function openAsset(asset) {
        setSelectedAsset(asset);
        setDrawerLoading(true);
        try {
            const res = await alertService.getForAsset(asset.id);
            setAssetAlerts(res.data || []);
        } catch {
            setAssetAlerts([]);
        } finally {
            setDrawerLoading(false);
        }
    }

    async function handleAcknowledge(id) {
        try {
            await alertService.acknowledge(id);
            showToast('Alert acknowledged');
            load(false);
        } catch (e) {
            showToast(e.response?.data?.error || 'Failed to acknowledge alert', 'error');
        }
    }

    async function handleResolve(id) {
        try {
            await alertService.resolve(id);
            showToast('Alert resolved', 'success');
            load(false);
        } catch (e) {
            showToast(e.response?.data?.error || 'Failed to resolve alert', 'error');
        }
    }

    const filteredAssets = assets.filter(a => {
        if (statusFilter && a.status !== statusFilter) return false;
        if (search) {
            const q = search.toLowerCase();
            return a.assetName?.toLowerCase().includes(q) || a.ipAddress?.includes(q) || a.hostname?.toLowerCase().includes(q);
        }
        return true;
    });

    const summaryCards = [
        { color: 'blue', label: 'Total Assets', value: summary?.totalAssets, icon: 'ph-hard-drives' },
        { color: 'green', label: 'Healthy', value: summary?.healthyAssets, icon: 'ph-check-circle', valueColor: 'var(--success-green)' },
        { color: 'yellow', label: 'Warning', value: summary?.warningAssets, icon: 'ph-warning', valueColor: 'var(--warning-amber)' },
        { color: 'red', label: 'Critical', value: summary?.criticalAssets, icon: 'ph-shield-warning', valueColor: 'var(--danger-red)' },
        { color: 'red', label: 'Offline', value: summary?.offlineAssets, icon: 'ph-plug', valueColor: '#6b7280' },
        { color: 'orange', label: 'Active Alerts', value: summary?.activeAlerts, icon: 'ph-bell', valueColor: 'var(--warning-amber)' },
    ];

    return (
        <DashboardLayout>
            <section className="content-header" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: 15, marginBottom: 20 }}>
                <h1>Infrastructure Monitoring
                    <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)', fontWeight: 400, marginLeft: 10 }}>
                        Milestone 1 {summary?.simulatedData ? '· simulated telemetry' : ''}
                    </span>
                </h1>
                <div style={{ display: 'flex', gap: 10 }}>
                    <input
                        placeholder="Search name, IP, hostname..."
                        value={search}
                        onChange={(e) => setSearch(e.target.value)}
                        style={{ padding: '8px 12px', borderRadius: 6, border: '1px solid var(--border-color)', background: 'var(--bg-inset)', color: 'var(--text-primary)' }}
                    />
                    <select value={statusFilter} onChange={(e) => setStatusFilter(e.target.value)}
                        style={{ padding: '8px 12px', borderRadius: 6, border: '1px solid var(--border-color)', background: 'var(--bg-inset)', color: 'var(--text-primary)' }}>
                        <option value="">All statuses</option>
                        <option value="Healthy">Healthy</option>
                        <option value="Warning">Warning</option>
                        <option value="Critical">Critical</option>
                        <option value="Offline">Offline</option>
                    </select>
                </div>
            </section>

            {loading ? <Loader /> : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>

                    {/* Summary cards */}
                    <section className="kpi-grid">
                        {summaryCards.map((c) => (
                            <div key={c.label} className={`kpi-card ${c.color}`}>
                                <div className="kpi-card-header">
                                    <span className="kpi-card-title">{c.label}</span>
                                    <i className={`ph ${c.icon} kpi-card-icon`} />
                                </div>
                                <div className="kpi-card-value" style={c.valueColor ? { color: c.valueColor } : {}}>
                                    {c.value ?? '—'}
                                </div>
                            </div>
                        ))}
                    </section>

                    {/* Fleet averages */}
                    <section style={{ background: 'var(--bg-panel)', border: '1px solid var(--border-color)', borderRadius: 8, padding: 16 }}>
                        <h3 style={{ marginBottom: 12, fontSize: '0.95rem' }}>Fleet Averages</h3>
                        <div className="system-health-grid">
                            <div className="health-mini-card">
                                <span className="health-mini-label">CPU</span>
                                <span className="health-mini-value">{summary?.avgCpuUsage ?? '—'}%</span>
                            </div>
                            <div className="health-mini-card">
                                <span className="health-mini-label">Memory</span>
                                <span className="health-mini-value">{summary?.avgMemoryUsage ?? '—'}%</span>
                            </div>
                            <div className="health-mini-card">
                                <span className="health-mini-label">Disk</span>
                                <span className="health-mini-value">{summary?.avgDiskUsage ?? '—'}%</span>
                            </div>
                            <div className="health-mini-card">
                                <span className="health-mini-label">Uptime</span>
                                <span className="health-mini-value">{summary?.avgUptime ?? '—'}%</span>
                            </div>
                        </div>
                    </section>

                    <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 20, alignItems: 'start' }}>

                        {/* Asset health table */}
                        <section style={{ background: 'var(--bg-panel)', border: '1px solid var(--border-color)', borderRadius: 8, padding: 16 }}>
                            <h3 style={{ marginBottom: 12, fontSize: '0.95rem' }}>Asset Inventory &amp; Health</h3>
                            <div className="table-wrapper">
                                <table className="data-table">
                                    <thead>
                                        <tr>
                                            <th>Asset</th>
                                            <th>Type</th>
                                            <th>IP / Host</th>
                                            <th>Status</th>
                                            <th>CPU</th>
                                            <th>Memory</th>
                                            <th>Disk</th>
                                            <th>Last Heartbeat</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        {filteredAssets.map((a) => (
                                            <tr key={a.id} style={{ cursor: 'pointer' }} onClick={() => openAsset(a)}>
                                                <td><strong>{a.assetName}</strong><div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>{a.environment}</div></td>
                                                <td>{a.assetType}</td>
                                                <td>{a.ipAddress}<div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>{a.hostname}</div></td>
                                                <td>{healthBadge(a.status)}</td>
                                                <td>{miniBar(a.cpuUsage)}</td>
                                                <td>{miniBar(a.memoryUsage)}</td>
                                                <td>{miniBar(a.diskUsage)}</td>
                                                <td style={{ fontSize: '0.78rem' }}>{a.lastHeartbeat ? new Date(a.lastHeartbeat).toLocaleTimeString() : '—'}</td>
                                                <td>
                                                    <button className="btn-blue" onClick={(e) => { e.stopPropagation(); openAsset(a); }}>
                                                        View
                                                    </button>
                                                </td>
                                            </tr>
                                        ))}
                                        {filteredAssets.length === 0 && (
                                            <tr><td colSpan={9} style={{ textAlign: 'center', padding: 20, color: 'var(--text-muted)' }}>No assets match your filters.</td></tr>
                                        )}
                                    </tbody>
                                </table>
                            </div>
                        </section>

                        {/* Alerts panel */}
                        <section style={{ background: 'var(--bg-panel)', border: '1px solid var(--border-color)', borderRadius: 8, padding: 16 }}>
                            <h3 style={{ marginBottom: 12, fontSize: '0.95rem' }}>Active Infrastructure Alerts</h3>
                            <div style={{ display: 'flex', flexDirection: 'column', gap: 10, maxHeight: 520, overflowY: 'auto' }}>
                                {alerts.map((a) => (
                                    <div key={a.id} style={{ border: '1px solid var(--border-color)', borderRadius: 6, padding: 10 }}>
                                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: 8 }}>
                                            <div>
                                                <div style={{ fontWeight: 600, fontSize: '0.85rem' }}>{a.title}</div>
                                                <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>{a.assetName}</div>
                                            </div>
                                            {severityBadge(a.severity)}
                                        </div>
                                        <p style={{ fontSize: '0.78rem', color: 'var(--text-secondary)', margin: '6px 0' }}>{a.description}</p>
                                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                                            {lifecycleBadge(a.status)}
                                            {canEdit && (
                                                <div style={{ display: 'flex', gap: 6 }}>
                                                    {a.status === 'OPEN' && (
                                                        <button className="btn-blue" onClick={() => handleAcknowledge(a.id)}>Acknowledge</button>
                                                    )}
                                                    <button className="btn-blue" onClick={() => handleResolve(a.id)}>Resolve</button>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                ))}
                                {alerts.length === 0 && (
                                    <p style={{ color: 'var(--text-muted)', fontSize: '0.85rem' }}>No active alerts. All systems nominal.</p>
                                )}
                            </div>
                        </section>
                    </div>
                </div>
            )}

            {/* Asset detail drawer */}
            {selectedAsset && (
                <div
                    style={{ position: 'fixed', inset: 0, background: 'rgba(0,0,0,0.5)', zIndex: 50, display: 'flex', justifyContent: 'flex-end' }}
                    onClick={() => setSelectedAsset(null)}
                >
                    <div
                        style={{ width: 420, maxWidth: '90vw', height: '100%', background: 'var(--bg-panel)', padding: 20, overflowY: 'auto' }}
                        onClick={(e) => e.stopPropagation()}
                    >
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 16 }}>
                            <h2 style={{ fontSize: '1.1rem' }}>{selectedAsset.assetName}</h2>
                            <button className="btn-blue" onClick={() => setSelectedAsset(null)}>Close</button>
                        </div>

                        <div style={{ marginBottom: 16 }}>{healthBadge(selectedAsset.status)}</div>

                        <table style={{ width: '100%', fontSize: '0.82rem', marginBottom: 20 }}>
                            <tbody>
                                <tr><td style={{ color: 'var(--text-muted)', padding: '4px 0' }}>Type</td><td>{selectedAsset.assetType}</td></tr>
                                <tr><td style={{ color: 'var(--text-muted)', padding: '4px 0' }}>Hostname</td><td>{selectedAsset.hostname || '—'}</td></tr>
                                <tr><td style={{ color: 'var(--text-muted)', padding: '4px 0' }}>IP Address</td><td>{selectedAsset.ipAddress}</td></tr>
                                <tr><td style={{ color: 'var(--text-muted)', padding: '4px 0' }}>OS</td><td>{selectedAsset.operatingSystem || '—'}</td></tr>
                                <tr><td style={{ color: 'var(--text-muted)', padding: '4px 0' }}>Environment</td><td>{selectedAsset.environment || '—'}</td></tr>
                                <tr><td style={{ color: 'var(--text-muted)', padding: '4px 0' }}>Owner Team</td><td>{selectedAsset.ownerTeam || '—'}</td></tr>
                                <tr><td style={{ color: 'var(--text-muted)', padding: '4px 0' }}>Location</td><td>{selectedAsset.location || '—'}</td></tr>
                                <tr><td style={{ color: 'var(--text-muted)', padding: '4px 0' }}>Last Heartbeat</td><td>{selectedAsset.lastHeartbeat ? new Date(selectedAsset.lastHeartbeat).toLocaleString() : '—'}</td></tr>
                            </tbody>
                        </table>

                        <h4 style={{ marginBottom: 8, fontSize: '0.88rem' }}>Live Metrics</h4>
                        <div className="system-health-grid" style={{ marginBottom: 20 }}>
                            <div className="health-mini-card"><span className="health-mini-label">CPU</span><span className="health-mini-value">{selectedAsset.cpuUsage}%</span></div>
                            <div className="health-mini-card"><span className="health-mini-label">Memory</span><span className="health-mini-value">{selectedAsset.memoryUsage}%</span></div>
                            <div className="health-mini-card"><span className="health-mini-label">Disk</span><span className="health-mini-value">{selectedAsset.diskUsage}%</span></div>
                            <div className="health-mini-card"><span className="health-mini-label">Network</span><span className="health-mini-value">{selectedAsset.networkUsage}%</span></div>
                            <div className="health-mini-card"><span className="health-mini-label">Uptime</span><span className="health-mini-value">{selectedAsset.uptime}%</span></div>
                        </div>

                        <h4 style={{ marginBottom: 8, fontSize: '0.88rem' }}>Recent Alerts</h4>
                        {drawerLoading ? <Loader /> : (
                            <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                                {assetAlerts.map((a) => (
                                    <div key={a.id} style={{ border: '1px solid var(--border-color)', borderRadius: 6, padding: 8 }}>
                                        <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                                            <span style={{ fontSize: '0.8rem', fontWeight: 600 }}>{a.title}</span>
                                            {severityBadge(a.severity)}
                                        </div>
                                        <div style={{ marginTop: 4 }}>{lifecycleBadge(a.status)}</div>
                                    </div>
                                ))}
                                {assetAlerts.length === 0 && <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>No alerts for this asset.</p>}
                            </div>
                        )}
                    </div>
                </div>
            )}
        </DashboardLayout>
    );
}