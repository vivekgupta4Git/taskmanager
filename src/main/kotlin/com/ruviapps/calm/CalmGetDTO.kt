package com.ruviapps.calm

import com.ruviapps.khatu.util.ObjectIdSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.bson.Document

/**
 * A Base Class for Get Dto
 */
@Serializable
abstract class CalmGetDTO {
    /**
     * For MongoDB, it is the primary key and enforcing all subclasses to have this
     * field by default. It has in-built Serializer attached. @see [ObjectIdSerializer]
     */
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: String? = null

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
        /**
         * A helper method to provide conversion from Document to Get Dto
         * @return [T] a Get Dto
         */
        inline fun <reified T> Document.toGetDTO(): T = json.decodeFromString<T>(this.toJson())
    }
}