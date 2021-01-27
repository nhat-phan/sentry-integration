package net.ntworld.sentryIntegration.entity

interface PersistentEntity {
    fun getId(): String

    fun getType(): String

    fun getValue(): String

    fun <T> toEntity(value: String): T
}