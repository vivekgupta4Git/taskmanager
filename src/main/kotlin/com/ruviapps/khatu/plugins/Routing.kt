package com.ruviapps.khatu.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ruviapps.khatu.routing.shyamGroupRoutes
import com.ruviapps.khatu.service.ShyamGroupService
import com.sun.org.apache.xml.internal.security.algorithms.JCEMapper
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureRouting(
    shyamGroupService : ShyamGroupService
) {
    routing {
        route("/api/token"){
            get {
                val secret = environment.config.property("ktor.jwt.secret").getString()
                val issuer = environment.config.property("ktor.jwt.issuer").getString()
                val audience = environment.config.property("ktor.jwt.audience").getString()
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withExpiresAt(Date(System.currentTimeMillis() + 3600000 * 24)) // 1 day
                    .sign(Algorithm.HMAC256(secret))
                call.respond(hashMapOf("token" to token))
            }


        }
        authenticate("auth-jwt") {
            route("/api/group") {
                shyamGroupRoutes(shyamGroupService)
            }
        }
    }
}
