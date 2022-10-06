package ru.iandreyshev.payfriends.data.storage.json

import kotlinx.serialization.Serializable

@Serializable
data class BillJson(
    val id: String,
    val title: String,
    val backer: MemberJson,
    val payments: MutableList<PaymentJson>,
    val creationDate: String
)
