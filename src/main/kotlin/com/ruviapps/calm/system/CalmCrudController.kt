package com.ruviapps.calm.system

import Inflector
import com.mongodb.client.model.Filters
import com.ruviapps.khatu.util.fixApiNaming
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
    val pluralizeName =
        if (moduleName.usePlural)
            Inflector.instance.pluralize(moduleName.name).fixApiNaming()
        else
            moduleName.name.fixApiNaming()

    abstract fun insertDtoTypeOf(): TypeInfo
    abstract fun updateDtoTypeOf(): TypeInfo
    abstract fun getDtoTypeOf(): TypeInfo
    abstract fun getListDtoTypeOf(): TypeInfo
    open fun defaultRoutes(route: Route, groupName : String) = with(route) {
        findAll(groupName)
        findById(groupName)
        findWhere(groupName)
        insert(groupName)
        updateOne(groupName)
        deleteOne(groupName)
        deleteWhere(groupName)
        deleteAll(groupName)
    }

    abstract fun customRoutes(route: Route, groupName: String)

    open fun Route.findAll(tag: String) = get(pluralizeName, {
        tags = listOf(tag)
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

    open fun Route.findById(tag: String) = get("$pluralizeName/{id}", {
        tags = listOf(tag)
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
            call.respond(HttpStatusCode.OK, result, getDtoTypeOf())
        else
            call.respond(HttpStatusCode.NotFound, "Not Found", typeInfo<String>())
    }

    open fun Route.insert(tag: String) = post(pluralizeName, {
        tags = listOf(tag)
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


    open fun Route.updateOne(tag: String) = put("${pluralizeName}/{id}", {
        tags = listOf(tag)
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

    open fun Route.deleteOne(tag: String) = delete("${pluralizeName}/{id}", {
        tags = listOf(tag)
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

    open fun Route.deleteAll(tag: String) = delete(pluralizeName, {
        tags = listOf(tag)
        description = "Delete all $pluralizeName"
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
        response { HttpStatusCode.NotFound to { body<String>() } }
    }) {
        val deletedCount = service.deleteAll()
        if(deletedCount == 0L)
            call.respond(HttpStatusCode.NotFound, "Not Found", typeInfo<String>())
        call.respond(HttpStatusCode.OK, deletedCount, typeInfo<Long>())
    }

    open fun Route.insertMany(tag: String) = post(pluralizeName, {
        tags = listOf(tag)
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

    open fun Route.deleteWhere(tag: String) = delete("${pluralizeName}/{field}/{value}", {
        tags = listOf(tag)
        description = "Delete by field and value"
        request { queryParameter<String>("field") }
        request { queryParameter<String>("value") }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.NotFound to { body<String>() } }
        response { HttpStatusCode.OK to { body<Long>() } }
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

    open fun Route.findWhere(tag: String) = get("${pluralizeName}/{field}/{value}", {
        tags = listOf(tag)
        description = "Find by field and value"
        request { queryParameter<String>("field") }
        request { queryParameter<String>("value") }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.NotFound to { body<String>() } }
        response { HttpStatusCode.OK to { body(KTypeDescriptor(getListDtoTypeOf().kotlinType!!)) } }
    }) {
        val field = call.parameters["field"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val value = call.parameters["value"] ?: return@get call.respond(HttpStatusCode.BadRequest)
        val filter = Filters.eq(field, value)
        val filteredModels = service.findWhere { filter }.map { documentToDto(it) }.toList()
        if (filteredModels.isEmpty())
            call.respond(HttpStatusCode.NotFound, "Not found", typeInfo<String>())
        else
            call.respond(HttpStatusCode.OK, filteredModels, getListDtoTypeOf())
    }

    abstract fun documentToDto(document: Document): GET_DTO
}