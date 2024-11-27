package com.ruviapps.khatu.routing

import com.ruviapps.khatu.domain.entity.ShyamPremiGroup
import com.ruviapps.khatu.domain.entity.toResponse
import com.ruviapps.khatu.request.ShyamPremiRequest
import com.ruviapps.khatu.request.toDomain
import com.ruviapps.khatu.service.ShyamGroupService
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.shyamGroupRoutes(
    service: ShyamGroupService
) {
    //Create Group
    post {
        try {
            val request = call.receive<ShyamPremiRequest>()
            val inserted = service.createGroup(request.toDomain())
            call.respond(HttpStatusCode.Created, inserted)
        } catch (e: NoTransformationFoundException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    //Get All
    get {
        val groups = service.findAll()
        if (groups.isEmpty())
            return@get call.respond(HttpStatusCode.NotFound)

        call.respond(HttpStatusCode.Found, groups)
    }
    //Get By id
    get("/{id}") {
        val id: String = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val found = service.findById(id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(
            HttpStatusCode.Found,
            found.toResponse()
        )
    }
    //delete by id
    delete("/{id}") {
        val id: String = call.parameters["id"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest)

        val deleted = service.deleteGroup(id) ?: return@delete call.respond(HttpStatusCode.NotModified)
        call.respond(
            HttpStatusCode.Accepted,
            deleted.toResponse()
        )
    }
    //update by id
    put("/{id}") {
        val id: String = call.parameters["id"]
            ?: return@put call.respond(HttpStatusCode.BadRequest)
        val group = call.receive<ShyamPremiGroup>()
        val updated = service.updateGroup(id, group) ?: return@put call.respond(HttpStatusCode.NotModified)

        call.respond(
            HttpStatusCode.Accepted,
            updated.toResponse()
        )
    }
}