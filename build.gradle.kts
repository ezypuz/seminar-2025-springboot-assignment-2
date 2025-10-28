import java.util.Properties
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.api.tasks.testing.TestResult.ResultType

plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.flywaydb.flyway") version "9.22.3"
    id("org.jlleitschuh.gradle.ktlint") version "13.1.0"
}

group = "com.wafflestudio"
version = "0.0.1-SNAPSHOT"
description = "seminar-2025-springboot-assignment"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.flywaydb:flyway-mysql:9.22.3")
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.mindrot:jbcrypt:0.4")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("com.mysql:mysql-connector-j")
    implementation("org.flywaydb:flyway-mysql")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:mysql")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    // Apache POI (Excel 파일 처리)
    implementation("org.apache.poi:poi:5.3.0") // 기본 POI
    implementation("org.apache.poi:poi-ooxml:5.3.0") // .xlsx 파일용 (선택)
    // XML 파싱 (poi-ooxml 사용 시 필요)
    implementation("org.apache.xmlbeans:xmlbeans:5.2.1")
    // Spring WebFlux (WebClient 사용)
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.apache.commons:commons-compress:1.26.2")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

flyway {
    val properties = Properties()
    file("src/main/resources/application.yaml").inputStream().use {
        properties.load(it)
    }

    url = properties.getProperty("spring.datasource.url")
    user = properties.getProperty("spring.datasource.username")
    password = properties.getProperty("spring.datasource.password")
}


tasks.withType<Test> {
    useJUnitPlatform()

    // 기본 JUnit 로그
    testLogging {
        events = setOf(TestLogEvent.PASSED, TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        showStandardStreams = false
        exceptionFormat = TestExceptionFormat.FULL
    }

    // ✅ 각 테스트 결과마다 콘솔에 SUCCESS / FAIL 표시
    afterTest(KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
        when (result.resultType) {
            ResultType.SUCCESS -> {
                logger.lifecycle("✅ SUCCESS: ${desc.className}.${desc.name}")
            }
            ResultType.FAILURE -> {
                logger.lifecycle("❌ FAIL: ${desc.className}.${desc.name}")
            }
            ResultType.SKIPPED -> {
                logger.lifecycle("⚠️ SKIPPED: ${desc.className}.${desc.name}")
            }
            else -> {}
        }
    }))

    // ✅ "미구현" 테스트가 자동으로 실패 처리되도록 (TODO() 감지)
    // → Kotlin의 TODO()는 NotImplementedError를 던지므로 FAILURE로 잡힘
    // 따로 try/catch 필요 없음
    // 단, SKIPPED로 표시되는 @Disabled 테스트도 FAIL로 간주하고 싶다면 아래 추가:
    afterSuite(KotlinClosure2<TestDescriptor, TestResult, Unit>({ desc, result ->
        if (result.resultType == ResultType.SKIPPED) {
            logger.lifecycle("❌ UNIMPLEMENTED (SKIPPED): ${desc.className}")
        }
    }))
}