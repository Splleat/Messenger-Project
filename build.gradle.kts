plugins {
    java
    jacoco
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    alias(libs.plugins.sonarqube)
}

group = "me.splleat"
version = "0.0.1-SNAPSHOT"
description = "messenger-project"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencyManagement {
    imports {
        mavenBom(libs.testcontainers.bom.get().toString())
    }
}

dependencies {
    // --- Spring Boot ---
    implementation(libs.spring.boot.starter)
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.security)
    implementation(libs.spring.boot.starter.websocket)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.data.redis)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.flyway)

    // --- 저장소 / 캐시 ---
    runtimeOnly(libs.mysql.connector)
    implementation(libs.flyway.mysql)
    implementation(libs.redisson.spring.boot.starter)
    implementation(libs.caffeine)
    implementation(libs.hypersistence.utils)

    // --- QueryDSL ---
    implementation(libs.querydsl.jpa)
    annotationProcessor(variantOf(libs.querydsl.apt) { classifier("jpa") })
    annotationProcessor(libs.jakarta.persistence.api)

    // --- Lombok (컴파일 시점에만, jar에 포함되지 않음) ---
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)

    // --- JWT ---
    implementation(libs.jjwt.api)
    runtimeOnly(libs.jjwt.impl)
    runtimeOnly(libs.jjwt.jackson)

    // --- AWS ---
    implementation(libs.aws.s3)

    // --- API 문서 / 메트릭 ---
    implementation(libs.springdoc.openapi.webmvc.ui)
    runtimeOnly(libs.micrometer.registry.prometheus)

    // --- 로컬 개발용 H2 (운영 jar에서 제외됨) ---
    developmentOnly(libs.h2)
    developmentOnly(libs.spring.boot.h2console)

    // --- 테스트 ---
    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.spring.boot.starter.webmvc.test)
    testImplementation(libs.spring.boot.starter.data.jpa.test)
    testImplementation(libs.spring.boot.starter.data.redis.test)
    testImplementation(libs.spring.security.test)
    testImplementation(libs.spring.restdocs.mockmvc)
    testImplementation(libs.bundles.testcontainers)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jar {
    enabled = false
}

jacoco {
    toolVersion = libs.versions.jacoco.get()
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required = true
        html.required = true
    }
}

sonar {
    properties {
        property("sonar.projectKey", "Splleat_Messenger-Project")
        property("sonar.organization", "splleat")
        property("sonar.host.url", "https://sonarcloud.io")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            layout.buildDirectory.file("reports/jacoco/test/jacocoTestReport.xml").get().asFile.path
        )
    }
}
