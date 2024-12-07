package com.ruviapps.khatu.domain.entity

import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Updates
import com.ruviapps.calm.*
import com.ruviapps.calm.system.CalmGetDTO
import com.ruviapps.calm.system.CalmGetDTO.Companion.toGetDTO
import com.ruviapps.calm.system.CalmInsertDTO
import com.ruviapps.calm.system.CalmUpdateDTO
import com.ruviapps.khatu.plugins.CalmRouter
import com.ruviapps.khatu.util.ListWrapperDto
import com.ruviapps.khatu.util.toUTCString
import io.ktor.server.routing.*
import io.ktor.util.reflect.*
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import org.bson.Document
import org.bson.conversions.Bson
import java.time.Instant

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
data class ShyamPremiGroupCalmInsertDTO  constructor(
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
    val name : String? = null,
    val model : String? = null,
    val color : String? = null
): CalmModel(){
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


class CarRepository(mongoDatabase: MongoDatabase) : CalmRepository<Car>(mongoDatabase,"MyCars"){
    override fun Car.insertDtoToDocument(): Document = toDocument()
    override fun Car.toUpdateBson(): Bson = toUpdates()
    override fun Document.documentToGetDTO(): Car = toGetDTO()
}

class CarService(carRepository: CarRepository) : CalmService<Car>(carRepository)

class CarController(carService: CarService) : CalmController<Car>(
    service = carService,
    modelName = "car",
    makePluralize = false,
    authenticateRoute = true,
){
    override fun Route.additionalRoutesForRegistration() {}
    override fun insertListDtoTypeOf(): TypeInfo  = typeInfo<ListWrapperDto<Car>>()
    override fun insertDtoTypeOf(): TypeInfo = typeInfo<Car>()
    override fun updateDtoTypeOf(): TypeInfo  = typeInfo<Car>()
    override fun getDtoTypeOf(): TypeInfo = typeInfo<Car>()
    override fun getListDtoTypeOf(): TypeInfo = typeInfo<ListWrapperDto<Car>>()
}

class CarRouter(
    carController: CarController
) : CalmRouter<Car>(controller = carController){

}