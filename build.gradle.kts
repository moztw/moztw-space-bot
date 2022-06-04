import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    var kotlinVersion: String by extra
    kotlinVersion = "1.6.0"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlin("gradle-plugin", kotlinVersion))
    }
}

group = "org.moztw.telegram-bot"
version = "1.1.1-SNAPSHOT"

apply {
    plugin("java")
    plugin("kotlin")
}

val kotlinVersion: String by extra

val implementation by configurations
val testImplementation by configurations

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8", kotlinVersion))
    implementation("commons-cli", "commons-cli", "1.5.0")
    implementation("org.telegram", "telegrambots", "5.5.0")
    implementation("com.squareup.okhttp3", "okhttp", "4.9.3")
    implementation("com.beust", "klaxon", "5.5")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.8.2")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
