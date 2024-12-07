package com.ruviapps.khatu.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.ktor.server.application.Application
import io.ktor.server.application.install

fun Application.registerSwagger() {
    install(SwaggerUI) {}
}