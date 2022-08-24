import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.7.10"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

group = "org.moztw.telegram-bot"
version = "1.1.3-SNAPSHOT"

apply {
    plugin("java")
    plugin("kotlin")
}

val kotlinVersion: String by extra

val implementation by configurations
val testImplementation by configurations

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("commons-cli", "commons-cli", "1.5.0")
    implementation("org.telegram", "telegrambots", "6.1.0")
    implementation("com.squareup.okhttp3", "okhttp", "4.10.0")
    implementation("com.beust", "klaxon", "5.5")
    implementation("io.prometheus", "simpleclient", "0.16.0")
    implementation("io.prometheus", "simpleclient_hotspot", "0.16.0")
    implementation("io.prometheus", "simpleclient_httpserver", "0.16.0")
    implementation("org.slf4j", "slf4j-api", "2.0.0")
    implementation("org.slf4j", "slf4j-simple", "2.0.0")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.8.2")
}

configure<JavaPluginExtension> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
