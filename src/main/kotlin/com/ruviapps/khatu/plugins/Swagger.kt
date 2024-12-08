package com.ruviapps.khatu.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.*

fun Application.registerSwagger() {
    install(SwaggerUI) {
        info {
            title = "Calm API"
            version = "1.0.0"
        }
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
    routing {
        // Create a route for the swagger-ui using the openapi-spec at "/api.json".
        // This route will not be included in the spec.
        route("swagger") {
            swaggerUI("/api.json")
        }
        // Create a route for the openapi-spec file.
        // This route will not be included in the spec.
        route("api.json") {
            openApiSpec()
        }
    }
}