package com.ruviapps.khatu.response

import kotlinx.serialization.Serializable

@Serializable
data class ShyamPremiGroupResponse(
    val id: String?,
    val name: String,
    val createdDate: String,
    val joiningFee: Double,
    val feeFrequency: String
)
