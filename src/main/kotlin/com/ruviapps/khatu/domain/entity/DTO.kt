package com.ruviapps.khatu.domain.entity

import com.mongodb.client.model.Updates
import com.ruviapps.calm.CalmGetDTO
import com.ruviapps.calm.CalmInsertDTO
import com.ruviapps.calm.CalmUpdateDTO
import com.ruviapps.khatu.util.toUTCString
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

@Serializable
data class ShyamPremiGroupCalmInsertDTO @OptIn(ExperimentalSerializationApi::class) constructor(
    val name: String,
    @EncodeDefault val joiningFee: Double = 0.0,
    @EncodeDefault val frequency: String = if (joiningFee == 0.0) "never" else "monthly",
    @EncodeDefault
    val createdDate: String = Instant.now().toUTCString(),
) : CalmInsertDTO() {

    override fun CalmInsertDTO.toDocument(): Document {
        return Document.parse(jsonForInsertDTO().encodeToString(this))
    }
}