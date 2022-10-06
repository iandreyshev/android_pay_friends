package ru.iandreyshev.payfriends.data.storage.json

import kotlinx.serialization.Serializable

@Serializable
data class ComputationJson(
    val id: String,
    val title: String,
    val bills: MutableList<BillJson>,
    val members: MutableList<MemberJson>,
    val creationDate: String,
    val isCompleted: Boolean
)
