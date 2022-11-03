package ru.iandreyshev.payfriends.domain.computationsList

import ru.iandreyshev.payfriends.domain.core.ComputationId
import ru.iandreyshev.payfriends.domain.time.Date

data class ComputationSummary(
    val id: ComputationId,
    val title: String,
    val date: Date
)
