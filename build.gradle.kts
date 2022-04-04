import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val mockkVersion = "1.10.5"
val h2Version = "1.4.200"
val tokenValidationVersion = "1.3.2"
val logstashVersion = "6.6"
val springSleuthVersion = "3.0.4"
val unleashVersion = "3.3.3"
val problemSpringWebStartVersion = "0.26.2"
val pdfboxVersion = "2.0.19"
val springRetryVersion = "1.3.1"
val springMockkVersion = "3.0.1"
val springFoxVersion = "3.0.0"
val testContainersVersion = "1.16.2"
val tikaVersion = "1.24.1"
val nimbusVersion = "8.20.1"
val threeTenExtraVersion = "1.6.0"
val shedlockVersion = "4.23.0"
val archunitVersion = "0.19.0"
val verapdfVersion = "1.18.8"
val kabalKodeverkVersion = "2021.12.14-15.39.5f6715f05a9d"

val githubUser: String by project
val githubPassword: String by project

java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    maven {
        url = uri("https://maven.pkg.github.com/navikt/simple-slack-poster")
        credentials {
            username = githubUser
            password = githubPassword
        }
    }
    jcenter()
    maven("https://packages.confluent.io/maven/")
    maven("https://jitpack.io")
}

plugins {
    kotlin("jvm") version "1.6.0"
    kotlin("plugin.spring") version "1.6.0"
    kotlin("plugin.jpa") version "1.6.0"
    id("org.springframework.boot") version "2.5.12"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    idea
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    //temporary fix:
    implementation("com.nimbusds:nimbus-jose-jwt:$nimbusVersion")
    implementation("org.threeten:threeten-extra:$threeTenExtraVersion")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-jdbc")
    implementation("javax.cache:cache-api")
    implementation("org.ehcache:ehcache")

    implementation("com.github.navikt:kabal-kodeverk:$kabalKodeverkVersion")

    implementation("net.javacrumbs.shedlock:shedlock-spring:$shedlockVersion")
    implementation("net.javacrumbs.shedlock:shedlock-provider-jdbc-template:$shedlockVersion")

    implementation("org.springframework.cloud:spring-cloud-starter-sleuth:$springSleuthVersion")
    implementation("io.springfox:springfox-boot-starter:$springFoxVersion")

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

    implementation("org.redundent:kotlin-xml-builder:1.7.3")

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