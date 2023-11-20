import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val protobufVersion by extra("3.22.3")
val protobufPluginVersion by extra("0.9.2")
val grpcVersion by extra("1.40.1")

plugins {
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
	`maven-publish`
	`java-library`
	id("com.google.protobuf") version "0.9.2"
}

group = "com.wirequery"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = "wirequery-spring-5"
            from(components["java"])
        }
    }
}

repositories {
	mavenLocal() // For development purposes.
	mavenCentral()
}

dependencies {
	api("com.wirequery:wirequery-java-core:0.0.1-SNAPSHOT")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8:2.13.5")
	implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.13.5")
	implementation("com.fasterxml.jackson.module:jackson-module-parameter-names:2.13.5")
	implementation("org.springframework:spring-core:5.3.27")
	implementation("org.springframework:spring-context:5.3.27")
	implementation("org.springframework:spring-web:5.3.27")
	implementation("org.springframework:spring-webmvc:5.3.27")
	implementation("org.apache.tomcat.embed:tomcat-embed-core:9.0.74")
	testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
	testImplementation("org.mockito:mockito-core:5.2.0")
	testImplementation("org.mockito:mockito-junit-jupiter:5.2.0")
	testImplementation("org.assertj:assertj-core:3.22.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")

	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

	api("io.grpc:grpc-protobuf:${grpcVersion}")
	api("io.grpc:grpc-stub:1.54.1")
	api("io.grpc:grpc-kotlin-stub:1.3.0")
	api("com.google.protobuf:protobuf-java:$protobufVersion")
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

sourceSets {
	main {
		proto {
			srcDir ("../../../proto")
		}
	}
}

protobuf {
	// Artifacts not available for Apple Silicon. Therefore fallback to x86_64 arch.
	protoc {
		artifact = if (osdetector.os == "osx") {
			"com.google.protobuf:protoc:${protobufVersion}:osx-x86_64"
		} else {
			"com.google.protobuf:protoc:${protobufVersion}"
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
