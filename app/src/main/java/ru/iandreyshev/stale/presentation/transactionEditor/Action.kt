package ru.iandreyshev.stale.presentation.transactionEditor

import ru.iandreyshev.stale.domain.core.PaymentId
import ru.iandreyshev.stale.domain.core.TransactionId

sealed interface Action {
    data class OnStart(
        val paymentId: PaymentId,
        val transactionId: TransactionId?
    ) : Action
}
