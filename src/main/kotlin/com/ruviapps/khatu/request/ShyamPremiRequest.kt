package com.ruviapps.khatu.request

import com.ruviapps.khatu.domain.entity.ShyamPremiGroup
import com.ruviapps.khatu.util.toUTCString
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ShyamPremiRequest(
    @SerialName("name")
    val name: String
)

fun ShyamPremiRequest.toDomain() = ShyamPremiGroup(
    name = name,
    createdDate = Instant.now().toUTCString(),
    joiningFee = 0.0,
    feeFrequency = "never",
    id = "",
)

