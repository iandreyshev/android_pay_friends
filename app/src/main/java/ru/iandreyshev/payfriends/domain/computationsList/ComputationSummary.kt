package ru.iandreyshev.payfriends.domain.computationsList

import ru.iandreyshev.payfriends.domain.core.ComputationId

data class ComputationSummary(
    val id: ComputationId,
    val title: String
)
