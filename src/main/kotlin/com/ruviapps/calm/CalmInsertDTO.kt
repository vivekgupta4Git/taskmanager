package com.ruviapps.calm

import com.ruviapps.khatu.domain.entity.Car
import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmInsertDTO
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bson.Document

/**
 *  Every Insert Dto should extend this interface and explicitly add itself to serializersModule
 *  @see [kotlinx.serialization.modules.polymorphic]
 */
interface CalmInsertDTO {
    /**
     * Enforcing Insert Dto to provide conversion to Document
     * @return [Document] a Bson Document
     */
     fun CalmInsertDTO.toDocument(): Document

    /**
     * Helper method to provide conversion from Insert Dto to Document
     * use this method to provide conversion from Insert Dto to Document
     *
     * example :
     *             val document = jsonForInsertDTO().encodeToString(insertDto)
     *
     * @return [Json] for serialization/deserialization
     */
     fun jsonForInsertDTO() = Json { serializersModule = getModule() }

    /**
     * Another Helper method to provide conversion from Document to Insert Dto
     * @return [SerializersModule] for polymorphic deserialization/serialization
     */
     fun getModule(): SerializersModule {
        return SerializersModule {
            polymorphic(CalmInsertDTO::class) {
                //each Insert Dto which are extended from CalmInsertDTO need to explicitly be added here
                subclass(ShyamPremiGroupCalmInsertDTO::class)
                subclass(Car::class)
            }
        }
    }


}
