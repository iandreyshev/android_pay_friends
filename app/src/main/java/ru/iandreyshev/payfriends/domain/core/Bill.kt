package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class Bill(
    val id: BillId,
    val title: String,
    val producer: Member,
    val payments: List<Payment>,
    val creationDate: Date
)
