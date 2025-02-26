plugins {
    `java-library`
}

group = "net.unknownuser"
version = "1.0-SNAPSHOT"
description = "Java argument parsing library"

java.sourceCompatibility = JavaVersion.VERSION_23
java.targetCompatibility = java.sourceCompatibility

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

tasks.withType<Javadoc> {
    options.encoding = "UTF-8"
}
