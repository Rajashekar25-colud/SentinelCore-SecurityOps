package com.sentinelcore.secureops.security;

import com.sentinelcore.secureops.user.model.Permission;
import com.sentinelcore.secureops.user.model.Role;
import com.sentinelcore.secureops.user.model.User;
import com.sentinelcore.secureops.user.repository.PermissionRepository;
import com.sentinelcore.secureops.user.repository.RoleRepository;
import com.sentinelcore.secureops.user.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import com.sentinelcore.secureops.incident.model.Incident;
import com.sentinelcore.secureops.incident.repository.IncidentRepository;
import com.sentinelcore.secureops.alert.model.Alert;
import com.sentinelcore.secureops.asset.model.Asset;
import com.sentinelcore.secureops.vulnerability.model.Vulnerability;
import com.sentinelcore.secureops.alert.repository.AlertRepository;
import com.sentinelcore.secureops.asset.repository.AssetRepository;
import com.sentinelcore.secureops.vulnerability.repository.VulnerabilityRepository;
import java.time.LocalDateTime;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final PasswordEncoder passwordEncoder;
    private final IncidentRepository incidentRepository;
    private final AlertRepository alertRepository;
    private final VulnerabilityRepository vulnerabilityRepository;
    private final AssetRepository assetRepository;

    @Value("${secureops.bootstrap.admin.username:admin}")
    private String bootstrapAdminUsername;

    @Value("${secureops.bootstrap.admin.password:}")
    private String bootstrapAdminPassword;

    @Value("${secureops.bootstrap.admin.email:admin@sentinelcore.local}")
    private String bootstrapAdminEmail;

    public DataInitializer(UserRepository userRepository,
                           RoleRepository roleRepository,
                           PermissionRepository permissionRepository,
                           PasswordEncoder passwordEncoder,
                           IncidentRepository incidentRepository,
                           AlertRepository alertRepository,
                           VulnerabilityRepository vulnerabilityRepository,
                           AssetRepository assetRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.passwordEncoder = passwordEncoder;
        this.incidentRepository = incidentRepository;
        this.alertRepository = alertRepository;
        this.vulnerabilityRepository = vulnerabilityRepository;
        this.assetRepository = assetRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        // ── 1. Seed permissions (idempotent) ─────────────────────────────────
        String[] allPerms = {
            "USER_MANAGE", "ROLE_ASSIGN",
            "ASSET_CREATE", "ASSET_EDIT", "ASSET_DELETE", "ASSET_VIEW",
            "INCIDENT_VIEW", "INCIDENT_CREATE", "INCIDENT_MANAGE", "INCIDENT_RESOLVE", "INCIDENT_DELETE",
            "SERVER_RESTART", "CLUSTER_SCALE", "CLOUD_MODIFY",
            "VULN_MANAGE", "COMPLIANCE_VIEW", "REPORT_EXPORT", "AUDIT_VIEW",
            "INTEGRATION_CONFIG", "SETTINGS_ACCESS"
        };

        java.util.Map<String, Permission> permMap = permissionRepository.findAll().stream()
                .collect(Collectors.toMap(Permission::getName, p -> p, (p1, p2) -> p1));

        for (String p : allPerms) {
            if (!permMap.containsKey(p)) {
                Permission newPerm = permissionRepository.save(new Permission(p));
                permMap.put(p, newPerm);
            }
        }

        java.util.Map<String, Role> roleMap = roleRepository.findAll().stream()
                .collect(Collectors.toMap(Role::getName, r -> r, (r1, r2) -> r1));

        // ── 2. Seed roles ──────────────────────────────────────────────────
        seedOrUpdateRole(roleMap, permMap, "ROLE_SUPER_ADMIN", allPerms);

        seedOrUpdateRole(roleMap, permMap, "ROLE_ADMIN",
                "ASSET_CREATE","ASSET_EDIT","ASSET_DELETE","ASSET_VIEW",
                "INCIDENT_VIEW","INCIDENT_CREATE","INCIDENT_MANAGE","INCIDENT_RESOLVE","INCIDENT_DELETE",
                "SERVER_RESTART","CLUSTER_SCALE","CLOUD_MODIFY",
                "VULN_MANAGE","COMPLIANCE_VIEW","REPORT_EXPORT","AUDIT_VIEW",
                "USER_MANAGE","ROLE_ASSIGN","SETTINGS_ACCESS","INTEGRATION_CONFIG");

        seedOrUpdateRole(roleMap, permMap, "ROLE_SOC_MANAGER",
                "ASSET_VIEW",
                "INCIDENT_VIEW","INCIDENT_CREATE","INCIDENT_MANAGE","INCIDENT_RESOLVE",
                "REPORT_EXPORT","AUDIT_VIEW");

        seedOrUpdateRole(roleMap, permMap, "ROLE_SECURITY_ANALYST",
                "ASSET_VIEW",
                "INCIDENT_VIEW","INCIDENT_CREATE","INCIDENT_MANAGE",
                "VULN_MANAGE","REPORT_EXPORT");

        seedOrUpdateRole(roleMap, permMap, "ROLE_INCIDENT_RESPONDER",
                "ASSET_VIEW",
                "INCIDENT_VIEW","INCIDENT_MANAGE","INCIDENT_RESOLVE",
                "AUDIT_VIEW");

        seedOrUpdateRole(roleMap, permMap, "ROLE_INFRA_ENGINEER",
                "ASSET_VIEW","ASSET_CREATE","ASSET_EDIT","ASSET_DELETE",
                "INCIDENT_VIEW",
                "SERVER_RESTART","CLUSTER_SCALE","CLOUD_MODIFY",
                "REPORT_EXPORT");

        seedOrUpdateRole(roleMap, permMap, "ROLE_DEVSECOPS",
                "ASSET_VIEW","ASSET_CREATE","ASSET_EDIT",
                "INCIDENT_VIEW","INCIDENT_CREATE","INCIDENT_MANAGE",
                "VULN_MANAGE","SERVER_RESTART","CLUSTER_SCALE",
                "REPORT_EXPORT");

        seedOrUpdateRole(roleMap, permMap, "ROLE_AUDITOR",
                "ASSET_VIEW",
                "INCIDENT_VIEW",
                "AUDIT_VIEW","COMPLIANCE_VIEW","REPORT_EXPORT");

        seedOrUpdateRole(roleMap, permMap, "ROLE_VIEWER",
                "ASSET_VIEW","INCIDENT_VIEW");

        // ── 3. Bootstrap super-admin ───────────────────────────────────────
        if (bootstrapAdminPassword != null && !bootstrapAdminPassword.isBlank()
                && userRepository.findByUsername(bootstrapAdminUsername).isEmpty()) {
            User admin = new User(bootstrapAdminUsername, passwordEncoder.encode(bootstrapAdminPassword), bootstrapAdminEmail);
            admin.setFirstName("System");
            admin.setLastName("Administrator");
            admin.setOrganization("SentinelCore");
            Role superAdminRole = roleMap.get("ROLE_SUPER_ADMIN");
            if (superAdminRole != null) {
                admin.getRoles().add(superAdminRole);
            }
            userRepository.save(admin);
            System.out.println("[SentinelCore] Bootstrap administrator created for username: " + bootstrapAdminUsername);
        } else if (bootstrapAdminPassword == null || bootstrapAdminPassword.isBlank()) {
            System.out.println("[SentinelCore] Bootstrap administrator creation skipped: set SECUREOPS_BOOTSTRAP_ADMIN_PASSWORD to enable it.");
        }

        // ── 4. Seed incidents (Milestone 2 demo data, unchanged) ───────────
        if (incidentRepository.count() == 0) {
            Incident i1 = new Incident();
            i1.setIncidentId("INC-889");
            i1.setTitle("Failed Login Attempts");
            i1.setDescription("Multiple failed logins detected.");
            i1.setSeverity("Critical");
            i1.setStatus("Open");
            i1.setAssignedTeam("Security Team");
            i1.setAssignedTo("John");
            i1.setSlaHours(2);
            i1.setCreatedAt(LocalDateTime.now());

            Incident i2 = new Incident();
            i2.setIncidentId("INC-888");
            i2.setTitle("Kubernetes Cluster Alert");
            i2.setDescription("High CPU usage detected.");
            i2.setSeverity("High");
            i2.setStatus("Investigating");
            i2.setAssignedTeam("SOC Team");
            i2.setAssignedTo("Alice");
            i2.setSlaHours(4);
            i2.setCreatedAt(LocalDateTime.now());

            incidentRepository.save(i1);
            incidentRepository.save(i2);
        }

        // ── 5. Seed Milestone 1 demo assets [SIMULATED DEMO DATA] ──────────
        // These give the Infrastructure Monitoring screens something to show
        // on first run. TelemetrySimulatorService takes over from here and
        // will drift their metrics on every scheduled tick.
        if (assetRepository.count() == 0) {
            assetRepository.save(demoAsset("WEB-SRV-01", "10.0.1.11", "Server", "web-01.internal",
                    "Ubuntu 22.04", "Production", "Platform Team", "New York", 32, 54, 41, 22, 99.98));
            assetRepository.save(demoAsset("DB-SRV-12", "10.0.1.24", "Database", "db-12.internal",
                    "PostgreSQL 15 / Ubuntu 22.04", "Production", "Data Team", "New York", 61, 78, 91, 35, 99.95));
            assetRepository.save(demoAsset("APP-SRV-47", "10.0.2.7", "Application", "app-47.internal",
                    "Amazon Linux 2023", "Production", "Backend Team", "Frankfurt", 45, 39, 55, 60, 99.99));
            assetRepository.save(demoAsset("FW-GW-03", "10.0.0.1", "Network Device", "fw-gw-03",
                    "Cisco ASA 9.18", "Production", "Network Team", "Frankfurt", 18, 44, 30, 71, 99.999));
            assetRepository.save(demoAsset("K8S-NODE-05", "10.0.3.15", "Container", "k8s-node-05",
                    "Container-Optimized OS", "Staging", "DevSecOps", "Singapore", 74, 68, 47, 40, 99.90));
        }

        // ── 6. Seed vulnerabilities (Milestone 3 demo data, unchanged) ─────
        if (vulnerabilityRepository.count() == 0) {
            vulnerabilityRepository.save(new Vulnerability("CVE-2023-4863", 8.8, 92, "14 Servers, 2 Clusters", "Pending", "Deploy Patch"));
            vulnerabilityRepository.save(new Vulnerability("CVE-2023-5363", 6.5, 65, "3 Firewalls", "Scheduled", "View Steps"));
        }

        // Note: the old free-text Alert(title, severity, source, timestamp) seed rows
        // were removed here on purpose. AlertService + TelemetrySimulatorService now
        // generate real lifecycle-tracked alerts from the seeded assets' metrics above
        // (e.g. DB-SRV-12 starts at 91% disk, which is >= the default 95%? no — but
        // 91% > default cpuWarning-style disk threshold of 80%, so a HIGH_DISK_USAGE
        // alert will appear on the very first scheduler tick, demonstrating Alert
        // Generation without needing hand-seeded rows).
    }

    private Asset demoAsset(String name, String ip, String type, String hostname, String os,
                             String environment, String owner, String location,
                             int cpu, int mem, int disk, int net, double uptime) {
        Asset a = new Asset();
        a.setAssetName(name);
        a.setIpAddress(ip);
        a.setAssetType(type);
        a.setHostname(hostname);
        a.setOperatingSystem(os);
        a.setEnvironment(environment);
        a.setOwnerTeam(owner);
        a.setLocation(location);
        a.setCpuUsage(cpu);
        a.setMemoryUsage(mem);
        a.setDiskUsage(disk);
        a.setNetworkUsage(net);
        a.setUptime(uptime);
        a.setActive(true);
        a.setLastHeartbeat(LocalDateTime.now());
        a.setStatus("Healthy"); // recalculated by the scheduler on the first tick
        return a;
    }

    private void seedOrUpdateRole(java.util.Map<String, Role> roleMap, java.util.Map<String, Permission> permMap, String roleName, String... perms) {
        Role role = roleMap.computeIfAbsent(roleName, k -> roleRepository.save(new Role(k)));
        Set<Permission> pSet = Arrays.stream(perms)
                .map(permMap::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toSet());
        role.setPermissions(pSet);
        roleRepository.save(role);
    }
}
