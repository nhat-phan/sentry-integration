import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val artifactGroup: String by project
val artifactVersion: String by project
val intellijVersion: String by project
val jvmTarget: String by project
val prettyTimeVersion: String by project
val fuelVersion: String by project
val jodaTimeVersion: String by project
val kotlinxSerializationRuntimeVersion: String by project

group = artifactGroup
version = artifactVersion

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.ocpsoft.prettytime:prettytime:$prettyTimeVersion")
    implementation("joda-time:joda-time:$jodaTimeVersion")
    implementation("com.github.kittinunf.fuel:fuel:$fuelVersion")

    compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:$kotlinxSerializationRuntimeVersion")

    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit"))
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
}