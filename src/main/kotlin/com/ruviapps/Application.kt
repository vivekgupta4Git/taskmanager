package com.ruviapps

import com.ruviapps.khatu.domain.entity.ShyamPremiGroupInsertDTO
import com.ruviapps.khatu.domain.repository.ShyamGroupRepositoryImpl
import com.ruviapps.khatu.plugins.configureDatabases
import com.ruviapps.khatu.plugins.configureRouting
import com.ruviapps.khatu.plugins.configureSecurity
import com.ruviapps.khatu.plugins.configureSerialization
import com.ruviapps.khatu.service.ShyamGroupService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    // configureSockets()
    configureRequestLogging()
    configureSerialization()
    val database = configureDatabases()
    configureSecurity()
    val repository = ShyamGroupRepositoryImpl(database)
    val service = ShyamGroupService(repository)
    configureRouting(service)
    configureStatusPage()
    configureShyamGroupRequestValidation()
}

fun Application.configureShyamGroupRequestValidation() {
    install(RequestValidation) {
        validate<ShyamPremiGroupInsertDTO> { dto ->
            if (dto.name.isBlank()) {
                ValidationResult.Invalid("Name cannot be empty")
            } else
                ValidationResult.Valid
        }
    }
}

fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(hashMapOf("message" to cause.localizedMessage))
        }
    }
}

fun Application.configureRequestLogging() {
    install(createApplicationPlugin(name = "RequestLoggingPlugin") {
        onCall { call ->
            call.request.origin.apply {
                println("Request URL: $scheme://$localHost:$localPort$uri")
            }
        }
    })
}
