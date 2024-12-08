package com.ruviapps.calm.system

import Inflector
import com.mongodb.client.model.Filters
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.routing.delete
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.put
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import org.bson.Document

data class ModuleName(val name: String, val usePlural: Boolean = true)


abstract class CalmCrudController<INSERT_DTO : CalmInsertDTO, GET_DTO : CalmGetDTO, UPDATE_DTO : CalmUpdateDTO>(
    moduleName: ModuleName,
    val authenticateRoute: Boolean = true,
    private val service: CalmCrudService<INSERT_DTO, GET_DTO, UPDATE_DTO>
) {
    private val pluralizeName =
        if (moduleName.usePlural)
            Inflector.instance.pluralize(moduleName.name)
        else
            moduleName.name

    abstract fun insertDtoTypeOf(): TypeInfo
    abstract fun updateDtoTypeOf(): TypeInfo
    abstract fun getDtoTypeOf(): TypeInfo
    abstract fun getListDtoTypeOf(): TypeInfo
    fun defaultRoutes(route: Route) = with(route) {
        findAll()
        findById()
        findWhere()
        insert()
        updateOne()
        deleteOne()
        deleteWhere()
        deleteAll()
    }

    abstract fun customRoutes(route: Route)

    fun Route.findAll() = get(pluralizeName, {
        description = "Find all the $pluralizeName in the database"
        response { HttpStatusCode.OK to { body<ArrayList<CalmGetDTO>>() } }
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getListDtoTypeOf().kotlinType!!)) } }
    }) {
        val result = service.findAll()
        if (result?.isNotEmpty() == true) {
            call.respond(HttpStatusCode.OK, result, getListDtoTypeOf())
        } else {
            call.respond(HttpStatusCode.OK, emptyList<GET_DTO>(), getListDtoTypeOf())
        }
    }

    fun Route.findById() = get("$pluralizeName/{id}", {
        description = "Find $pluralizeName by id"
        request { queryParameter<String>("id") }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
        response { HttpStatusCode.NotFound to { body<String>() } }
    }) {
        val id: String = call.parameters["id"]
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Bad Request", typeInfo<String>())
        val result = service.findById(id)
        if (result != null)
            call.respond(HttpStatusCode.OK, result, getListDtoTypeOf())
        else
            call.respond(HttpStatusCode.NotFound, "Not Found", typeInfo<String>())
    }

    fun Route.insert() = post(pluralizeName, {
        description = "Insert $pluralizeName into the database"
        request { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) }
        response { HttpStatusCode.Created to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.InternalServerError to { body<String>() } }
    }) {
        try {
            val requestModel = call.receive<INSERT_DTO>(insertDtoTypeOf())
            val insertedDto = service.insert(requestModel)
            call.respond(HttpStatusCode.Created, insertedDto, getDtoTypeOf())
        } catch (ex: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, ex.message, typeInfo<String>())
        } catch (ex: MongoException) {
            call.respond(HttpStatusCode.InternalServerError, ex.message, typeInfo<String>())
        }
    }


    fun Route.updateOne() = put("${pluralizeName}/{id}", {
        description = "Update $pluralizeName by id"
        request { queryParameter<String>("id") }
        request { body(KTypeDescriptor(updateDtoTypeOf().kotlinType!!)) }
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
        response { HttpStatusCode.BadRequest to { body<String>() } }
    }) {
        val id = call.request.queryParameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
        val model = call.receive<UPDATE_DTO>(updateDtoTypeOf())
        val updatedDto = service.updateById(id, model)
        call.respond(HttpStatusCode.OK, updatedDto, getDtoTypeOf())
    }

    fun Route.deleteOne() = delete("${pluralizeName}/{id}", {
        description = "Delete $pluralizeName by id"
        request { queryParameter<String>("id") }
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
        response { HttpStatusCode.NotFound to { body<String>() } }
        response { HttpStatusCode.BadRequest to { body<String>() } }
    }) {
        val id = call.request.queryParameters["id"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        val deleted = service.deleteById(id)
        if(deleted == null)
            call.respond(HttpStatusCode.NotFound, "Not Found", typeInfo<String>())
        call.respond(HttpStatusCode.OK, deleted, getDtoTypeOf())
    }

    fun Route.deleteAll() = delete(pluralizeName, {
        description = "Delete all $pluralizeName"
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
        response { HttpStatusCode.NotFound to { body<String>() } }
    }) {
        val deletedCount = service.deleteAll()
        if(deletedCount == 0L)
            call.respond(HttpStatusCode.NotFound, "Not Found", typeInfo<String>())
        call.respond(HttpStatusCode.OK, deletedCount, typeInfo<Long>())
    }

    fun Route.insertMany() = post(pluralizeName, {
        description = "Insert many $pluralizeName into the database"
        request { body<List<INSERT_DTO>>() }
        response { HttpStatusCode.Created to { body<List<INSERT_DTO>>() } }
        response { HttpStatusCode.InternalServerError to { body<String>() } }
    }) {
        val requestModel: List<INSERT_DTO> = call.receive(insertDtoTypeOf())
        val result = service.insertMany(requestModel)
        if (result?.isNotEmpty() == true)
            call.respond(HttpStatusCode.Created, result, getListDtoTypeOf())
        else
            call.respond(HttpStatusCode.InternalServerError)
    }

    fun Route.deleteWhere() = delete("${pluralizeName}/{field}/{value}", {
        description = "Delete by field and value"
        request { queryParameter<String>("field") }
        request { queryParameter<String>("value") }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.NotFound to { body<String>() } }
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
    }) {
        val field = call.parameters["field"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        val value = call.parameters["value"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
        val filter = Filters.eq(field, value)
        val deleteRowCount = service.deleteWhere { filter }
        if (deleteRowCount == 0L)
            call.respond(HttpStatusCode.NotFound, "Not found", typeInfo<String>())
        else
            call.respond(HttpStatusCode.OK, deleteRowCount, typeInfo<Long>())
    }

    fun Route.findWhere() = get("${pluralizeName}/{field}/{value}", {
        description = "Find by field and value"
        request { queryParameter<String>("field") }
        request { queryParameter<String>("value") }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.NotFound to { body<String>() } }
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
    }) {
        val field = call.parameters["field"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val value = call.parameters["value"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val filter = Filters.eq(field, value)
        val filteredModels = service.findWhere { filter }.map { documentToDto(it) }.toList()
        if (filteredModels.toList().isEmpty())
            call.respond(HttpStatusCode.NotFound, "Not found", typeInfo<String>())
        else
            call.respond(HttpStatusCode.OK, filteredModels, getListDtoTypeOf())
    }

    abstract fun documentToDto(document: Document): GET_DTO
}