import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URI

val protobufVersion by extra("3.22.3")
val protobufPluginVersion by extra("0.9.2")
val grpcVersion by extra("1.55.1")

plugins {
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    `maven-publish`
    `java-library`
    id("com.google.protobuf") version "0.9.2"
}

group = "com.wirequery"
java.sourceCompatibility = JavaVersion.VERSION_17

java {
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenLocal() // For development purposes.
    mavenCentral()
}

dependencies {
    api("com.wirequery:wirequery-java-core:${project.version}")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.14.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.14.2")
    implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.14.2")
    implementation("org.springframework:spring-core:6.0.6")
    implementation("org.springframework:spring-context:6.0.6")
    implementation("org.springframework:spring-web:6.0.6")
    implementation("org.springframework:spring-webmvc:6.0.6")
    implementation("org.apache.tomcat.embed:tomcat-embed-core:10.1.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-core:5.2.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")
    testImplementation("org.assertj:assertj-core:3.24.2")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

    api("io.grpc:grpc-protobuf:$grpcVersion")
    api("io.grpc:grpc-netty:$grpcVersion")
    api("io.grpc:grpc-stub:$grpcVersion")
    api("io.grpc:grpc-kotlin-stub:1.3.0")
    api("com.google.protobuf:protobuf-java:$protobufVersion")
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

sourceSets {
    main {
        proto {
            srcDir("../../../proto")
        }
    }
}

protobuf {
    // Artifacts not available for Apple Silicon. Therefore fallback to x86_64 arch.
    protoc {
        artifact =
            if (osdetector.os == "osx") {
                "com.google.protobuf:protoc:$protobufVersion:osx-x86_64"
            } else {
                "com.google.protobuf:protoc:$protobufVersion"
            }
    }
    plugins {
        id("grpc") {
            artifact = "io.grpc:protoc-gen-grpc-java:1.54.1"
        }
        id("grpckt") {
            artifact = "io.grpc:protoc-gen-grpc-kotlin:1.3.0:jdk8@jar"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.builtins {
                java {}
            }
            it.plugins {
                id("grpc")
                id("grpckt")
            }
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "wirequery-spring-6"
            from(components["java"])
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = URI("https://maven.pkg.github.com/wirequery/wirequery")
            credentials {
                username = System.getenv("GITHUB_ACTOR")
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
}
