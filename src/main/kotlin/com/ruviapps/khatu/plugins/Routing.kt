package com.ruviapps.khatu.plugins

import com.ruviapps.khatu.routing.shyamGroupRoutes
import com.ruviapps.khatu.service.ShyamGroupService
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    shyamGroupService : ShyamGroupService
) {
    routing {
            route("/api/group"){
                    shyamGroupRoutes(shyamGroupService)
            }
    }
}
