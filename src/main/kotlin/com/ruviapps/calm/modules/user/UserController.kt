package com.ruviapps.calm.modules.user

import com.ruviapps.calm.CalmController
import com.ruviapps.calm.system.CalmGetDTO.Companion.toGetDTO
import com.ruviapps.calm.system.MongoException
import io.github.smiley4.ktorswaggerui.data.KTypeDescriptor
import io.github.smiley4.ktorswaggerui.dsl.routing.post
import io.github.smiley4.ktorswaggerui.dsl.routing.put
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import org.bson.Document

class UserController(
    private val service: UserService
) : CalmController<User>(
    modelName = "User",
    service = service,
    makePluralize = true,
    authenticateRoute = true
) {
    override fun insertDtoTypeOf(): TypeInfo = typeInfo<User>()
    override fun updateDtoTypeOf(): TypeInfo = typeInfo<User>()
    override fun getDtoTypeOf(): TypeInfo = typeInfo<User>()
    override fun getListDtoTypeOf(): TypeInfo = typeInfo<List<User>>()
    override fun documentToDto(document: Document): User {
        return document.toGetDTO()
    }

    override fun customRoutes(route: Route, groupName: String) {
        with(route) {
            authenticate("auth-jwt") {
                put("/$pluralizeName/update-password/{id}", {
                    tags = listOf(groupName)
                    description = "Update password"
                    request {
                        queryParameter<String>("id")
                    }
                    request {
                        body<UserPasswordUpdate>()
                    }
                    response {
                        HttpStatusCode.OK to { body<String>() }
                    }
                    response {
                        HttpStatusCode.BadRequest to { body<String>() }
                    }
                    response {
                        HttpStatusCode.NotFound to { body<String>() }
                    }
                    response {
                        HttpStatusCode.InternalServerError to { body<String>() }
                    }
                }) {
                    try {
                        val id =
                            call.request.queryParameters["id"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                        val newPassword = call.receive<UserPasswordUpdate>()
                        val found = service.findById(id)
                        if (found == null)
                            call.respond(HttpStatusCode.NotFound, "User Not Found", typeInfo<String>())
                        else {
                            val passwordHash = found.getBcryptHashString(newPassword.password)
                            val updated = service.updatePassword(id, passwordHash)
                            if(updated == null)
                                call.respond(HttpStatusCode.InternalServerError, "Update Failed", typeInfo<String>())
                            else
                                call.respond(HttpStatusCode.OK, "Password Updated", typeInfo<String>())
                        }
                    } catch (ex: Exception) {
                        call.respond(HttpStatusCode.InternalServerError, ex.message, typeInfo<String>())
                    }
                }
            }
        }
    }

    override fun Route.insert(tag: String) = post(pluralizeName, {
        tags = listOf(tag)
        description = "Insert $pluralizeName into the database"
        request { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) }
        response { HttpStatusCode.Created to { body(KTypeDescriptor(getDtoTypeOf().kotlinType!!)) } }
        response { HttpStatusCode.BadRequest to { body<String>() } }
        response { HttpStatusCode.InternalServerError to { body<String>() } }
    }) {
        try {
            val requestModel = call.receive<User>(insertDtoTypeOf())
            val passwordHash = requestModel.getBcryptHashString(requestModel.passwordHash)
            val insertedDto = service.insert(
                requestModel.copy(
                    passwordHash = passwordHash
                )
            )
            call.respond(HttpStatusCode.Created, insertedDto, getDtoTypeOf())
        } catch (ex: ContentTransformationException) {
            call.respond(HttpStatusCode.BadRequest, ex.message, typeInfo<String>())
        } catch (ex: MongoException) {
            call.respond(HttpStatusCode.InternalServerError, ex.message, typeInfo<String>())
        }
    }

    override fun Route.findAll(tag: String) = get {
        //blank implementation for security purpose
    }
}