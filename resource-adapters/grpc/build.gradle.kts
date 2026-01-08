plugins {
    `java-library`
}

dependencies {
    implementation(project(":resource-application"))
    api("com.erval.argos:argos-contracts:0.0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")
    implementation("org.springframework:spring-context")
}
