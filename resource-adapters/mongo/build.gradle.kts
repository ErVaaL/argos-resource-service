plugins {
    id("java-library")
    id("io.spring.dependency-management")
}

dependencies {
    api(project(":services:resource-service:resource-core"))

    api("org.springframework.boot:spring-boot-starter-data-mongodb")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.testcontainers:mongodb:1.21.3")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

