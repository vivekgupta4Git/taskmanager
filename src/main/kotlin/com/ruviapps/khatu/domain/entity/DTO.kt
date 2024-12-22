package com.ruviapps.khatu.domain.entity

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Updates
import com.ruviapps.calm.*
import com.ruviapps.calm.system.CalmGetDTO
import com.ruviapps.calm.system.CalmGetDTO.Companion.toGetDTO
import com.ruviapps.calm.system.CalmInsertDTO
import com.ruviapps.calm.system.CalmUpdateDTO
import com.ruviapps.khatu.util.toUTCString
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.bson.Document
import org.bson.conversions.Bson
import java.time.Instant
import java.util.*
import io.github.smiley4.ktorswaggerui.dsl.routing.get
import io.ktor.http.*

@Serializable
data class ShyamPremiGroupCalmGetDTO(
    val name: String? = null,
    val createdDate: String? = null,
    val joiningFee: Double? = null,
    val frequency: String? = null
) : CalmGetDTO()

@Serializable
data class ShyamPremiGroupCalmUpdateDTO(
    val name: String,
    val createdDate: String,
    val joiningFee: Double,
    val frequency: String
) : CalmUpdateDTO {
    override fun CalmUpdateDTO.toUpdates(): Bson {
        return Updates.combine(
            Updates.set("name", name),
            Updates.set("createdDate", createdDate),
            Updates.set("joiningFee", joiningFee),
            Updates.set("frequency", frequency)
        )
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class ShyamPremiGroupCalmInsertDTO constructor(
    val name: String,
    @EncodeDefault val joiningFee: Double = 0.0,
    @EncodeDefault val frequency: String = if (joiningFee == 0.0) "never" else "monthly",
    @EncodeDefault
    val createdDate: String = Instant.now().toUTCString(),
) : CalmInsertDTO {

    override fun CalmInsertDTO.toDocument(): Document {
        return Document.parse(jsonForInsertDTO().encodeToString(this))
    }
}


@Serializable
data class Car(
    val name: String? = null,
    val model: String? = null,
    val color: String? = null
) : CalmModel() {
    override fun CalmUpdateDTO.toUpdates(): Bson {
        return Updates.combine(
            Updates.set("name", name),
            Updates.set("model", model),
            Updates.set("color", color)
        )
    }

    override fun CalmInsertDTO.toDocument(): Document {
        return Document.parse(jsonForInsertDTO().encodeToString(this))
    }
}


class CarRepository(mongoDatabase: MongoDatabase) : CalmRepository<Car>(mongoDatabase, "MyCars") {
    override fun Car.insertDtoToDocument(): Document = toDocument()
    override fun Car.toUpdateBson(): Bson = toUpdates()
    override fun Document.documentToGetDTO(): Car = toGetDTO()
}

class CarService(carRepository: CarRepository) : CalmService<Car>(carRepository)

class CarController(service: CarService) : CalmController<Car>(
    modelName = "car",
    service = service,
    makePluralize = true,
    authenticateRoute = true
) {
    override fun insertDtoTypeOf(): TypeInfo = typeInfo<Car>()
    override fun updateDtoTypeOf(): TypeInfo = typeInfo<Car>()

    override fun getDtoTypeOf(): TypeInfo = typeInfo<Car>()


    override fun getListDtoTypeOf(): TypeInfo = typeInfo<List<Car>>()

    override fun customRoutes(route: Route, groupName : String) {

        with(route) {
            route("/api") {
                get("token", {
                    tags = listOf("Token")
                    description = "Get token"
                    response { HttpStatusCode.OK to { body<TokenResponse>() } }
                }) {
                    val secret = environment.config.property("ktor.jwt.secret").getString()
                    val issuer = environment.config.property("ktor.jwt.issuer").getString()
                    val audience = environment.config.property("ktor.jwt.audience").getString()
                    val expiry = environment.config.property("ktor.jwt.expiry").getString().toLong()
                    val token = JWT.create()
                        .withAudience(audience)
                        .withIssuer(issuer)
                        .withExpiresAt(Date(System.currentTimeMillis() + expiry)) // 1 day
                        .sign(Algorithm.HMAC256(secret))
                    call.respond(HttpStatusCode.OK, TokenResponse(token))
                }
            }
        }
    }

    override fun documentToDto(document: Document): Car {
        return document.toGetDTO()
    }
    @Serializable
    data class TokenResponse(val token : String)
}

class CarRouter(
    basePath: String,
    controller: CarController
) : CalmRouter<Car>(controller = controller, basePath = basePath, groupName = "Car Api")