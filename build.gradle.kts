plugins {
    id("java")
    id("io.spring.dependency-management") version "1.1.7"
    id("org.springframework.boot") version "4.0.0" apply false
}

allprojects {
    group = "com.erval.argos"
    version = System.getenv("VERSION") ?: "0.0.1-SNAPSHOT"
    repositories {
        mavenCentral()

        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/ErVaaL/argos-contracts")
            credentials {
                username = System.getenv("GITHUB_ACTOR") ?: System.getenv("GH_USER")
                password = System.getenv("GITHUB_TOKEN") ?: System.getenv("GH_TOKEN")
            }
        }
    }
}

subprojects {
    apply(plugin = "java")
    apply(plugin = "io.spring.dependency-management")

    dependencyManagement {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.0")
        }
    }

    java {
        toolchain { languageVersion.set(JavaLanguageVersion.of(21))}
    }

    tasks.test {
        useJUnitPlatform()
    }

    dependencies {
        testImplementation(platform("org.junit:junit-bom:6.0.2"))
        testImplementation("org.junit.jupiter:junit-jupiter")
        testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    }
}

