plugins {
    id("java")
    id("application")
}

application {
    mainClass = "es.ylabs.clijavamodloader.App"
}

dependencies {
    implementation(libs.snakeyaml)
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

tasks.named<JavaExec>("run") {
    standardInput = System.`in`
}

tasks {
    val standaloneJar = register<Jar>("standaloneJar") {
        dependsOn.addAll(listOf(":modlibrary:jar", "compileJava", "processResources"))

        archiveClassifier = "standalone"

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        manifest {
            attributes(mapOf("Main-Class" to application.mainClass))
        }

        val sources = sourceSets.main.get()
        val contents = configurations.runtimeClasspath.get()
            .map { if (it.isDirectory) it else zipTree(it) } + sources.output
        from(contents)
    }

    build {
        dependsOn("standaloneJar")
    }
}
