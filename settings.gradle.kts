rootProject.name = "argos-v2-resource-service"

include(
    ":resource-core",
    ":resource-application",
    ":resource-adapters:grpc",
    ":resource-adapters:mongo",
    ":resource-bootstrap",
)
