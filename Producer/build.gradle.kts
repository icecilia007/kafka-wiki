import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "2.7.16"
	id("io.spring.dependency-management") version "1.0.15.RELEASE"
	kotlin("jvm") version "1.6.21"
	kotlin("plugin.spring") version "1.6.21"
}

group = "com.study"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_11
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.springframework.kafka:spring-kafka")
	implementation("org.apache.kafka:kafka-clients")
	implementation ("com.google.code.gson:gson:2.8.5")
	implementation ("org.slf4j:slf4j-api:2.0.5")
	// https://mvnrepository.com/artifact/org.slf4j/slf4j-simple
	implementation ("org.slf4j:slf4j-simple:2.0.5")
	// https://mvnrepository.com/artifact/com.squareup.okhttp3/okhttp
	implementation ("com.squareup.okhttp3:okhttp:4.9.3")
	// https://mvnrepository.com/artifact/com.launchdarkly/okhttp-eventsource
	implementation ("com.launchdarkly:okhttp-eventsource:2.5.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.kafka:spring-kafka-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "11"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
