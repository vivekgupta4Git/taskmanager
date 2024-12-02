package com.ruviapps.khatu.domain.entity

import com.mongodb.client.model.Updates
import com.ruviapps.khatu.util.ObjectIdSerializer
import com.ruviapps.khatu.util.toUTCString
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import org.bson.Document
import org.bson.conversions.Bson
import java.time.Instant
import javax.print.Doc

@Serializable
data class ShyamPremiGroupGetDTO(
    @SerialName("_id")
    @Serializable(with = ObjectIdSerializer::class)
    val id: String? = null,
    val name: String? = null,
    val createdDate: String? = null,
    val joiningFee: Double? = null,
    val frequency: String? = null
) {
    companion object {
        private val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

        fun Document.toGetDTO(): ShyamPremiGroupGetDTO = json.decodeFromString<ShyamPremiGroupGetDTO>(this.toJson())

    }
}

@Serializable
data class ShyamPremiGroupInsertDTO @OptIn(ExperimentalSerializationApi::class) constructor(
    val name: String,
    @EncodeDefault
    val joiningFee: Double = 0.0,
    @EncodeDefault
    val frequency: String = if (joiningFee == 0.0) "never" else "monthly",
    @EncodeDefault
    val createdDate: String = Instant.now().toUTCString(),
) {
    companion object {
        fun ShyamPremiGroupInsertDTO.toDocument(): Document = Document.parse(Json.encodeToString(this@toDocument))
    }
}

@Serializable
data class ShyamPremiGroupUpdateDTO(
    val name: String,
    val createdDate: String,
    val joiningFee: Double,
    val frequency: String
) {
    companion object {
        fun ShyamPremiGroupUpdateDTO.toUpdates(): Bson = Updates.combine(
            Updates.set("name", name),
            Updates.set("createdDate", createdDate),
            Updates.set("joiningFee", joiningFee),
            Updates.set("frequency", frequency)
        )
    }
}
