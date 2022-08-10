package ru.iandreyshev.stale.domain.payments

import ru.iandreyshev.stale.domain.core.PaymentId

data class PaymentSummary(
    val id: PaymentId,
    val name: String
)
