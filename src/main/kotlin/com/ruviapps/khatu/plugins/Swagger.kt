package com.ruviapps.khatu.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.ktor.server.application.Application
import io.ktor.server.application.install

fun Application.registerSwagger() {
    install(SwaggerUI) {

        security {
            defaultSecuritySchemeNames = setOf("auth-jwt")
            securityScheme("auth-jwt") {
                type = AuthType.HTTP
                bearerFormat = "JWT"
                name = "auth-jwt"
                scheme = AuthScheme.BEARER
            }
        }
    }
}