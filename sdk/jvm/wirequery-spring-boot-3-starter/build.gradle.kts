import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

plugins {
    id("org.springframework.boot") version "3.0.6"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    `maven-publish`
    `java-library`
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.1.1"
}

signing {
    useGpgCmd()
    sign(configurations.runtimeElements.get())
}

centralPortal {
    username = System.getenv("CENTRAL_USERNAME")
    password = System.getenv("CENTRAL_TOKEN")
    pom {
        name = "WireQuery Spring Boot 3 Starter"
        description = "WireQuery Spring Boot 3 Starter library"
        url = "https://github.com/wirequery/wirequery"
        licenses {
            license {
                name = "MIT License"
                url = "https://github.com/wirequery/wirequery/blob/main/licenses/LICENSE-MIT.md"
            }
        }
        developers {
            developer {
                name = "Wouter Nederhof"
                email = "wouter@wirequery.io"
                url = "https://github.com/wnederhof"
            }
        }
        scm {
            connection = "scm:git:git://github.com/wirequery/wirequery.git"
            developerConnection = "scm:git:ssh://github.com:wirequery/wirequery.git"
            url = "http://github.com/wirequery/wirequery/tree/master"
        }
    }
}

group = "com.wirequery"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api("com.wirequery:wirequery-spring-6:${project.version}")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "wirequery-spring-boot-3-starter"
            from(components["java"])
        }
    }
}
