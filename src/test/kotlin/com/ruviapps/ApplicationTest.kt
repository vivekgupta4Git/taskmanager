package com.ruviapps

import com.ruviapps.khatu.domain.entity.CarRepository
import com.ruviapps.khatu.domain.entity.CarService
import com.ruviapps.khatu.domain.repository.ShyamGroupCrudRepositoryImpl
import com.ruviapps.khatu.plugins.configureDatabases
import com.ruviapps.khatu.plugins.configureRouting
import com.ruviapps.khatu.service.ShyamGroupCrudService
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            val database = configureDatabases()
            configureRouting()
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
