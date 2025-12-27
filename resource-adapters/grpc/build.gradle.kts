plugins {
    `java-library`
}

dependencies {
    implementation(project(":services:resource-service:resource-application"))
    api(project(":contracts"))

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("org.springframework:spring-context")
}
