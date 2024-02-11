import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.21"
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
        name = "WireQuery Java Core"
        description = "WireQuery Java Core library"
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
java.sourceCompatibility = JavaVersion.VERSION_11

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("dev.cel:cel:0.2.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
    api("com.google.guava:guava:32.1.3-jre")
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.3.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.9.3")
    testImplementation("org.assertj:assertj-core:3.24.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "11"
        javaParameters = true
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "wirequery-java-core"
            from(components["java"])
        }
    }
}
