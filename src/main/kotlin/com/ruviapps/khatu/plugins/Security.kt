package com.ruviapps.khatu.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.kborowy.authprovider.firebase.firebase
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.*
import java.io.File

fun Application.configureSecurity() {
    val myRealm = environment.config.property("ktor.jwt.realm").getString()
    val secret = environment.config.property("ktor.jwt.secret").getString()
    val issuer = environment.config.property("ktor.jwt.issuer").getString()
    val audience = environment.config.property("ktor.jwt.audience").getString()
    install(Authentication) {
    /*        firebase {
            adminFile = File("path/to/admin/file.json")
            realm = "My Server"
            validate { token ->
                // MyAuthenticatedUser(id = token.uid)
            }
        }
        bearer("auth-bearer") {
            realm = "Access to the '/' path"
            authenticate { tokenCredential ->
                if (tokenCredential.token == "abc123") {
                    UserIdPrincipal("jetbrains")
                } else {
                    null
                }
            }
        }*/
        jwt("auth-jwt"){
            realm = myRealm
            verifier(JWT
                .require(Algorithm.HMAC256(secret))
                .withAudience(audience)
                .withIssuer(issuer)
                .build())

            validate { credential ->
                val expiration = credential.payload.expiresAt?.time ?: 0
                if (expiration > System.currentTimeMillis()) {
                    JWTPrincipal(credential.payload) // Token is valid
                } else {
                    null // Token is expired or invalid
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized,
                    "Token is not valid")
            }
        }
    }
}
