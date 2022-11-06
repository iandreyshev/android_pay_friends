package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class HistoryBill(
    val id: BillId,
    val transfers: List<HistoryTransfer>,
    val date: Date
)
