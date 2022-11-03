package ru.iandreyshev.payfriends.data.storage.json

import kotlinx.serialization.Serializable

@Serializable
data class PaymentJson(
    var id: String,
    var receiver: MemberJson,
    var cost: Int,
    var description: String,
    var creationDate: String
)
