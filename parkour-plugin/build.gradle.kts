plugins {
    `java-library`
    `maven-publish`
}

repositories {
    mavenLocal()
    mavenCentral();
    maven {
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.10-R0.1-SNAPSHOT")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    compileOnly("fr.cel:api-plugin:1.4")
}

group = "fr.cel"
version = "1.0.1"
description = "Parkour"
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
