plugins {
    id("java-library")
    id("io.spring.dependency-management")
}

dependencies {
    api(project(":resource-core"))

    api("org.springframework.boot:spring-boot-starter-data-mongodb")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testImplementation("org.testcontainers:mongodb:1.21.3")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation(project(":resource-application"))

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

