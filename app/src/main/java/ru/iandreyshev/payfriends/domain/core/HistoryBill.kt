package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class HistoryBill(
    val transfers: List<HistoryTransfer>,
    val date: Date
)
