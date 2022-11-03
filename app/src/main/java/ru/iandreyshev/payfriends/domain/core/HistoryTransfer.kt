package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class HistoryTransfer(
    val transfer: Transfer,
    val description: String,
    val date: Date
)
