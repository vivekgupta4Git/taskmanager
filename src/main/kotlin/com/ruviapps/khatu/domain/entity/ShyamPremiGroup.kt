package com.ruviapps.khatu.domain.entity

import com.ruviapps.khatu.response.ShyamPremiGroupResponse
import com.ruviapps.khatu.util.ObjectIdSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.bson.Document

@Serializable
data class ShyamPremiGroup(
    @Serializable(with = ObjectIdSerializer::class)
    val id : String?,
    val name: String,
    val createdDate: String,
    val joiningFee: Double,
    val feeFrequency: String
) {
    companion object {
        private val json = Json { ignoreUnknownKeys = true }
        fun ShyamPremiGroup.toDocument(): Document = Document.parse(Json.encodeToString(this))
        fun Document.toShyamPremiGroup() : ShyamPremiGroup = json.decodeFromString(this.toJson())

    }
}

fun ShyamPremiGroup.toResponse() = ShyamPremiGroupResponse(
    id = id,
    name = name,
    createdDate = createdDate,
    joiningFee = joiningFee,
    feeFrequency = feeFrequency
)
