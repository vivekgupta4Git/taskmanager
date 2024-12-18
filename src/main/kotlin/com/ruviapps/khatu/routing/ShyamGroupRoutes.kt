package com.ruviapps.khatu.routing

import com.ruviapps.khatu.domain.entity.Car
import com.ruviapps.khatu.domain.entity.CarService
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmInsertDTO
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmUpdateDTO
import com.ruviapps.khatu.service.ShyamGroupCrudService
import io.ktor.client.call.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

fun Route.shyamGroupRoutes(
    service: ShyamGroupCrudService
) {
    //Create Group
    post {
        try {
            val request = call.receive<ShyamPremiGroupCalmInsertDTO>()
            val inserted = service.createGroup(request) ?: return@post call.respond(HttpStatusCode.Conflict)
            call.respond(inserted)
        } catch (e: NoTransformationFoundException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    //Get All
    get {
        val groups = service.findAll()
        if (groups?.isNotEmpty() == true)
            call.respond(HttpStatusCode.Found, groups)
        else
            call.respond(HttpStatusCode.NotFound)
    }
    //Get By id
    get("/{id}") {
        val id: String = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest)

        val found = service.findById(id)
            ?: return@get call.respond(HttpStatusCode.NotFound)

        call.respond(
            HttpStatusCode.Found,
            found
        )
    }
    //delete by id
    delete("/{id}") {
        val id: String = call.parameters["id"]
            ?: return@delete call.respond(HttpStatusCode.BadRequest)

        val deleted = service.deleteGroup(id) ?: return@delete call.respond(HttpStatusCode.NotModified)
        call.respond(
            HttpStatusCode.Accepted,
            deleted
        )
    }
    //update by id
    put("/{id}") {
        val id: String = call.parameters["id"]
            ?: return@put call.respond(HttpStatusCode.BadRequest)
        val group = call.receive<ShyamPremiGroupCalmUpdateDTO>()
        val updated = service.updateGroup(id, group) ?: return@put call.respond(HttpStatusCode.NotModified)

        call.respond(
            HttpStatusCode.Accepted,
            updated
        )
    }
    delete {
        val deleted = service.deleteAll()
        if (deleted > 0L)
            call.respond(HttpStatusCode.OK)
        else
            call.respond(HttpStatusCode.NotModified)
    }
}

fun Route.carRoutes(
    service: CarService
) {
    //Create Car
    post {
        try {
            val request = call.receive<Car>()
            val inserted = service.insert(request) ?: return@post call.respond(HttpStatusCode.Conflict)
            call.respond(inserted)
        } catch (e: NoTransformationFoundException) {
            call.respond(HttpStatusCode.BadRequest)
        }
    }
    get {
        val cars = service.findAll()
        if (cars?.isNotEmpty() == true)
            call.respond(HttpStatusCode.Found, cars)
        else
            call.respond(HttpStatusCode.NotFound)
    }

}