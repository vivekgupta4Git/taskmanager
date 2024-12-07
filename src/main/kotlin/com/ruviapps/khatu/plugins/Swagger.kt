package com.ruviapps.khatu.plugins

import io.github.smiley4.ktorswaggerui.SwaggerUI
import io.github.smiley4.ktorswaggerui.data.AuthScheme
import io.github.smiley4.ktorswaggerui.data.AuthType
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureSwagger() {
    install(SwaggerUI) {
        info {
            title = "Ktor Koin template API docs"
            version = "latest"
        }

        security {
            securityScheme("auth-jwt"){
                type = AuthType.HTTP
                scheme = AuthScheme.BEARER
                description = "Type in the api key"
            }
        }
    }
    routing {
        route("swagger") {
            swaggerUI("/api.json")
        }
        route("api.json") {
            openApiSpec()
        }
    }
}