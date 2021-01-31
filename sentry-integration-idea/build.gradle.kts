import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val artifactGroup: String by project
val artifactVersion: String by project
val intellijVersion: String by project
val jvmTarget: String by project
val prettyTimeVersion: String by project
val intellijSinceBuild: String by project
val intellijUntilBuild: String by project

group = artifactGroup
version = artifactVersion

repositories {
    jcenter()
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.ocpsoft.prettytime:prettytime:$prettyTimeVersion")

    implementation(project(":sentry-integration"))
}

// See https://github.com/JetBrains/gradle-intellij-plugin/
intellij {
    version = intellijVersion
    updateSinceUntilBuild = true
}

val compileKotlin: KotlinCompile by tasks
val compileTestKotlin: KotlinCompile by tasks

compileKotlin.kotlinOptions {
    jvmTarget = jvmTarget
}

compileTestKotlin.kotlinOptions {
    jvmTarget = jvmTarget
}

tasks {
    named<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>("compileKotlin") {
        kotlinOptions {
            jvmTarget = jvmTarget
        }
    }

    named<org.jetbrains.intellij.tasks.PatchPluginXmlTask>("patchPluginXml") {
        val parts = artifactVersion.split('.')
        val mainVersion = "${parts[0]}.${parts[1]}"
        changeNotes(htmlFixer("./sentry-integration-idea/doc/release-notes.$mainVersion.html"))
        pluginDescription(htmlFixer("./sentry-integration-idea/doc/description.html"))
        sinceBuild(intellijSinceBuild)
        untilBuild(intellijUntilBuild)
    }
}

fun htmlFixer(filename: String): String {
    if (!File(filename).exists()) {
        throw Exception("File $filename not found.")
    }
    return File(filename).readText().replace("<html lang=\"en\">", "").replace("</html>", "")
}