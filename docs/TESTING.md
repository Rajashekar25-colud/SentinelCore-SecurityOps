# Testing Guide

## Backend

Run the Spring Boot test suite from the repository root:

```powershell
.\mvnw.cmd test
```

The test dependencies include H2, so tests can run without a local PostgreSQL server when the test profile selects the embedded database. Tests that use secured controller methods should use `@WithMockUser` with the authority required by the endpoint.

To run one test class while developing:

```powershell
.\mvnw.cmd -Dtest=IncidentControllerTest test
```

## Test data

Integration-style tests use the application context and may write to the embedded database. Create records with distinctive values and remove records created by the test when the workflow does not already clean them up. This keeps repeated local runs predictable.

## Frontend

From the `frontend` directory, run the available checks:

```powershell
npm run lint
npm run build
```