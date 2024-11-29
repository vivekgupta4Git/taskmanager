package com.ruviapps

import com.ruviapps.khatu.domain.entity.ShyamPremiGroupInsertDTO
import com.ruviapps.khatu.domain.repository.ShyamGroupRepositoryImpl
import com.ruviapps.khatu.plugins.configureDatabases
import com.ruviapps.khatu.plugins.configureRouting
import com.ruviapps.khatu.plugins.configureSecurity
import com.ruviapps.khatu.plugins.configureSerialization
import com.ruviapps.khatu.service.ShyamGroupService
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
   // configureSockets()
    configureSerialization()
    val database = configureDatabases()
    configureSecurity()
    val repository = ShyamGroupRepositoryImpl(database)
    val service = ShyamGroupService(repository)
    configureRouting(service)
    configureShyamGroupRequestValidation()
}

fun Application.configureShyamGroupRequestValidation(){
    install(RequestValidation){
        validate<ShyamPremiGroupInsertDTO> {
           dto ->
            if(dto.name.isBlank()){
                ValidationResult.Invalid("Name cannot be empty")
            }else
                ValidationResult.Valid
        }
    }
}