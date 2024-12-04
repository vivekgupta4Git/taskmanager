
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.ruviapps"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":kolor"))
    implementation(project(":inflector"))
    implementation(libs.swagger)
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.mongodb)
    implementation(libs.firebase.auth.provider)
    implementation(libs.logback.classic)

    testImplementation(libs.kotlin.test.junit)
}
