package ru.iandreyshev.payfriends.presentation.transactionEditor

import ru.iandreyshev.payfriends.domain.core.PaymentId
import ru.iandreyshev.payfriends.domain.core.TransactionId

sealed interface Action {
    data class OnStart(
        val paymentId: PaymentId,
        val transactionId: TransactionId?
    ) : Action
}
