import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.wirequery"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
	mavenLocal() // For development purposes.
	mavenCentral()
}

dependencies {
	implementation("com.wirequery:wirequery-java-core:0.0.1-SNAPSHOT")
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
