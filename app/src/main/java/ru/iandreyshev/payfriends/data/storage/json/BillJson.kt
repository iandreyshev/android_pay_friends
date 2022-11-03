package ru.iandreyshev.payfriends.data.storage.json

import kotlinx.serialization.Serializable

@Serializable
data class BillJson(
    var id: String,
    var title: String,
    var backer: MemberJson,
    val payments: MutableList<PaymentJson>,
    var creationDate: String
)
