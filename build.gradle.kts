import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    var kotlin_version: String by extra
    kotlin_version = "1.2.30"

    repositories {
        mavenCentral()
    }
    dependencies {
        classpath(kotlinModule("gradle-plugin", kotlin_version))
    }
}

group = "org.moztw.telegram-bot"
version = "1.0.2-SNAPSHOT"

apply {
    plugin("java")
    plugin("kotlin")
}

val kotlin_version: String by extra

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlinModule("stdlib-jdk8", kotlin_version))
    implementation("commons-cli", "commons-cli", "1.4")
    implementation("org.telegram", "telegrambots", "3.6")
    testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.1.0")
}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
