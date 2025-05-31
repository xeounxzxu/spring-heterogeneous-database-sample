plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.2"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"

//    kotlin("kapt") version "1.9.0"

    id("com.google.devtools.ksp") version "1.9.25-1.0.20"
}

group = "io.xeounxzxu"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
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
    runtimeOnly("com.mysql:mysql-connector-j")
    runtimeOnly("com.oracle.database.jdbc:ojdbc11")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // QueryDSL 의존성 추가
//    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
//    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
//    kapt("jakarta.annotation:jakarta.annotation-api")
//    kapt("jakarta.persistence:jakarta.persistence-api")

    implementation("io.github.openfeign.querydsl:querydsl-jpa:6.11")
    ksp("io.github.openfeign.querydsl:querydsl-ksp-codegen:6.11")
    ksp("jakarta.annotation:jakarta.annotation-api")
    ksp("jakarta.persistence:jakarta.persistence-api")
    annotationProcessor("io.github.openfeign.querydsl:querydsl-apt:6.11:jakarta")
}

//kapt {
//    generateStubs = true
//}

val generated = file("src/main/generated")

tasks.withType<JavaCompile> {
    options.generatedSourceOutputDirectory.set(generated)
}

sourceSets {
    main {
        kotlin.srcDirs += generated
    }
}

tasks.named("clean") {
    doLast {
        generated.deleteRecursively()
    }
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
