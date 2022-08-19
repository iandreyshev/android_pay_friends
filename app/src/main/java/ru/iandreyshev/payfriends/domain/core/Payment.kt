package ru.iandreyshev.payfriends.domain.core

import ru.iandreyshev.payfriends.domain.time.Date

data class Payment(
    val id: PaymentId,
    val name: String,
    val members: List<Member>,
    val creationDate: Date,
    val isCompleted: Boolean,
    val transactions: List<Transaction>
)
