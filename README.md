# Argos Resource Service

Resource microservice for devices and measurements. Exposes a GraphQL API and a gRPC query service backed by MongoDB.
Created in hexagonal architecture style (ports and adapters).

## Modules

- `resource-core`: domain models and ports
- `resource-application`: application services
- `resource-adapters:mongo`: MongoDB persistence adapter
- `resource-adapters:grpc`: gRPC query adapter
- `resource-bootstrap`: Spring Boot application and GraphQL controllers

## Requirements

- Java 21
- Docker (for local Mongo + Keycloak via compose)
- Gradle (or use `./gradlew`)

## Configuration

The service reads configuration from environment variables (see `resource-bootstrap/src/main/resources/application.yaml`):

- `SPRING_MONGODB_URI` (required)
- `SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI` (required)
- `ARGOS_RESOURCE_GRPC_PORT` (optional, default 9091)

Default HTTP port is `8081`.

## Run locally (Docker Compose)

```bash
export GH_USER=...          # if building image from source in compose
export GH_TOKEN=...

docker compose up --build
```

> Note:
>
> Environment variables can also be set in .env file for Docker Compose. (Don't commit sensitive info!)

Services:
- GraphQL: http://localhost:8081/graphql
- gRPC: localhost:9091
- Mongo: localhost:27017 (container)
- Keycloak: http://localhost:8080

## Run locally (Gradle)

```bash
export SPRING_MONGODB_URI=mongodb://root:root@localhost:27017/argos_resource?authSource=admin
export SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://localhost:8080/realms/master
export ARGOS_RESOURCE_GRPC_PORT=9091

./gradlew :resource-bootstrap:bootRun
```

## GraphQL

Schema: `resource-bootstrap/src/main/resources/graphql/schema.graphqls`

Example queries:

```graphql
query {
  devices(page: { page: 0, size: 10, sortBy: "name", sortDirection: ASC }) {
    content { id name type building room active }
    totalElements
    page
    size
  }
}
```

```graphql
query {
  measurements(
    filter: { deviceId: "device-123" }
    page: { page: 0, size: 50, sortBy: "timestamp", sortDirection: DESC }
  ) {
    content { id deviceId type value sequenceNumber timestamp }
    totalElements
    page
    size
  }
}
```

Example mutations:

```graphql
mutation {
  createDevice(input: { name: "Sensor-A", type: TEMP, building: "HQ", room: "101" }) {
    id
    name
    active
  }
}
```

```graphql
mutation {
  createMeasurement(input: { deviceId: "device-123", type: TEMP, value: 23.5 }) {
    id
    deviceId
    value
    timestamp
  }
}
```

## gRPC

The gRPC service is defined in the `argos-contracts` package and exposed by
`resource-adapters/grpc`. The server listens on `ARGOS_RESOURCE_GRPC_PORT` (default 9091).

Methods:
- `GetDevice`
- `GetLastMeasurements`

If you have the protobuf definitions locally, you can generate stubs and call the service
using your preferred gRPC client.

## Tests

```bash
./gradlew test
```

Note: Mongo adapter tests use Testcontainers.
