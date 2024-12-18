package com.ruviapps

import com.ruviapps.calm.example.BookController
import com.ruviapps.calm.example.BookRepository
import com.ruviapps.calm.example.BookRouter
import com.ruviapps.calm.example.BookService
import com.ruviapps.khatu.domain.entity.*
import com.ruviapps.khatu.domain.repository.ShyamGroupCrudRepositoryImpl
import com.ruviapps.khatu.plugins.*
import com.ruviapps.khatu.service.ShyamGroupCrudService
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
    registerSwagger()
    val database = configureDatabases()
    configureSecurity()
    val carRepository = CarRepository(database)
    val carService = CarService(carRepository)
    val carController = CarController(carService)
    val carRouter = CarRouter("v1",carController)
    carRouter.registerDefaultRoutesWithAuth(this)
    carRouter.addCustomRoutes(this)

    val bookRepo = BookRepository(database)
    val bookService = BookService(bookRepo)
    val bookController = BookController(bookService)
    val bookRouter = BookRouter("v1",bookController)
    bookRouter.registerDefaultRoutesWithAuth(this)
    configureStatusPage()
    configureShyamGroupRequestValidation()
}

fun Application.configureShyamGroupRequestValidation() {
    install(RequestValidation) {
        validate<ShyamPremiGroupCalmInsertDTO> { dto ->
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
