import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mockkVersion = "1.13.3"
val h2Version = "1.4.200"
val tokenValidationVersion = "2.1.8"
val logstashVersion = "7.2"
val springSleuthVersion = "3.1.5"
val unleashVersion = "3.3.3"
val problemSpringWebStartVersion = "0.27.0"
val pdfboxVersion = "2.0.27"
val springRetryVersion = "2.0.0"
val springMockkVersion = "3.1.2"
val springDocVersion = "1.6.13"
val testContainersVersion = "1.17.6"
val tikaVersion = "2.6.0"
val threeTenExtraVersion = "1.7.2"
val shedlockVersion = "4.42.0"
val archunitVersion = "1.0.1"
val verapdfVersion = "1.22.2"


java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

plugins {
    val kotlinVersion = "1.7.22"
    kotlin("jvm") version kotlinVersion
    kotlin("plugin.spring") version kotlinVersion
    kotlin("plugin.jpa") version kotlinVersion
    id("org.springframework.boot") version "2.7.5"
    idea
}

apply(plugin = "io.spring.dependency-management")

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.threeten:threeten-extra:$threeTenExtraVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("javax.cache:cache-api")
    implementation("org.ehcache:ehcache")

    implementation("com.github.navikt:klage-kodeverk:v1.1.9")

    implementation("net.javacrumbs.shedlock:shedlock-spring:$shedlockVersion")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:$shedlockVersion")

    implementation("org.springframework.cloud:spring-cloud-starter-sleuth:$springSleuthVersion")
    implementation("org.springdoc:springdoc-openapi-ui:$springDocVersion")

    implementation("org.projectreactor:reactor-spring:1.0.1.RELEASE")

    implementation("org.flywaydb:flyway-core")
    implementation("com.zaxxer:HikariCP")
    implementation("org.postgresql:postgresql")

    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("ch.qos.logback:logback-classic")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("com.kjetland:mbknor-jackson-jsonschema_2.13:1.0.39")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")

    implementation("no.nav.security:token-validation-spring:$tokenValidationVersion")
    implementation("no.nav.security:token-client-spring:$tokenValidationVersion")

    implementation("org.springframework.retry:spring-retry:$springRetryVersion")
    implementation("org.zalando:problem-spring-web-starter:$problemSpringWebStartVersion")

    implementation("org.verapdf:validation-model:$verapdfVersion")
    implementation("org.apache.pdfbox:pdfbox:$pdfboxVersion")
    implementation("org.apache.tika:tika-core:$tikaVersion")

    implementation("commons-io:commons-io:2.11.0")
    implementation("commons-fileupload:commons-fileupload:1.4")

    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.ninja-squad:springmockk:$springMockkVersion")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage")
        exclude(group = "org.mockito")
    }
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:postgresql:$testContainersVersion")
    testImplementation("com.tngtech.archunit:archunit-junit5:$archunitVersion")
}

idea {
    module {
        isDownloadJavadoc = true
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    this.archiveFileName.set("app.jar")
}