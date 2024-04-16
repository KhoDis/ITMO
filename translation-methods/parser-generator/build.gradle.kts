import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.21"
    application
    antlr
}

group = "ru.itmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    antlr("org.antlr:antlr4:4.11.1")
    implementation(gradleApi())
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    dependsOn(tasks.generateGrammarSource)
}

tasks.generateGrammarSource {
    maxHeapSize = "1024m"
    arguments = arguments + listOf("-visitor", "-long-messages")
}

application {
    mainClass.set("MainKt")
//    applicationDefaultJvmArgs = listOf("-cp", "src/main/meta")
}

sourceSets {
    main {
        kotlin {
            srcDir("src/main/meta")
        }
    }
}