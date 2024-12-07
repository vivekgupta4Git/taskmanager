package com.ruviapps.calm

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

abstract class CalmRouter<T : CalmModel>(
    private val basePath : String,
    private val controller : CalmController<T>
){

    fun registerDefaultRoutesWithAuth(application: Application) = application.routing {
        when(controller.authenticateRoute){
            true -> authenticate("auth-jwt") {
                route(basePath) {
                    controller.defaultRoutes(this)
                }
            }
            false -> route(basePath) {
                controller.defaultRoutes(this)
            }
        }
    }

    fun addCustomRoutes(application: Application) = application.routing {
        route(basePath) {
            controller.customRoutes(this)
        }
    }
}