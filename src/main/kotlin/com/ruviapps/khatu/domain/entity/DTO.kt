package com.ruviapps.khatu.domain.entity

import com.mongodb.client.model.Updates
import com.ruviapps.khatu.util.ObjectIdSerializer
import com.ruviapps.khatu.util.toUTCString
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.conversions.Bson
import java.time.Instant

@Serializable
abstract class GetBaseDTo {
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: String? = null

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        inline fun <reified T> Document.toGetDTO(): T = json.decodeFromString<T>(this.toJson())
    }
}

@Serializable
data class ShyamPremiGroupGetDTO(
    val name: String? = null,
    val createdDate: String? = null,
    val joiningFee: Double? = null,
    val frequency: String? = null
) : GetBaseDTo()

@Serializable
sealed class InsertBaseDTO {
    abstract fun InsertBaseDTO.toDocument(): Document
}

@Serializable
data class ShyamPremiGroupInsertDTO @OptIn(ExperimentalSerializationApi::class) constructor(
    val name: String,
    @EncodeDefault val joiningFee: Double = 0.0,
    @EncodeDefault val frequency: String = if (joiningFee == 0.0) "never" else "monthly",
    @EncodeDefault
    val createdDate: String = Instant.now().toUTCString(),
) : InsertBaseDTO() {

    override fun InsertBaseDTO.toDocument(): Document {
        return Document.parse(Json.encodeToString(this@ShyamPremiGroupInsertDTO))
    }
}

interface UpdateBaseDTO {
    fun UpdateBaseDTO.toUpdates(): Bson
}

@Serializable
data class ShyamPremiGroupUpdateDTO(
    val name: String,
    val createdDate: String,
    val joiningFee: Double,
    val frequency: String
) : UpdateBaseDTO {
    override fun UpdateBaseDTO.toUpdates(): Bson {
        return Updates.combine(
            Updates.set("name", name),
            Updates.set("createdDate", createdDate),
            Updates.set("joiningFee", joiningFee),
            Updates.set("frequency", frequency)
        )
    }
}
