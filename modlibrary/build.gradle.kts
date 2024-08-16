plugins {
    id("java")
    id("java-library")
}

dependencies {
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

repositories {
    mavenCentral()
}
