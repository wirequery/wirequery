import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootJar
import java.net.URI

tasks.getByName<BootJar>("bootJar") {
    enabled = false
}

plugins {
    id("org.springframework.boot") version "2.7.11"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    `maven-publish`
    `java-library`
}

group = "com.wirequery"
java.sourceCompatibility = JavaVersion.VERSION_11

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    api("com.wirequery:wirequery-spring-5:${project.version}")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "wirequery-spring-boot-2-starter"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "OSSRH"
            url = URI("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}
