import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.tasks.bundling.BootBuildImage

val protobufVersion by extra("3.22.3")
val protobufPluginVersion by extra("0.9.2")
val grpcVersion by extra("1.55.1")

plugins {
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.0"
    kotlin("jvm") version "1.9.20"
    kotlin("plugin.spring") version "1.8.21"
    id("com.google.protobuf") version "0.9.2"
}

group = "com.wirequery"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenLocal() // For development purposes.
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom("com.netflix.graphql.dgs:graphql-dgs-platform-dependencies:latest.release")
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.google.re2j:re2j:1.6")

    implementation("org.postgresql:postgresql")
    implementation("com.graphql-java-kickstart:graphiql-spring-boot-starter:11.1.0")
    implementation("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")

    implementation("com.google.protobuf:protobuf-java:3.22.0")

    implementation("org.flywaydb:flyway-core")

    implementation("io.lettuce:lettuce-core:6.2.6.RELEASE")

    implementation("com.google.protobuf:protobuf-java:3.22.0")
    implementation("net.devh:grpc-spring-boot-starter:2.15.0.RELEASE")

    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("com.netflix.graphql.dgs:graphql-dgs-subscriptions-sse-autoconfigure")
    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter")
    implementation("com.netflix.graphql.dgs:graphql-dgs-extended-scalars")

    implementation("org.springframework.boot:spring-boot-starter-graphql")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("com.graphql-java:graphql-java-extended-scalars:21.0")

    implementation("com.auth0:java-jwt:4.4.0")

    implementation("io.grpc:grpc-protobuf:$grpcVersion")
    implementation("io.grpc:grpc-netty:$grpcVersion")
    implementation("io.grpc:grpc-stub:$grpcVersion")
    implementation("io.grpc:grpc-kotlin-stub:1.3.0")
    implementation("com.google.protobuf:protobuf-java:$protobufVersion")

    testImplementation("org.springframework.graphql:spring-graphql-test")

    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
    testImplementation("org.testcontainers:testcontainers:1.19.1")
    testImplementation("org.testcontainers:junit-jupiter:1.19.1")
    testImplementation("org.testcontainers:postgresql:1.19.1")
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

// only for m1
if (osdetector.os == "osx") {
    tasks.getByName<BootBuildImage>("bootBuildImage") {
        builder.set("dashaun/builder:tiny")
        environment.set(environment.get() + mapOf("BP_NATIVE_IMAGE" to "true"))
    }
}

sourceSets {
    main {
        proto {
            srcDir("../../wirequery/proto")
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
            artifact = "io.grpc:protoc-gen-grpc-java:1.58.0"
        }
    }

    generateProtoTasks {
        ofSourceSet("main").forEach {
            it.builtins {
                java {}
            }
            it.plugins {
                id("grpc")
            }
        }
    }
}
