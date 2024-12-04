package com.ruviapps.calm

import com.ruviapps.khatu.domain.entity.ShyamPremiGroupCalmInsertDTO
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass
import org.bson.Document

/**
 *  Every Insert Dto should extend this class and explicitly add in serializersModule
 *  @see [kotlinx.serialization.modules.polymorphic]
 */
@Serializable
abstract class CalmInsertDTO {
    /**
     * Enforcing Insert Dto to provide conversion to Document
     * @return [Document] a Bson Document
     */
    abstract fun CalmInsertDTO.toDocument(): Document

    /**
     * Helper method to provide conversion from Insert Dto to Document
     * use this method to provide conversion from Insert Dto to Document
     * @return [Json] for serialization/deserialization
     */
    open fun jsonForInsertDTO() = Json { serializersModule = getModule() }

    /**
     * Another Helper method to provide conversion from Document to Insert Dto
     * @return [SerializersModule] for polymorphic deserialization/serialization
     */
    open fun getModule(): SerializersModule {
        return SerializersModule {
            polymorphic(CalmInsertDTO::class) {
                //each Insert Dto which are extended from CalmInsertDTO need to explicitly be added here
                subclass(ShyamPremiGroupCalmInsertDTO::class)
            }
        }
    }


}
