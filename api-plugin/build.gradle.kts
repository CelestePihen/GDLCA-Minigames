plugins {
  `java-library`
  id("io.papermc.paperweight.userdev") version "2.0.0-beta.17"
  `maven-publish`
}

group = "fr.cel"
version = "1.3"

java {
  // Configure the java toolchain. This allows gradle to auto-provision JDK 21 on systems that only have JDK 11 installed for example.
  toolchain.languageVersion = JavaLanguageVersion.of(21)
}

repositories {
  mavenCentral()
  flatDir {
    dirs("libs")
  }
}

dependencies {
  paperweight.paperDevBundle("1.21.8-R0.1-SNAPSHOT")

  compileOnly("org.projectlombok:lombok:1.18.42")
  annotationProcessor("org.projectlombok:lombok:1.18.42")

  testCompileOnly("org.projectlombok:lombok:1.18.42")
  testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

  implementation("com.zaxxer:HikariCP:6.3.3")
  implementation("org.postgresql:postgresql:42.7.8")

  compileOnly("com.comphenix.protocol:ProtocolLib:5.4.0")
}

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