import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.wirequery"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	api("org.projectnessie.cel:cel-core:0.3.15")
	api("org.projectnessie.cel:cel-tools:0.3.15")
	api("org.projectnessie.cel:cel-jackson:0.3.15")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.15.0")
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
