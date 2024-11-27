package com.ruviapps

import com.ruviapps.khatu.domain.repository.ShyamGroupRepositoryImpl
import com.ruviapps.khatu.plugins.configureDatabases
import com.ruviapps.khatu.plugins.configureRouting
import com.ruviapps.khatu.plugins.configureSerialization
import com.ruviapps.khatu.service.ShyamGroupService
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
   // configureSockets()
    configureSerialization()
    val database = configureDatabases()
   // configureSecurity()
    val repository = ShyamGroupRepositoryImpl(database)
    val service = ShyamGroupService(repository)
    configureRouting(service)
}
