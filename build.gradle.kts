plugins {
    id("java")
    id("idea")
    id("org.openjfx.javafxplugin") version "0.0.12"
}

group = "com.github.adamtmalek"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "17"
    modules("javafx.base")
}

dependencies {
    implementation("commons-io:commons-io:2.11.0")
    implementation("org.jetbrains:annotations:20.1.0")
    implementation("org.jxmapviewer:jxmapviewer2:2.5")
    compileOnly("org.jetbrains:annotations:23.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.assertj:assertj-core:3.22.0")
    testImplementation("org.assertj:assertj-swing-junit:3.9.2")
    testImplementation("org.mockito:mockito-core:4.4.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}