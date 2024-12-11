package com.ruviapps.calm.system

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*

abstract class CalmCrudRouter<GET_DTO : CalmGetDTO, INSERT_DTO : CalmInsertDTO, UPDATE_DTO : CalmUpdateDTO>(
    private val basePath: String,
    private val tag : String,
    private val crudController: CalmCrudController<INSERT_DTO, GET_DTO, UPDATE_DTO>
) {
    open fun registerDefaultRoutesWithAuth(application: Application): RoutingRoot {
        return application.routing {
            when (crudController.authenticateRoute) {
                true -> authenticate("auth-jwt") {
                    route(basePath) {
                        crudController.defaultRoutes(this,tag)
                    }
                }

                false -> route(basePath) {
                    crudController.defaultRoutes(this,tag)
                }
            }
        }
    }

   open fun addCustomRoutes(application: Application): RoutingRoot {
        return application.routing {
            route(basePath) {
                crudController.customRoutes(this)
            }
        }
    }
}