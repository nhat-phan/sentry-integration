plugins {
    // "org.jetbrains.kotlin.jvm"
    kotlin("jvm") version "1.3.50" apply false

    // "org.jetbrains.kotlin.kapt"
    kotlin("kapt") version "1.3.50" apply false

    // "kotlinx-serialization"
    id("kotlinx-serialization") version "1.3.50" apply false

    id("org.jetbrains.intellij") version "0.4.12" apply false
}

subprojects {
    // An internal project which I use real life data to ensure the response's parsed correctly.
    // It contains private data so it is not added to git
    if (name == "internal-test") {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "kotlinx-serialization")
    }

    if (name == "sentry-integration") {
        apply(plugin = "org.jetbrains.kotlin.jvm")
        apply(plugin = "kotlinx-serialization")
    }

    if (name == "sentry-integration-idea") {
        apply(plugin = "org.jetbrains.intellij")
        apply(plugin = "org.jetbrains.kotlin.jvm")
    }
}