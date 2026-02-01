dependencies {
    implementation(project(":gateway-scripting-core"))
    implementation(rootProject.libs.apache.groovy)
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/NicolasBreval/spring-cloud-gateway-scripting")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "gateway-scripting-groovy"
            
            pom {
                description.set("Spring Cloud Gateway filter to modify requests using scripts.")
                url.set("https://github.com/NicolasBreval/spring-cloud-gateway-scripting")
            }
        }
    }
}