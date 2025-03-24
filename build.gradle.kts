plugins {
    id("groovy")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "com.example"
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
    // Spring Boot 核心依賴
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.session:spring-session-core")

    // Groovy 支援
    implementation("org.apache.groovy:groovy:4.0.23")

    // Hibernate 核心依賴（替換 spring-boot-starter-data-jpa）
    implementation("org.hibernate:hibernate-core:6.6.1.Final")
    implementation("org.springframework:spring-orm:6.1.14")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // MySQL 驅動
    runtimeOnly("com.mysql:mysql-connector-j:8.4.0")

    // 開發工具
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // 測試依賴
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}