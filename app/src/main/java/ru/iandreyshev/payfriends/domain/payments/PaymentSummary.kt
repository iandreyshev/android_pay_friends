package ru.iandreyshev.payfriends.domain.payments

import ru.iandreyshev.payfriends.domain.core.PaymentId

data class PaymentSummary(
    val id: PaymentId,
    val name: String
)
