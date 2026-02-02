plugins {
    `java-library`
    alias(libs.plugins.spring.deps.management)
    alias(libs.plugins.spotless)
    `maven-publish`
}

allprojects {
    group = "org.nbreval.spring.cloud.gateway.scripting"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.gradle.java-library")
    apply(plugin = "io.spring.dependency-management")
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "maven-publish")

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(25))
        }
    }

    dependencies {
        api(platform(rootProject.libs.spring.framework.bom))
        api(platform(rootProject.libs.spring.cloud.bom))
        api("org.springframework.cloud:spring-cloud-starter-gateway")

        testImplementation("org.springframework:spring-test")
        testImplementation(rootProject.libs.junit.jupiter)
        testImplementation(rootProject.libs.reactor.test)
        testImplementation(rootProject.libs.assertj.core)
        testImplementation(rootProject.libs.nimbus.jose.jwt)

        testRuntimeOnly(rootProject.libs.junit.platform.launcher)
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    spotless {
        java {
            googleJavaFormat(libs.versions.googleJavaFormat.get().toString())
            
            removeUnusedImports()
            
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    publishing {
        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                artifactId = project.name
            }
        }
    }
}