import net.ltgt.gradle.nullaway.nullaway
import net.ltgt.gradle.errorprone.CheckSeverity
import net.ltgt.gradle.errorprone.errorprone
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.kotlin.dsl.errorprone

plugins {
    java
    id("org.springframework.boot") version "4.0.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("net.ltgt.errorprone") version "5.1.0"
    id("net.ltgt.nullaway") version "3.0.0"
}

group = "com.system"
version = "0.0.1-SNAPSHOT"
description = "cleanSpring"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

val mockitoAgent: Configuration = configurations.create("mockitoAgent")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // bean validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // jpa
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")

    //lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testCompileOnly("org.projectlombok:lombok")

    //db
//    runtimeOnly("com.h2database:h2")
    runtimeOnly("com.mysql:mysql-connector-j")

    // docker compose
    runtimeOnly("org.springframework.boot:spring-boot-docker-compose")

    // jspecify
    implementation("org.jspecify:jspecify:1.0.0")
    errorprone("com.google.errorprone:error_prone_core:2.48.0")
    errorprone("com.uber.nullaway:nullaway:0.13.1")
//    testImplementation("org.junit-pioneer:junit-pioneer:2.3.0")

    mockitoAgent("org.mockito:mockito-core:5.18.0") { isTransitive = false }
}

nullaway {
    onlyNullMarked.set(true)
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs("-javaagent:${mockitoAgent.asPath}")
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
        check("NullAway", CheckSeverity.ERROR)
    }
}


