package com.ruviapps.khatu.util

import kotlinx.serialization.Serializable

@Serializable
data class ListWrapperDto<T>(val data: List<T>)