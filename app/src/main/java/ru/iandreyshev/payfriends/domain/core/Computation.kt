package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class Computation(
    val id: ComputationId,
    val title: String,
    val bills: List<Bill>,
    val members: List<Member>,
    val creationDate: Date,
    val isCompleted: Boolean,
)
