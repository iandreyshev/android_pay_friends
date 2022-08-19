package ru.iandreyshev.stale.domain.core

import ru.iandreyshev.stale.domain.time.Date

data class Payment(
    val id: PaymentId,
    val name: String,
    val members: List<Member>,
    val creationDate: Date,
    val isCompleted: Boolean,
    val transactions: List<Transaction>
)
