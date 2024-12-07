package com.ruviapps.khatu.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.ruviapps.calm.CalmController
import com.ruviapps.calm.CalmModel
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Application.configureRouting(
    /*shyamGroupService: ShyamGroupCrudService,
    carService: CarService*/
) {
    routing {
        route("/api/token") {
            get {
                
                val secret = application.environment.config.property("ktor.jwt.secret").getString()
                val issuer = application.environment.config.property("ktor.jwt.issuer").getString()
                val audience = application.environment.config.property("ktor.jwt.audience").getString()
                val expiry = application.environment.config.property("ktor.jwt.expiry").getString().toLong()
                val token = JWT.create()
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .withExpiresAt(Date(System.currentTimeMillis() + expiry)) // 1 day
                    .sign(Algorithm.HMAC256(secret))
                call.respond(hashMapOf("token" to token))
            }


        }

       /* authenticate("auth-jwt") {
            route("/api/group") {
                val controller = ShyamGroupCrudController(shyamGroupService)
                controller.registerRoutes(this)
               // shyamGroupRoutes(shyamGroupService)
            }
            route("/api/car") {
                carRoutes(carService)
            }
        }*/
    }
}

open class CalmRouter<T : CalmModel>(
    private val controller : CalmController<T>
){
    fun routeAll(application: Application) = application.routing {
        when(controller.authenticateRoute){
            true -> authenticate("auth-jwt") {
                route(controller.pluralizeBasePath) {
                    controller.registerRoutes(this)
                }
            }
            false -> route(controller.pluralizeBasePath) {
                controller.registerRoutes(this)
            }
        }
    }
}