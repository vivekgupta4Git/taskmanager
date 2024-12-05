package com.ruviapps.calm.system

import Inflector
import com.mongodb.client.model.Filters
import io.github.smiley4.ktorswaggerui.dsl.get
import io.github.smiley4.ktorswaggerui.dsl.post
import io.github.smiley4.ktorswaggerui.dsl.put
import io.github.smiley4.ktorswaggerui.dsl.delete
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*

data class ModuleName(val name: String, val usePlural: Boolean = true)


abstract class CalmCrudController<INSERT_DTO : CalmInsertDTO, GET_DTO : CalmGetDTO, UPDATE_DTO : CalmUpdateDTO>(
    moduleName: ModuleName,
    val authenticateRoute: Boolean = true,
    private val service: CalmCrudService<INSERT_DTO, GET_DTO, UPDATE_DTO>
) {
    private val pluralizeName =
        if (moduleName.usePlural) Inflector.instance.pluralize(moduleName.name) else moduleName.name

    abstract fun insertDtoTypeOf(): TypeInfo
    abstract fun updateDtoTypeOf(): TypeInfo
    abstract fun getDtoTypeOf(): TypeInfo
    abstract fun getListDtoTypeOf(): TypeInfo

    fun Route.findAll() = get("/$pluralizeName", {
        response { HttpStatusCode.OK to { body(getListDtoTypeOf().type) } }
    }) {
        val result = service.findAll()
        if (result?.isNotEmpty() == true)
            call.respond(HttpStatusCode.Found, getListDtoTypeOf().type)
        else
            call.respond(HttpStatusCode.OK, getListDtoTypeOf().type)
    }

    fun Route.findById() = get("/$pluralizeName/{id}", {
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.Found to { body(getDtoTypeOf().type) } }
        response { HttpStatusCode.NotFound to { body<String>() } }
    }) {
        val id: String = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, typeInfo<String>())
        val result = service.findById(id)
        if (result != null)
            call.respond(HttpStatusCode.Found, getListDtoTypeOf().type)
        else
            call.respond(HttpStatusCode.NotFound, typeInfo<String>())
    }

    fun Route.insert() = post("/${pluralizeName}", {
        request { body(insertDtoTypeOf().type) }
        response { HttpStatusCode.Created to { body(getDtoTypeOf().type) } }
    }) {
        val requestModel = call.receive<INSERT_DTO>(insertDtoTypeOf())
        service.insert(requestModel)
        call.respond(HttpStatusCode.Created, getListDtoTypeOf().type)
    }


    fun Route.updateOne() = put("/${pluralizeName}/{id}", {
        request { queryParameter<String>("id") }
        request { body(updateDtoTypeOf().type) }
        response { HttpStatusCode.Accepted to { body(getDtoTypeOf().type) } }
        response { HttpStatusCode.BadRequest to { body<String>() } }
    }) {
        val id = call.request.queryParameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val model = call.receive<UPDATE_DTO>(updateDtoTypeOf())
        service.updateById(id, model)
        call.respond(HttpStatusCode.Accepted, getListDtoTypeOf().type)
    }

    fun Route.deleteOne() = delete("/${pluralizeName}", {
        request { queryParameter<String>("id") }
        response { HttpStatusCode.Accepted to { body(getDtoTypeOf().type) } }
        response { HttpStatusCode.BadRequest to { body<String>() } }
    }) {
        val id = call.request.queryParameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        service.deleteById(id)
        call.respond(HttpStatusCode.Accepted, getListDtoTypeOf().type)
    }

    fun Route.deleteAll() = delete("/${pluralizeName}", {
        response { HttpStatusCode.Accepted to { body(getDtoTypeOf().type) } }
    }) {
        service.deleteAll()
        call.respond(HttpStatusCode.Accepted, getListDtoTypeOf().type)
    }

    fun Route.insertMany() = post("/${pluralizeName}", {
        request { body<List<INSERT_DTO>>() }
        response { HttpStatusCode.Created to { body<List<INSERT_DTO>>() } }
        response { HttpStatusCode.InternalServerError to { body<String>() } }
    }) {
        val requestModel: List<INSERT_DTO> = call.receive(insertDtoTypeOf())
        val result = service.insertMany(requestModel)
        if (result?.isNotEmpty() == true)
            call.respond(HttpStatusCode.Created, getListDtoTypeOf().type)
        else
            call.respond(HttpStatusCode.InternalServerError)
    }

    fun Route.deleteWhere() = delete("/${pluralizeName}/{field}/{value}", {
        request { queryParameter<String>("field") }
        request { queryParameter<String>("value") }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.NotFound to { body<String>() } }
        response { HttpStatusCode.Accepted to { body(getDtoTypeOf().type) } }
    }) {
        val field = call.parameters["field"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        val value = call.parameters["value"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        val filter = Filters.eq(field, value)
        val deleteRowCount = service.deleteWhere { filter }
        if (deleteRowCount == 0L)
            call.respond(HttpStatusCode.NotFound, typeInfo<String>())
        else
            call.respond(HttpStatusCode.Accepted, getListDtoTypeOf().type)
    }

    fun Route.findWhere() = put("/${pluralizeName}/{field}/{value}", {
        request { queryParameter<String>("field") }
        request { queryParameter<String>("value") }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.NotFound to { body<String>() } }
        response { HttpStatusCode.Found to { body(getDtoTypeOf().type) } }
    }) {
        val field = call.parameters["field"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val value = call.parameters["value"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val filter = Filters.eq(field, value)
        val filteredModels = service.findWhere { filter }
        if (filteredModels.toList().isEmpty())
            call.respond(HttpStatusCode.NotFound, typeInfo<String>())
        else
            call.respond(HttpStatusCode.Found, getListDtoTypeOf().type)
    }

}

