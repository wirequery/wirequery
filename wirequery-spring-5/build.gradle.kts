import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.wirequery"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenLocal() // For development purposes.
	mavenCentral()
}

dependencies {
	implementation("com.wirequery:wirequery-java-core:0.0.1-SNAPSHOT")
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
	testImplementation("org.mockito:mockito-core:4.5.1")
	testImplementation("org.mockito:mockito-junit-jupiter:4.5.1")
	testImplementation("org.assertj:assertj-core:3.22.0")
	testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
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
