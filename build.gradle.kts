import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    application
    id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "me.italoarmenti"
version = "2.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.opencsv:opencsv:5.6")
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-apache:1.6.7")
    implementation("io.ktor:ktor-client-gson:1.6.7")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.6.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:1.6.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "1.8"
}

// create executable with :./gradlew clean installShadowDist
tasks {
    named<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
        archiveBaseName.set("staking_calculator")
        mergeServiceFiles()
    }
}

application {
    mainClass.set("MainKt")
}

// Required by the 'shadowJar' task
project.setProperty("mainClassName", "MainKt")
