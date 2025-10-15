plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    compileOnly("fr.cel:api-plugin:1.3")

    compileOnly("fr.cel:cache-cache-plugin:1.2")
    compileOnly("fr.cel:valocraft-plugin:1.0.1")
    compileOnly("fr.cel:pvp-plugin:1.0.3")
    compileOnly("fr.cel:parkour-plugin:1.0.1")
    compileOnly("fr.cel:halloween-event-plugin:1.0.0")
}

group = "fr.cel.hub"
version = "1.1"
description = "Hub"
java.sourceCompatibility = JavaVersion.VERSION_21

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

publishing {
    publications.create<MavenPublication>("maven") {
        from(components["java"])
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
