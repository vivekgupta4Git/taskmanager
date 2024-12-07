package com.ruviapps.khatu.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ruviapps.khatu.domain.entity.CarService
import com.ruviapps.khatu.routing.carRoutes
import com.ruviapps.khatu.routing.shyamGroupRoutes
import com.ruviapps.khatu.service.ShyamGroupCrudService
import io.github.smiley4.ktorswaggerui.routing.openApiSpec
import io.github.smiley4.ktorswaggerui.routing.swaggerUI
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureRouting(
    shyamGroupService: ShyamGroupCrudService,
    carService: CarService
) {
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
        route("/api/token") {
            get {
                val secret = environment.config.property("ktor.jwt.secret").getString()
                val issuer = environment.config.property("ktor.jwt.issuer").getString()
                val audience = environment.config.property("ktor.jwt.audience").getString()
                val expiry = environment.config.property("ktor.jwt.expiry").getString().toLong()
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withExpiresAt(Date(System.currentTimeMillis() + expiry)) // 1 day
                    .sign(Algorithm.HMAC256(secret))
                call.respond(hashMapOf("token" to token))
            }


        }
        /*authenticate("auth-jwt") {
            route("/api/group") {
                shyamGroupRoutes(shyamGroupService)
            }
            route("/api/car") {
                carRoutes(carService)
            }
        }*/
    }
}