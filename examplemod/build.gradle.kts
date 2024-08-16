plugins {
    id("java")
    id("java-library")
}

dependencies {
    implementation(project(":modlibrary"))
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(libs.versions.java.get())
    }
}

repositories {
    mavenCentral()
}
