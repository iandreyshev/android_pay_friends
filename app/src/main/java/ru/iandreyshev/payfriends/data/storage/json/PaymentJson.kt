package ru.iandreyshev.payfriends.data.storage.json

import kotlinx.serialization.Serializable

@Serializable
data class PaymentJson(
    val id: String,
    val receiver: MemberJson,
    val cost: Int,
    val description: String,
    val creationDate: String
)
