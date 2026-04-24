plugins {
    id("java")
    id("war")
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //servlet
    compileOnly("jakarta.servlet:jakarta.servlet-api:6.1.0")
    implementation("jakarta.servlet.jsp.jstl:jakarta.servlet.jsp.jstl-api:3.0.0")
    implementation("org.glassfish.web:jakarta.servlet.jsp.jstl:3.0.1")

    //database
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("org.elasticsearch:elasticsearch:5.6.4")
    implementation("org.elasticsearch.client:transport:5.6.4")

    //cryptography
    implementation("org.mindrot:jbcrypt:0.4")
}

tasks.test {
    useJUnitPlatform()
}