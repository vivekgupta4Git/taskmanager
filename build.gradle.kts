
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
    implementation("io.github.smiley4:ktor-swagger-ui:4.1.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
        exclude(group = "org.yaml", module = "snakeyaml")
    }
    implementation(libs.bundles.ktor)
    implementation(libs.bundles.mongodb)
    implementation(libs.firebase.auth.provider)
    implementation(libs.logback.classic)

    testImplementation(libs.kotlin.test.junit)
}
