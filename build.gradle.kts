plugins {
    kotlin("jvm") version "2.3.0"
    application
    id("com.gradleup.shadow") version "9.3.0"
}

group = "me.italoarmenti"
version = "2.2-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.opencsv:opencsv:5.6")
    implementation("io.ktor:ktor-client-core:1.6.7")
    implementation("io.ktor:ktor-client-apache:1.6.7")
    implementation("io.ktor:ktor-client-gson:1.6.7")
    implementation("com.xenomachina:kotlin-argparser:2.0.7")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:2.3.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.3.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.8.2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

// create executable with :./gradlew clean installShadowDist
tasks.shadowJar {
    archiveBaseName.set("staking_calculator")
    mergeServiceFiles()
}

application {
    mainClass.set("MainKt")
}
