plugins {
  `java-library`
  `maven-publish`
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.19"
  id("xyz.jpenilla.run-paper") version "3.0.2"
}

group = "fr.cel"
version = "1.4"

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
  mavenCentral()
}

dependencies {
  paperweight.paperDevBundle("1.21.10-R0.1-SNAPSHOT")

  compileOnly("org.projectlombok:lombok:1.18.42")
  annotationProcessor("org.projectlombok:lombok:1.18.42")

  testCompileOnly("org.projectlombok:lombok:1.18.42")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

  implementation("com.zaxxer:HikariCP:7.0.2")
  implementation("org.postgresql:postgresql:42.7.8")
}

paperweight.reobfArtifactConfiguration =
  io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

tasks.assemble {
  dependsOn(tasks.reobfJar)
}

tasks {
  compileJava {
    // Set the release flag. This configures what version bytecode the compiler will emit, as well as what JDK APIs are usable.
    // See https://openjdk.java.net/jeps/247 for more information.
    options.release = 21
  }
  javadoc {
    options.encoding = Charsets.UTF_8.name() // We want UTF-8 for everything
  }
}

publishing {
  publications {
    create<MavenPublication>("mavenJava") {
      from(components["java"])
    }
  }
  repositories {
    mavenLocal()
  }
}