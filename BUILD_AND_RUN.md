# SentinelCore SecureOps — Milestone 1

## Backend (Windows PowerShell)

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-17"
.\mvnw.cmd -version
.\mvnw.cmd clean test
.\mvnw.cmd spring-boot:run
```

If using PostgreSQL, set these before starting the backend:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://HOST:5432/DATABASE"
$env:SPRING_DATASOURCE_USERNAME="USERNAME"
$env:SPRING_DATASOURCE_PASSWORD="PASSWORD"
```

Optional bootstrap administrator (recommended for a fresh database):

```powershell
$env:SECUREOPS_BOOTSTRAP_ADMIN_USERNAME="admin"
$env:SECUREOPS_BOOTSTRAP_ADMIN_PASSWORD="CHANGE_ME_TO_A_STRONG_PASSWORD"
$env:SECUREOPS_BOOTSTRAP_ADMIN_EMAIL="admin@example.com"
```

## Frontend

In a second terminal:

```powershell
cd frontend
npm install
npm run dev
```

The Milestone 1 page is available at `/monitoring` and is also exposed as **Infrastructure** from the sidebar.

## Package root

All backend Java sources use:

`com.sentinelcore.secureops`

The former friend package has been removed.

## Milestone 1 telemetry

`TelemetrySimulatorService` provides explicitly simulated telemetry for demonstration. Disable it with:

```properties
monitoring.simulation.enabled=false
```

when a real telemetry collector is connected to `AssetService.recordHeartbeat()`.
