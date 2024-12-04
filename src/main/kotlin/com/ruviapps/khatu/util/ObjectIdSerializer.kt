package com.ruviapps.khatu.util

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import org.bson.codecs.kotlinx.BsonDecoder

/**
 * A custom serializer for ObjectId
 */
object ObjectIdSerializer : KSerializer<String> {
    @OptIn(ExperimentalSerializationApi::class)
    override fun deserialize(decoder: Decoder): String {
        return when(decoder){
            is BsonDecoder -> decoder.decodeObjectId().toHexString()
            is JsonDecoder -> {
                when(val jsonElement =  decoder.decodeJsonElement()){
                   is JsonObject -> {
                       jsonElement["\$oid"]?.jsonPrimitive?.content ?: ""
                   }
                   else -> decoder.decodeString()
               }
            }
            else -> throw SerializationException("${decoder::class.java.name} is not supported for deserialization")
        }
    }

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor(
            "ObjectIdSerializer",
            PrimitiveKind.STRING
        )

    override fun serialize(encoder: Encoder, value: String) {
        when (encoder) {
            is JsonEncoder -> encoder.encodeString(value)
            else -> throw SerializationException("${encoder::class.java.name} is not supported for serialization")
        }
    }
}