package com.ruviapps

import com.mongodb.client.MongoDatabase
import com.ruviapps.calm.example.BookController
import com.ruviapps.calm.example.BookRepository
import com.ruviapps.calm.example.BookRouter
import com.ruviapps.calm.example.BookService
import com.ruviapps.calm.modules.user.UserController
import com.ruviapps.calm.modules.user.UserRepository
import com.ruviapps.calm.modules.user.UserRouter
import com.ruviapps.calm.modules.user.UserService
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
    configureCarApi(database)
    configureUserApi(database)
    configureBookApi(database)
    configureStatusPage()
    configureShyamGroupRequestValidation()
}

private fun Application.configureBookApi(database: MongoDatabase) {
    val bookRepo = BookRepository(database)
    val bookService = BookService(bookRepo)
    val bookController = BookController(bookService)
    val bookRouter = BookRouter("v1", bookController)
    bookRouter.registerDefaultRoutesWithAuth(this)
}

private fun Application.configureCarApi(database: MongoDatabase) {
    val carRepository = CarRepository(database)
    val carService = CarService(carRepository)
    val carController = CarController(carService)
    val carRouter = CarRouter("v1", carController)
    carRouter.addCustomRoutes(this )
    carRouter.registerDefaultRoutesWithAuth(this)
}

private fun Application.configureUserApi(database: MongoDatabase) {
    val userRepository = UserRepository(database)
    val userService = UserService(userRepository)
    val userController = UserController(userService)
    val userRouter = UserRouter("v1", userController)
    userRouter.registerDefaultRoutesWithAuth(this)
    userRouter.addCustomRoutes(this)
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
