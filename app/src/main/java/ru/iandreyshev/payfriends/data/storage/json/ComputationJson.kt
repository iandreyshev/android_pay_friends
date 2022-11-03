package ru.iandreyshev.payfriends.data.storage.json

import kotlinx.serialization.Serializable

@Serializable
data class ComputationJson(
    var id: String,
    var title: String,
    val bills: MutableList<BillJson>,
    val members: MutableList<MemberJson>,
    var creationDate: String,
    var isCompleted: Boolean
)
