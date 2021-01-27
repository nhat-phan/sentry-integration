package net.ntworld.sentryIntegration.entity

import kotlinx.serialization.Decoder
import kotlinx.serialization.Encoder
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialDescriptor
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.internal.StringDescriptor

@Serializable
data class SentryIssueStat(
    val type: Type,
    val items: List<Item>
) {
    @Serializable
    data class Item(
        val start: Long,
        val end: Long,
        val count: Int
    )

    enum class Type {
        TWENTY_FOUR_HOURS,
        THIRTY_DAYS
    }

    @Serializer(forClass = SentryIssueStat.Type::class)
    object TypeSerializer : KSerializer<SentryIssueStat.Type> {
        override val descriptor: SerialDescriptor = StringDescriptor

        override fun serialize(output: Encoder, obj: SentryIssueStat.Type) {
            output.encodeString(obj.toString().toLowerCase())
        }

        override fun deserialize(input: Decoder): SentryIssueStat.Type {
            return Type.valueOf(input.decodeString().toUpperCase())
        }
    }

}