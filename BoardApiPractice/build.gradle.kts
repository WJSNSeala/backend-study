plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.4.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.google.cloud.tools.jib") version "3.4.4"
	kotlin("plugin.jpa") version "1.9.25"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(19)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.mysql:mysql-connector-j")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val dockerUsername: String by project
val dockerPassword: String by project

jib {
	// 애플리케이션을 빌드할 기본 이미지를 구성
	from {
		image = "eclipse-temurin:21.0.3_9-jre-ubi9-minimal"
	}
	to {
		image = "rocketman35/spring_jlb"
		tags = setOf("0.0.1")

		auth {
			username = ""
			password = ""
		}
	}
	// 빌드된 이미지에서 실행될 컨테이너를 구성
	container {
		jvmFlags = listOf(
			"-Dspring.profiles.active=local",
			"-Dfile.encoding=UTF-8",
		)
		ports = listOf("8080")
		setAllowInsecureRegistries(true)  // 보안이 적용되지 않은 registry 연결 허용
	}
}