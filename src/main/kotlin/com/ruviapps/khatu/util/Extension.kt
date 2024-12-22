package com.ruviapps.khatu.util

import com.mongodb.client.result.InsertOneResult
import java.time.Instant
import java.time.ZoneId
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter

fun InsertOneResult.getObjectIdAsString(): String? = insertedId?.asObjectId()?.value?.toHexString()

fun Instant.toUTCString(): String = IsoChronology
    .INSTANCE
    .zonedDateTime(
        this,
        ZoneId.of("UTC")
    )
    .format(DateTimeFormatter.ISO_DATE_TIME)


fun String.fixApiNaming() : String = lowercase().replace(" ", "-")